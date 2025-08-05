package com.example.virtual_account.validator.signature;

import com.example.virtual_account.dto.request.BaseRequest;
import com.example.virtual_account.repository.KeyRepository;
import com.example.virtual_account.repository.MerchantRepository;
import com.example.virtual_account.validator.filter.ValidationHandler;

public class RequestValidator {
    private final ValidationHandler chain;

    public RequestValidator(MerchantRepository mRepo, KeyRepository kRepo) {
        var merchantCheck = new MerchantExistenceHandler(mRepo);
        var sigCheck = new SignatureVerificationHandler(kRepo);
        merchantCheck.setNext(sigCheck);
        this.chain = merchantCheck;
    }

    public void validate(BaseRequest data) {
        chain.handle(data);
    }
}
