package com.example.virtual_account.service.createva;

import java.util.Map;

import com.example.virtual_account.constant.ErrorCode;
import com.example.virtual_account.exception.VirtualAccountException;

public class CreateVaFactory {
    public static final Map<String, CreateVaStrategy> strategies = Map.of(
            "VPBANK", new CreateVaVPBANKStrategy(),
            "BIDV", new CreateVaBIDVStrategy());

    public static CreateVaStrategy get(String bankCode) {
        CreateVaStrategy strategy = strategies.get(bankCode);
        if (strategy == null) {
            throw new VirtualAccountException(ErrorCode.VIRTUAL_ACCOUNT_BANK_NOT_SUPPORTED);
        }
        return strategy;
    }
}
