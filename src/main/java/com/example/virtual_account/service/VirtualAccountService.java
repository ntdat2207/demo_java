package com.example.virtual_account.service;

import org.springframework.stereotype.Service;

import com.example.virtual_account.dto.request.BaseRequest;
import com.example.virtual_account.dto.request.VACreateRequest;
import com.example.virtual_account.dto.response.CreateVaResponse;
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

    public CreateVaResponse createVirtualAccount(String merchantCode, String signatureHeader, VACreateRequest payload) {
        log.info("Creating virtual account for merchant: {}", merchantCode);
        log.info("Signature header: {}", signatureHeader);
        log.info("Payload: {}", payload.toString());

        SignatureHeaderPaser.ParsedSignature parsed = SignatureHeaderPaser.parse(signatureHeader);
        log.info("Parsed signature algorithm: {}", parsed.algorithm());
        log.info("Parsed signature value: {}", parsed.signature());

        String payloadStr = "{}";
        try {
            payloadStr = new ObjectMapper().writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            payloadStr = "{}"; // Fallback to empty JSON if serialization fails
        }

        BaseRequest requestData = new BaseRequest();
        requestData.setMerchantCode(merchantCode);
        requestData.setAlgorithm(parsed.algorithm());
        requestData.setSignature(parsed.signature());
        requestData.setPayload(payloadStr);

        requestValidator.validate(requestData);
        return new CreateVaResponse("virtual-account-id");
    }
}
