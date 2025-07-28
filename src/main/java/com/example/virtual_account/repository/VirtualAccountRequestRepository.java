package com.example.virtual_account.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.virtual_account.entity.VirtualAccountRequestEntity;

public interface VirtualAccountRequestRepository extends JpaRepository<VirtualAccountRequestEntity, Long> {
    Optional<VirtualAccountRequestEntity> findByOrderCode(String orderCode);
}
