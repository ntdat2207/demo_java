package com.example.virtual_account.service;

import org.springframework.stereotype.Service;

import com.example.virtual_account.constant.ErrorCode;
import com.example.virtual_account.dto.request.BaseRequest;
import com.example.virtual_account.dto.request.VACreateRequest;
import com.example.virtual_account.dto.response.CreateVaResponse;
import com.example.virtual_account.exception.VirtualAccountException;
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

    public CreateVaResponse createVirtualAccount(String merchantCode, String signatureHeader, VACreateRequest payload) {
        log.info("Creating virtual account for merchant: {}", merchantCode);
        log.info("Signature header: {}", signatureHeader);
        log.info("Payload: {}", payload.toString());

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

        String lockKey = "lock:va:create:" + payload.getOrderCode();
        try {
            return redisLockService.executeWithLock(lockKey, 30, () -> {
                // Logic tạo virtual account
                log.info("Creating virtual account for orderCode={}", payload.getOrderCode());
                return new CreateVaResponse("virtual-account-id");
            });
        } catch (Exception e) {
            throw new VirtualAccountException(ErrorCode.VIRTUAL_ACCOUNT_IS_PROCESSING);
        }
    }
}
