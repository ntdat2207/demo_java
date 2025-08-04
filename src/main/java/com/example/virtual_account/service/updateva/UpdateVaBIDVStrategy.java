package com.example.virtual_account.service.updateva;

import org.springframework.stereotype.Component;

import com.example.virtual_account.dto.request.VAUpdateRequest;
import com.example.virtual_account.entity.VirtualAccountEntity;
import com.example.virtual_account.repository.VirtualAccountRepository;

@Component("BIDV_UPDATE")
public class UpdateVaBIDVStrategy implements UpdateVaStrategy {

    private VirtualAccountRepository virtualAccountRepository;

    public UpdateVaBIDVStrategy(
            VirtualAccountRepository virtualAccountRepository) {
        this.virtualAccountRepository = virtualAccountRepository;
    }

    @Override
    public VirtualAccountEntity updateVirtualAccount(VAUpdateRequest request,
            VirtualAccountEntity virtualAccountEntity) {

        virtualAccountEntity.setAmount(request.getAmount());
        virtualAccountEntity.setName(request.getAccountName());
        virtualAccountEntity.setDescription(request.getDescription());
        virtualAccountEntity.setExpiredAt(request.getExpiredAt());

        virtualAccountRepository.save(virtualAccountEntity);
        return virtualAccountEntity;
    }

}
