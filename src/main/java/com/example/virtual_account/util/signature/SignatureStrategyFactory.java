package com.example.virtual_account.util.signature;

import java.util.Map;

import com.example.virtual_account.constant.ErrorCode;
import com.example.virtual_account.exception.MerchantException;

public class SignatureStrategyFactory {
    private static final Map<String, SignatureStrategy> strategies = Map.of(
            "SHA256", new RSASignatureStrategy(),
            "ED25519", new ED25519SignatureStrategy());

    public static SignatureStrategy get(String algorithm) {
        SignatureStrategy strategy = strategies.get(algorithm);
        if (strategy == null) {
            throw new MerchantException(
                    ErrorCode.MERCHANT_ALGORITHM_NOT_SUPPORTED);
        }
        return strategy;
    }
}
