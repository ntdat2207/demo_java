package com.example.virtual_account.controller;

import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.virtual_account.dto.request.va.VACreateRequest;
import com.example.virtual_account.dto.request.va.VAGetRequest;
import com.example.virtual_account.dto.request.va.VAUpdateRequest;
import com.example.virtual_account.dto.response.ApiResponse;
import com.example.virtual_account.dto.response.va.VACreateResponse;
import com.example.virtual_account.dto.response.va.VAGetResponse;
import com.example.virtual_account.dto.response.va.VAUpdateResponse;
import com.example.virtual_account.service.VirtualAccountService;
import com.example.virtual_account.validator.signature.ValidateSignature;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/virtual-account")
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VirtualAccountController {
    VirtualAccountService virtualAccountService;

    @PostMapping("/create")
    @ValidateSignature
    public ApiResponse<VACreateResponse> createVirtualAccount(
            @RequestBody @Valid VACreateRequest request,
            HttpServletRequest httpRequest) {
        String merchantCode = httpRequest.getHeader("merchant_code");
        log.info("Creating virtual account...");

        ApiResponse<VACreateResponse> response = new ApiResponse<>();
        response.setData(virtualAccountService.createVirtualAccount(merchantCode, request));
        return response;
    }

    @PatchMapping("/update")
    @ValidateSignature
    public ApiResponse<VAUpdateResponse> updateVirtualAccount(
            @RequestBody @Valid VAUpdateRequest request,
            HttpServletRequest httpRequest) {
        String merchantCode = httpRequest.getHeader("merchant_code");
        log.info("Updating virtual account...");

        ApiResponse<VAUpdateResponse> response = new ApiResponse<>();
        response.setData(virtualAccountService.updateVirtualAccount(merchantCode, request));
        return response;
    }

    @PostMapping("/get")
    @ValidateSignature
    public ApiResponse<VAGetResponse> getVirtualAccount(HttpServletRequest httpRequest,
            @RequestBody @Valid VAGetRequest request) {
        String orderCode = request.getOrderCode();
        String account = request.getAccount();
        ApiResponse<VAGetResponse> response = new ApiResponse<>();
        response.setData(virtualAccountService.getVirtualAccount(account, orderCode));
        return response;
    }
}