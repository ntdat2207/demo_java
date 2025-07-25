package com.example.virtual_account.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
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
@Table(name = "mrc_keys")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KeyEntity extends BaseEntity {
    @Column(name = "merchant_id")
    Long merchantId;
    String algorithm;
    String info;
    int status;
    @Column(name = "actived_at")
    LocalDateTime activedAt;
}
