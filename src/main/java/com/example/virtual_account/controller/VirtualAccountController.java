package com.example.virtual_account.controller;

import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.virtual_account.dto.request.VACreateRequest;
import com.example.virtual_account.dto.request.VAUpdateRequest;
import com.example.virtual_account.dto.response.ApiResponse;
import com.example.virtual_account.dto.response.VACreateResponse;
import com.example.virtual_account.dto.response.VAUpdateResponse;
import com.example.virtual_account.service.VirtualAccountService;

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
    public ApiResponse<VACreateResponse> createVirtualAccount(
            @RequestBody @Valid VACreateRequest request,
            HttpServletRequest httpRequest) {
        String merchantCode = httpRequest.getHeader("merchant_code");
        String signatureHeader = httpRequest.getHeader("signature");
        log.info("Creating virtual account...");

        ApiResponse<VACreateResponse> response = new ApiResponse<>();
        response.setData(virtualAccountService.createVirtualAccount(merchantCode, signatureHeader, request));
        return response;
    }

    @PatchMapping("/update")
    public ApiResponse<VAUpdateResponse> updateVirtualAccount(
            @RequestBody @Valid VAUpdateRequest request,
            HttpServletRequest httpRequest) {
        String merchantCode = httpRequest.getHeader("merchant_code");
        String signatureHeader = httpRequest.getHeader("signature");
        log.info("Updating virtual account...");

        ApiResponse<VAUpdateResponse> response = new ApiResponse<>();
        response.setData(virtualAccountService.updateVirtualAccount(merchantCode, signatureHeader, request));
        return response;
    }
}