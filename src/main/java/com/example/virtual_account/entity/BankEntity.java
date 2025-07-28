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
@Table(name = "banks")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BankEntity extends BaseEntity {
    String bankNo;
    String bankName;
    int transferViaAccountNo;
    int transferViaCardNo;
    int status;
    int isDeleted;
    String bankShortName;
    String sacombankBankName;
    String citadBankCode;
    String citadVietinbankCode;
}
