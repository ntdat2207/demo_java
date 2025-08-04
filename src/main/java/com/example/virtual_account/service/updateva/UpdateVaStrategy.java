package com.example.virtual_account.service.updateva;

import com.example.virtual_account.dto.request.VAUpdateRequest;
import com.example.virtual_account.entity.VirtualAccountEntity;

public interface UpdateVaStrategy {
    public VirtualAccountEntity updateVirtualAccount(VAUpdateRequest request,
            VirtualAccountEntity virtualAccountEntity);
}
