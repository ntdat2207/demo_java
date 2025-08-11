package com.example.virtual_account.dto.response.va;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class VACreateResponse {
    @JsonProperty("account")
    String account;

    @JsonProperty("name")
    String name;

    @JsonProperty("amount")
    Long amount;

    @JsonProperty("type")
    Integer type;

    @JsonProperty("order_code")
    String orderCode;

    @JsonProperty("status")
    Integer status;

    @JsonProperty("expired_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime expiredAt;

    @JsonProperty("bank_code")
    String bankCode;
}
