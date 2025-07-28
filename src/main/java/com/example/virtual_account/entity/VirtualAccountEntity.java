package com.example.virtual_account.entity;

import jakarta.persistence.Entity;
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
    Long bankId;
    String account;
    String name;
    Long amount;
    int type;
    String orderCode;
    int status;
    String expiredAt;
}
