package com.example.virtual_account.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.virtual_account.entity.KeyEntity;

public interface KeyRepository extends JpaRepository<KeyEntity, Long> {
    // Additional query methods can be defined here if needed
    Optional<KeyEntity> findByMerchantIdAndAlgorithm(Long mrcId, String algorithm);
}
