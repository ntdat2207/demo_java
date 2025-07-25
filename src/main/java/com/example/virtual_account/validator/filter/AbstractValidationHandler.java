package com.example.virtual_account.validator.filter;

import com.example.virtual_account.dto.request.BaseRequest;

public abstract class AbstractValidationHandler implements ValidationHandler {
    protected ValidationHandler next;

    @Override
    public void setNext(ValidationHandler next) {
        this.next = next;
    }

    @Override
    public void handle(BaseRequest requestData) {
        if (next != null)
            next.handle(requestData);
    }
}
