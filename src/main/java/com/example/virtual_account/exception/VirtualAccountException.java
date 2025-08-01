package com.example.virtual_account.exception;

import com.example.virtual_account.constant.ErrorCode;

public class VirtualAccountException extends RuntimeException {
    private ErrorCode errorCode;

    public VirtualAccountException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

}
