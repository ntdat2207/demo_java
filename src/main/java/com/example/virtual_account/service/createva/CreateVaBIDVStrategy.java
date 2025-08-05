package com.example.virtual_account.service.createva;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.virtual_account.constant.VirtualAccountConstant;
import com.example.virtual_account.dto.request.VACreateRequest;
import com.example.virtual_account.entity.BankEntity;
import com.example.virtual_account.entity.MerchantEntity;
import com.example.virtual_account.entity.VirtualAccountEntity;
import com.example.virtual_account.repository.VirtualAccountRepository;
import com.example.virtual_account.repository.VirtualAccountRequestRepository;
import com.example.virtual_account.service.GenVANumberService;

@Component("BIDV")
public class CreateVaBIDVStrategy implements CreateVaStrategy {
    private final String prefix = "963336";
    private final String redisKey = "BIDV-LENGTH-12"; // total length = 12, prefix = 6 -> suffix length = 6
    private final int suffixLength = 6;
    private final GenVANumberService vaNumberService;
    private final VirtualAccountRepository virtualAccountRepository;

    public CreateVaBIDVStrategy(GenVANumberService vaNumberService,
            VirtualAccountRepository virtualAccountRepository,
            VirtualAccountRequestRepository virtualAccountRequestRepository) {
        this.vaNumberService = vaNumberService;
        this.virtualAccountRepository = virtualAccountRepository;
    }

    @Override
    @Transactional
    public VirtualAccountEntity createVirtualAccount(VACreateRequest request, MerchantEntity merchant,
            BankEntity bank) {

        String account = vaNumberService.generate(prefix, redisKey, suffixLength);
        VirtualAccountEntity virtualAccount = VirtualAccountEntity.builder()
                .merchantId(merchant.getId())
                .bankId(bank.getId())
                .account(account)
                .name(request.getAccountName())
                .amount(request.getAmount())
                .expiredAt(request.getExpiredAt())
                .orderCode(request.getOrderCode())
                .status(VirtualAccountConstant.STATUS_ACTIVE)
                .description(request.getDescription())
                .type(request.getAccountType())
                .build();

        virtualAccountRepository.save(virtualAccount);

        return virtualAccount;
    }
}
