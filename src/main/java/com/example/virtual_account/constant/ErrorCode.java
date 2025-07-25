package com.example.virtual_account.constant;

import lombok.Getter;

@Getter
public enum ErrorCode {
    SYSTEM_ERROR(ResponseCode.SYSTEM_ERROR, "System error"),
    VALIDATE_ERROR(ResponseCode.VALIDATE_ERROR, "Validation error"),
    SUCCESS(ResponseCode.SUCCESS, "Success"),
    MERCHANT_NOT_FOUND(ResponseCode.MERCHANT_NOT_FOUND, "Merchant not found"),
    MERCHANT_INACTIVE(ResponseCode.MERCHANT_INACTIVE, "Merchant is not active"),
    MERCHANT_KEY_NOT_FOUND(ResponseCode.MERCHANT_KEY_NOT_FOUND, "Merchant key not found"),
    MERCHANT_ALGORITHM_NOT_SUPPORTED(ResponseCode.MERCHANT_ALGORITHM_NOT_SUPPORTED, "Merchant algorithm not supported"),
    MERCHANT_SIGNATURE_NOT_MATCH(ResponseCode.MERCHANT_SIGNATURE_NOT_MATCH, "Merchant signature does not match"),
    MERCHANT_SIGNATURE_FORMAT_NOT_SUPPORTED(ResponseCode.MERCHANT_SIGNATURE_FORMAT_NOT_SUPPORTED,
            "Merchant signature format not supported");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
