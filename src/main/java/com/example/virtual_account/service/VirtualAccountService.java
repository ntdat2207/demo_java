package com.example.virtual_account.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.virtual_account.constant.BankConstant;
import com.example.virtual_account.constant.ErrorCode;
import com.example.virtual_account.constant.VirtualAccountConstant;
import com.example.virtual_account.dto.request.va.VACreateRequest;
import com.example.virtual_account.dto.request.va.VAUpdateRequest;
import com.example.virtual_account.dto.response.va.VACreateResponse;
import com.example.virtual_account.dto.response.va.VAGetResponse;
import com.example.virtual_account.dto.response.va.VAUpdateResponse;
import com.example.virtual_account.entity.BankEntity;
import com.example.virtual_account.entity.MerchantEntity;
import com.example.virtual_account.entity.VirtualAccountEntity;
import com.example.virtual_account.entity.VirtualAccountRequestEntity;
import com.example.virtual_account.exception.MerchantException;
import com.example.virtual_account.exception.VirtualAccountException;
import com.example.virtual_account.repository.BankRepository;
import com.example.virtual_account.repository.MerchantRepository;
import com.example.virtual_account.repository.VirtualAccountRepository;
import com.example.virtual_account.repository.VirtualAccountRequestRepository;
import com.example.virtual_account.service.createva.CreateVaFactory;
import com.example.virtual_account.service.createva.CreateVaStrategy;
import com.example.virtual_account.service.redis.RedisLockService;
import com.example.virtual_account.service.updateva.UpdateVaFactory;
import com.example.virtual_account.service.updateva.UpdateVaStrategy;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class VirtualAccountService {
    private final RedisLockService redisLockService;
    private final ObjectMapper objectMapper;
    private final BankRepository bankRepository;
    private final MerchantRepository merchantRepository;
    private final CreateVaFactory createVaFactory;
    private final UpdateVaFactory updateVaFactory;
    private final VirtualAccountRequestRepository virtualAccountRequestRepository;
    private final VirtualAccountRepository virtualAccountRepository;

    public VACreateResponse createVirtualAccount(String merchantCode, VACreateRequest payload) {
        String lockKey = "lock:va:create:" + payload.getOrderCode();
        try {
            return redisLockService.executeWithLock(lockKey, 30, () -> {
                String payloadStr = "{}";
                try {
                    payloadStr = objectMapper.writeValueAsString(payload);
                } catch (JsonProcessingException e) {
                    payloadStr = "{}"; // Fallback to empty JSON if serialization fails
                    log.error("Failed to serialize payload to JSON", e);
                }

                log.info("Payload as JSON string: {}", payloadStr);

                log.info("Creating virtual account for orderCode={}", payload.getOrderCode());
                // Get Bank
                BankEntity bank = bankRepository.findByBankShortName(payload.getBankCode());
                MerchantEntity merchant = merchantRepository.findByCode(merchantCode)
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

                return VACreateResponse.builder()
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

    public VAUpdateResponse updateVirtualAccount(String merchantCode, VAUpdateRequest payload) {
        String lockKey = "lock:va:update:" + payload.getOrderCode();
        try {
            return redisLockService.executeWithLock(lockKey, 30, () -> {
                // Get existing virtual account
                Optional<VirtualAccountEntity> existingVa = virtualAccountRepository
                        .findByOrderCodeAndAccount(
                                payload.getOrderCode(), payload.getAccount());

                if (existingVa.isEmpty()) {
                    throw new VirtualAccountException(ErrorCode.VIRTUAL_ACCOUNT_NOT_FOUND);
                }

                VirtualAccountEntity vaEntity = existingVa.get();
                if (vaEntity.getStatus() != VirtualAccountConstant.STATUS_ACTIVE) {
                    throw new VirtualAccountException(ErrorCode.VIRTUAL_ACCOUNT_CANNOT_BE_UPDATED);
                }

                log.info("Updating virtual account for orderCode={}", payload.getOrderCode());
                // Get Bank
                BankEntity bank = bankRepository.findById(vaEntity.getBankId())
                        .orElseThrow(() -> new VirtualAccountException(ErrorCode.VIRTUAL_ACCOUNT_BANK_NOT_SUPPORTED));

                if (bank.getStatus() != BankConstant.STATUS_ACTIVE) {
                    throw new VirtualAccountException(ErrorCode.VIRTUAL_ACCOUNT_BANK_NOT_SUPPORTED);
                }

                // Update virtual account
                UpdateVaStrategy strategy = updateVaFactory
                        .get(bank.getBankShortName() + VirtualAccountConstant.UPDATE_VA_COMPONENT);
                VirtualAccountEntity virtualAccount = strategy.updateVirtualAccount(payload, vaEntity);

                return VAUpdateResponse.builder()
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

    public VAGetResponse getVirtualAccount(String account, String orderCode) {
        Optional<VirtualAccountEntity> existsVa = virtualAccountRepository.findByOrderCodeAndAccount(orderCode,
                account);
        if (existsVa.isEmpty()) {
            throw new VirtualAccountException(ErrorCode.VIRTUAL_ACCOUNT_NOT_FOUND);
        }

        VirtualAccountEntity vaEntity = existsVa.get();
        return VAGetResponse.builder()
                .account(account)
                .name(vaEntity.getName())
                .amount(vaEntity.getAmount())
                .type(vaEntity.getType())
                .orderCode(vaEntity.getOrderCode())
                .status(vaEntity.getStatus())
                .expiredAt(vaEntity.getExpiredAt())
                .bankCode(vaEntity.getBank().getBankShortName())
                .build();
    }
}
