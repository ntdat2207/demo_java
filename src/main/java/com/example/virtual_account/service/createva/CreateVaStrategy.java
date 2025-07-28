package com.example.virtual_account.service.createva;

import com.example.virtual_account.dto.request.VACreateRequest;
import com.example.virtual_account.entity.BankEntity;
import com.example.virtual_account.entity.MerchantEntity;
import com.example.virtual_account.entity.VirtualAccountEntity;

public interface CreateVaStrategy {
    public VirtualAccountEntity createVirtualAccount(VACreateRequest request, MerchantEntity merchant, BankEntity bank);
}
