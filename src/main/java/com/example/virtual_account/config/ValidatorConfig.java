package com.example.virtual_account.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.virtual_account.repository.KeyRepository;
import com.example.virtual_account.repository.MerchantRepository;
import com.example.virtual_account.validator.filter.RequestValidator;

@Configuration
public class ValidatorConfig {
    @Bean
    public RequestValidator requestValidator(MerchantRepository merchantRepository, KeyRepository keyRepository) {
        return new RequestValidator(merchantRepository, keyRepository);
    }
}
