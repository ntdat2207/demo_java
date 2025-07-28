package com.example.virtual_account.service.createva;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.example.virtual_account.constant.ErrorCode;
import com.example.virtual_account.exception.VirtualAccountException;

@Component
public class CreateVaFactory {
    private final Map<String, CreateVaStrategy> strategies;

    public CreateVaFactory(Map<String, CreateVaStrategy> strategies) {
        this.strategies = strategies;
    }

    public CreateVaStrategy get(String bankCode) {
        CreateVaStrategy strategy = strategies.get(bankCode);
        if (strategy == null) {
            throw new VirtualAccountException(ErrorCode.VIRTUAL_ACCOUNT_BANK_NOT_SUPPORTED);
        }
        return strategy;
    }
}
