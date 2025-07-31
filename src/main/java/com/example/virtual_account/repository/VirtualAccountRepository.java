package com.example.virtual_account.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.virtual_account.entity.VirtualAccountEntity;

public interface VirtualAccountRepository extends JpaRepository<VirtualAccountEntity, Long> {
    @Query("SELECT v FROM VirtualAccountEntity v WHERE v.status = :status AND v.expiredAt BETWEEN :start AND :end")
    List<VirtualAccountEntity> findByStatusAndExpiredAt(int status, LocalDateTime start, LocalDateTime end);

}
