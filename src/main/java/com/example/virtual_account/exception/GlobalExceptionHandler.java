package com.example.virtual_account.exception;

import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.example.virtual_account.constant.ErrorCode;
import com.example.virtual_account.dto.response.ApiResponse;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    @SuppressWarnings("rawtypes")
    @ExceptionHandler(value = MerchantException.class)
    ResponseEntity<ApiResponse> handlingMerchantException(MerchantException exception) {
        ErrorCode errorCode = exception.getErrorCode();

        ApiResponse apiResponse = new ApiResponse<>();
        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());
        return ResponseEntity.ok().body(apiResponse);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse<Object>> handlingMethodArgumentNotValidException(
            MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors()
                .stream()
                .map(error -> error.getField() + " invalid.")
                .collect(Collectors.joining("; "));

        ApiResponse<Object> apiResponse = new ApiResponse<>();
        apiResponse.setCode(ErrorCode.VALIDATE_ERROR.getCode());
        apiResponse.setMessage(message);
        return ResponseEntity.ok().body(apiResponse);
    }

    @ExceptionHandler(value = HttpMessageNotReadableException.class)

    ResponseEntity<ApiResponse<Object>> handlingHttpMessageNotReadableException(
            HttpMessageNotReadableException exception) {

        Throwable cause = exception.getCause();
        String message = "Invalid request format";
        if (cause instanceof InvalidFormatException invalidFormatException) {
            String fieldName = "";

            if (!invalidFormatException.getPath().isEmpty()) {
                fieldName = invalidFormatException.getPath().get(0).getFieldName();
                message = fieldName + " invalid";
            }
        }

        ApiResponse<Object> apiResponse = new ApiResponse<>();
        apiResponse.setCode(ErrorCode.VALIDATE_ERROR.getCode());
        apiResponse.setMessage(message);
        return ResponseEntity.ok().body(apiResponse);
    }

    @SuppressWarnings("rawtypes")
    @ExceptionHandler(value = VirtualAccountException.class)
    ResponseEntity<ApiResponse> handlingVirtualAccountException(VirtualAccountException exception) {
        ErrorCode errorCode = exception.getErrorCode();

        ApiResponse apiResponse = new ApiResponse<>();
        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());
        return ResponseEntity.ok().body(apiResponse);
    }
}
