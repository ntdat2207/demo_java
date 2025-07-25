package com.example.virtual_account.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.virtual_account.entity.VirtualAccountRequest;

public interface VirtualAccountRequestRepository extends JpaRepository<VirtualAccountRequest, Long> {

}
