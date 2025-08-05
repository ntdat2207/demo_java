package com.example.virtual_account.validator.signature;

import java.util.Arrays;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.example.virtual_account.constant.ErrorCode;
import com.example.virtual_account.dto.request.BaseRequest;
import com.example.virtual_account.exception.MerchantException;
import com.example.virtual_account.util.signature.SignatureHeaderPaser;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class SignatureValidationAspect {
    private final ObjectMapper objectMapper;
    private final RequestValidator requestValidator;

    @Around("@annotation(com.example.virtual_account.validator.signature.ValidateSignature)")
    public Object validateSignature(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("Vao day");
        HttpServletRequest httpRequest = getHttpServletRequest();
        String merchantCode = httpRequest.getHeader("merchant_code");
        String signatureHeader = httpRequest.getHeader("signature");

        if (merchantCode == null || signatureHeader == null) {
            throw new MerchantException(ErrorCode.HEADER_ERROR);
        }

        SignatureHeaderPaser.ParsedSignature parsed = SignatureHeaderPaser.parse(signatureHeader);

        Object[] args = joinPoint.getArgs();
        Object payloadObject = Arrays.stream(args)
                .filter(arg -> arg != null && arg.getClass().getSimpleName().endsWith("Request"))
                .findFirst()
                .orElseThrow(() -> new MerchantException(ErrorCode.MERCHANT_PAYLOAD_NOT_FOUND));

        String payloadStr = "{}";
        payloadStr = objectMapper.writeValueAsString(payloadObject);

        BaseRequest requestData = new BaseRequest();
        requestData.setMerchantCode(merchantCode);
        requestData.setAlgorithm(parsed.algorithm());
        requestData.setSignature(parsed.signature());
        requestData.setPayload(payloadStr);

        requestValidator.validate(requestData);

        // Gán merchant nếu cần truyền cho downstream
        if (payloadObject instanceof BaseRequest) {
            ((BaseRequest) payloadObject).setMerchant(requestData.getMerchant());
        }

        return joinPoint.proceed();
    }

    private HttpServletRequest getHttpServletRequest() {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attr == null)
            throw new RuntimeException("Cannot get request context");
        return attr.getRequest();
    }
}
