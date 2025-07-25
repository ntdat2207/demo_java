package com.example.virtual_account.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.virtual_account.entity.MerchantEntity;

public interface MerchantRepository extends JpaRepository<MerchantEntity, Long> {
    // Additional query methods can be defined here if needed
    Optional<MerchantEntity> findByCode(String code);
}
