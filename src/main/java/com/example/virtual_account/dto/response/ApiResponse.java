package com.example.virtual_account.dto.response;

import com.example.virtual_account.constant.ErrorCode;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
    T data;
    String message = ErrorCode.SUCCESS.getMessage();
    int code = ErrorCode.SUCCESS.getCode();
}
