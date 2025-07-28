package com.example.virtual_account.service;

import org.springframework.stereotype.Service;

import com.example.virtual_account.constant.ErrorCode;
import com.example.virtual_account.dto.request.BaseRequest;
import com.example.virtual_account.dto.request.VACreateRequest;
import com.example.virtual_account.dto.response.CreateVaResponse;
import com.example.virtual_account.entity.BankEntity;
import com.example.virtual_account.entity.MerchantEntity;
import com.example.virtual_account.entity.VirtualAccountEntity;
import com.example.virtual_account.entity.VirtualAccountRequestEntity;
import com.example.virtual_account.exception.MerchantException;
import com.example.virtual_account.exception.VirtualAccountException;
import com.example.virtual_account.repository.BankRepository;
import com.example.virtual_account.repository.MerchantRepository;
import com.example.virtual_account.repository.VirtualAccountRequestRepository;
import com.example.virtual_account.service.createva.CreateVaFactory;
import com.example.virtual_account.service.createva.CreateVaStrategy;
import com.example.virtual_account.util.signature.SignatureHeaderPaser;
import com.example.virtual_account.validator.filter.RequestValidator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class VirtualAccountService {
    private final RequestValidator requestValidator;
    private final RedisLockService redisLockService;
    private final ObjectMapper objectMapper;
    private final BankRepository bankRepository;
    private final MerchantRepository merchantRepository;
    private final CreateVaFactory createVaFactory;
    private final VirtualAccountRequestRepository virtualAccountRequestRepository;

    public CreateVaResponse createVirtualAccount(String merchantCode, String signatureHeader, VACreateRequest payload) {
        String lockKey = "lock:va:create:" + payload.getOrderCode();
        try {
            return redisLockService.executeWithLock(lockKey, 30, () -> {
                SignatureHeaderPaser.ParsedSignature parsed = SignatureHeaderPaser.parse(signatureHeader);
                log.info("Parsed signature algorithm: {}", parsed.algorithm());
                log.info("Parsed signature value: {}", parsed.signature());

                String payloadStr = "{}";
                try {
                    payloadStr = objectMapper.writeValueAsString(payload);
                } catch (JsonProcessingException e) {
                    payloadStr = "{}"; // Fallback to empty JSON if serialization fails
                    log.error("Failed to serialize payload to JSON", e);
                }

                log.info("Payload as JSON string: {}", payloadStr);

                BaseRequest requestData = new BaseRequest();
                requestData.setMerchantCode(merchantCode);
                requestData.setAlgorithm(parsed.algorithm());
                requestData.setSignature(parsed.signature());
                requestData.setPayload(payloadStr);

                requestValidator.validate(requestData);

                log.info("Creating virtual account for orderCode={}", payload.getOrderCode());
                // Get Bank
                BankEntity bank = bankRepository.findByBankShortName(payload.getBankCode());
                MerchantEntity merchant = merchantRepository.findByCode(requestData.getMerchantCode())
                        .orElseThrow(() -> new MerchantException(ErrorCode.MERCHANT_NOT_FOUND));

                // Save virtual account request
                if (virtualAccountRequestRepository.findByOrderCode(payload.getOrderCode()).isPresent()) {
                    throw new VirtualAccountException(ErrorCode.VIRTUAL_ACCOUNT_ORDER_CODE_DUPLICATED);
                }
                VirtualAccountRequestEntity vaRequest = VirtualAccountRequestEntity.builder()
                        .orderCode(payload.getOrderCode())
                        .merchantId(merchant.getId())
                        .request(payloadStr)
                        .build();
                virtualAccountRequestRepository.save(vaRequest);

                // Save virtual account
                CreateVaStrategy strategy = createVaFactory.get(payload.getBankCode());
                VirtualAccountEntity virtualAccount = strategy.createVirtualAccount(payload, merchant, bank);

                return CreateVaResponse.builder()
                        .account(virtualAccount.getAccount())
                        .name(virtualAccount.getName())
                        .amount(virtualAccount.getAmount())
                        .type(virtualAccount.getType())
                        .expiredAt(virtualAccount.getExpiredAt())
                        .orderCode(virtualAccount.getOrderCode())
                        .bankCode(bank.getBankShortName())
                        .status(virtualAccount.getStatus())
                        .build();
            });
        } catch (VirtualAccountException e) {
            // Nếu là lỗi nghiệp vụ như duplicated order code, giữ nguyên
            throw e;
        } catch (Exception e) {
            throw new VirtualAccountException(ErrorCode.VIRTUAL_ACCOUNT_IS_PROCESSING);
        }
    }
}
