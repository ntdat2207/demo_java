package com.example.virtual_account.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.virtual_account.entity.BankEntity;

public interface BankRepository extends JpaRepository<BankEntity, Long> {
    // Additional query methods can be defined here if needed
    BankEntity findByBankShortName(String bankShortName);
}
