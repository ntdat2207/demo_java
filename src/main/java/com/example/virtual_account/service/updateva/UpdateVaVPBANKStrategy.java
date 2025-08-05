package com.example.virtual_account.service.updateva;

import org.springframework.stereotype.Component;

import com.example.virtual_account.constant.VirtualAccountConstant;
import com.example.virtual_account.dto.request.VAUpdateRequest;
import com.example.virtual_account.entity.VirtualAccountEntity;
import com.example.virtual_account.repository.VirtualAccountRepository;

@Component("VPBANK" + VirtualAccountConstant.UPDATE_VA_COMPONENT)
public class UpdateVaVPBANKStrategy implements UpdateVaStrategy {
    private final VirtualAccountRepository virtualAccountRepository;

    public UpdateVaVPBANKStrategy(
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
