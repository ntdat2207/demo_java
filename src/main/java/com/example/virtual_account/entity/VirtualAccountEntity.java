package com.example.virtual_account.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "virtual_accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class VirtualAccountEntity extends BaseEntity {
    Long merchantId;

    @Column(name = "bank_id", insertable = false, updatable = false)
    Long bankId;
    String account;
    String name;
    Long amount;
    int type;
    String orderCode;
    int status;
    String description;
    LocalDateTime expiredAt;

    // Belongs to Bank
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_id", nullable = false)
    private BankEntity bank;
}
