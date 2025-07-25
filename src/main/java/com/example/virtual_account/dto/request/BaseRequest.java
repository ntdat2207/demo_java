package com.example.virtual_account.dto.request;

import com.example.virtual_account.entity.KeyEntity;
import com.example.virtual_account.entity.MerchantEntity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BaseRequest {
    String merchantCode;
    String algorithm;
    String signature;
    String payload;

    MerchantEntity merchant;
    KeyEntity merchantKey;
}
