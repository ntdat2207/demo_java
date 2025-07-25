package com.example.virtual_account.validator.filter;

import com.example.virtual_account.dto.request.BaseRequest;

public interface ValidationHandler {
    void setNext(ValidationHandler next);

    void handle(BaseRequest requestData);
}
