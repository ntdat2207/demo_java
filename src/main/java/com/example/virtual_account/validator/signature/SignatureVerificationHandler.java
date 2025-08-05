package com.example.virtual_account.validator.signature;

import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.util.Base64;

import com.example.virtual_account.constant.ErrorCode;
import com.example.virtual_account.dto.request.BaseRequest;
import com.example.virtual_account.entity.KeyEntity;
import com.example.virtual_account.exception.MerchantException;
import com.example.virtual_account.repository.KeyRepository;
import com.example.virtual_account.util.signature.SignatureStrategy;
import com.example.virtual_account.util.signature.SignatureStrategyFactory;
import com.example.virtual_account.validator.filter.AbstractValidationHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SignatureVerificationHandler extends AbstractValidationHandler {
    private final KeyRepository keyRepo;

    public SignatureVerificationHandler(KeyRepository repo) {
        this.keyRepo = repo;
    }

    @Override
    public void handle(BaseRequest requestData) {
        KeyEntity key = keyRepo
                .findByMerchantIdAndAlgorithm(requestData.getMerchant().getId(), requestData.getAlgorithm())
                .orElseThrow(() -> new MerchantException(ErrorCode.MERCHANT_KEY_NOT_FOUND));

        try {
            SignatureStrategy strategy = SignatureStrategyFactory.get(requestData.getAlgorithm());
            PublicKey publicKey = strategy.loadPublicKey(key.getInfo());
            log.info("payload: {}", requestData.getPayload());

            boolean check = strategy.verify(
                    requestData.getPayload().getBytes(StandardCharsets.UTF_8),
                    Base64.getDecoder().decode(requestData.getSignature()),
                    publicKey);
            if (!check)
                throw new MerchantException(ErrorCode.MERCHANT_SIGNATURE_NOT_MATCH);
        } catch (Exception e) {
            log.error("Signature verification failed for merchant ID: {}", requestData.getMerchant().getId(), e);
            throw new MerchantException(ErrorCode.MERCHANT_SIGNATURE_NOT_MATCH);
        }

        super.handle(requestData);
    }
}
