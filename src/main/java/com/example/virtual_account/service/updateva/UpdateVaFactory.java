package com.example.virtual_account.service.updateva;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.example.virtual_account.constant.ErrorCode;
import com.example.virtual_account.exception.VirtualAccountException;

@Component
public class UpdateVaFactory {
    private final Map<String, UpdateVaStrategy> strategies;

    public UpdateVaFactory(Map<String, UpdateVaStrategy> strategies) {
        this.strategies = strategies;
    }

    public UpdateVaStrategy get(String bankCode) {
        UpdateVaStrategy strategy = strategies.get(bankCode);
        if (strategy == null) {
            throw new VirtualAccountException(ErrorCode.VIRTUAL_ACCOUNT_BANK_NOT_SUPPORTED);
        }
        return strategy;
    }
}
