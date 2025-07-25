package com.example.virtual_account.util.signature;

import com.example.virtual_account.constant.ErrorCode;
import com.example.virtual_account.exception.MerchantException;

public class SignatureHeaderPaser {
    public static ParsedSignature parse(String headerValue) {
        String[] parts = headerValue.split("&");
        String algo = null;
        String sig = null;

        for (String part : parts) {
            if (part.startsWith("algo=")) {
                algo = part.substring("algo=".length());
            } else if (part.startsWith("signature=")) {
                sig = part.substring("signature=".length());
            }
        }

        if (algo == null || sig == null) {
            throw new MerchantException(ErrorCode.MERCHANT_SIGNATURE_FORMAT_NOT_SUPPORTED);
        }

        return new ParsedSignature(algo, sig);
    }

    public record ParsedSignature(String algorithm, String signature) {
    }
}
