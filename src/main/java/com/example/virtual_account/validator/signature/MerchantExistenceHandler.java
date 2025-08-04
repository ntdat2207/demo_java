package com.example.virtual_account.validator.signature;

import com.example.virtual_account.constant.ErrorCode;
import com.example.virtual_account.constant.MerchantConstant;
import com.example.virtual_account.dto.request.BaseRequest;
import com.example.virtual_account.entity.MerchantEntity;
import com.example.virtual_account.exception.MerchantException;
import com.example.virtual_account.repository.MerchantRepository;
import com.example.virtual_account.validator.filter.AbstractValidationHandler;

public class MerchantExistenceHandler extends AbstractValidationHandler {
    private final MerchantRepository merchantRepo;

    public MerchantExistenceHandler(MerchantRepository repo) {
        this.merchantRepo = repo;
    }

    @Override
    public void handle(BaseRequest requestData) {
        MerchantEntity merchant = merchantRepo.findByCode(requestData.getMerchantCode())
                .orElseThrow(() -> new MerchantException(ErrorCode.MERCHANT_NOT_FOUND));

        if (merchant.getStatus() != MerchantConstant.ACTIVE_STATUS) {
            throw new MerchantException(ErrorCode.MERCHANT_INACTIVE);
        }

        requestData.setMerchant(merchant);
        super.handle(requestData);
    }
}
