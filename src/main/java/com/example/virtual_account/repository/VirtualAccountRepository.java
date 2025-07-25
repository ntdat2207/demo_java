package com.example.virtual_account.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.virtual_account.entity.VirtualAccountEntity;

public interface VirtualAccountRepository extends JpaRepository<VirtualAccountEntity, Long> {

}
