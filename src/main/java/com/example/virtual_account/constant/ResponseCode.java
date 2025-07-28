package com.example.virtual_account.constant;

import lombok.experimental.FieldDefaults;

@FieldDefaults(makeFinal = true)
public class ResponseCode {
    // System error: 500
    public static int SYSTEM_ERROR = 500;

    // Validate error: 400
    public static int VALIDATE_ERROR = 400;

    // Success: 200
    public static int SUCCESS = 200;

    // Another error: 1xxx
    // Merchant error: 10xx
    // Merchant not found: 1001
    public static int MERCHANT_NOT_FOUND = 1001;
    // Merchant is not active: 1002
    public static int MERCHANT_INACTIVE = 1002;
    // Merchant key not found: 1003
    public static int MERCHANT_KEY_NOT_FOUND = 1003;
    // Merchant algorithm not supported: 1004
    public static int MERCHANT_ALGORITHM_NOT_SUPPORTED = 1004;
    // Merchant signature not match: 1005
    public static int MERCHANT_SIGNATURE_NOT_MATCH = 1005;
    // Merchant signature format not supported: 1006
    public static int MERCHANT_SIGNATURE_FORMAT_NOT_SUPPORTED = 1006;

    // Create virtual account error: 11xx
    // Virtual account already exists: 1101
    public static int VIRTUAL_ACCOUNT_ALREADY_EXISTS = 1101;
    // Virtual account is processing: 1102
    public static int VIRTUAL_ACCOUNT_IS_PROCESSING = 1102;
    // Bank not supported: 1103
    public static int VIRTUAL_ACCOUNT_BANK_NOT_SUPPORTED = 1103;
}
