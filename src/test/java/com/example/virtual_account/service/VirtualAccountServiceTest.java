package com.example.virtual_account.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.Callable;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.virtual_account.constant.ErrorCode;
import com.example.virtual_account.constant.VirtualAccountConstant;
import com.example.virtual_account.dto.request.VACreateRequest;
import com.example.virtual_account.dto.request.VAUpdateRequest;
import com.example.virtual_account.dto.response.VACreateResponse;
import com.example.virtual_account.dto.response.VAUpdateResponse;
import com.example.virtual_account.entity.BankEntity;
import com.example.virtual_account.entity.MerchantEntity;
import com.example.virtual_account.entity.VirtualAccountEntity;
import com.example.virtual_account.entity.VirtualAccountRequestEntity;
import com.example.virtual_account.exception.VirtualAccountException;
import com.example.virtual_account.repository.BankRepository;
import com.example.virtual_account.repository.MerchantRepository;
import com.example.virtual_account.repository.VirtualAccountRepository;
import com.example.virtual_account.repository.VirtualAccountRequestRepository;
import com.example.virtual_account.service.createva.CreateVaFactory;
import com.example.virtual_account.service.createva.CreateVaStrategy;
import com.example.virtual_account.service.redis.RedisLockService;
import com.example.virtual_account.service.updateva.UpdateVaFactory;
import com.example.virtual_account.service.updateva.UpdateVaStrategy;
import com.example.virtual_account.util.signature.SignatureHeaderPaser;
import com.example.virtual_account.validator.signature.RequestValidator;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class VirtualAccountServiceTest {
    @Mock
    private RequestValidator requestValidator;

    @Mock
    private RedisLockService redisLockService;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private BankRepository bankRepository;

    @Mock
    private MerchantRepository merchantRepository;

    @Mock
    private CreateVaFactory createVaFactory;

    @Mock
    private VirtualAccountRequestRepository virtualAccountRequestRepository;

    @Mock
    private VirtualAccountRepository virtualAccountRepository;

    @Mock
    private CreateVaStrategy createVaStrategy;

    @Mock
    private UpdateVaFactory updateVaFactory;

    @InjectMocks
    private VirtualAccountService virtualAccountService;

    private VACreateRequest vaCreateRequest;
    private VAUpdateRequest vaUpdateRequest;
    private BankEntity bankEntity;
    private MerchantEntity merchantEntity;
    private VirtualAccountEntity virtualAccountEntity;
    private String merchantCode = "DATNT";
    private String signatureHeader = "algo=SHA256&signature=abc123xyz";
    private String orderCode = "ORDER_CODE_0002";
    private String bankCode = "VPBANK";
    private String account = "963336000011";

    @BeforeEach
    void setUp() {
        vaCreateRequest = new VACreateRequest();
        vaCreateRequest.setOrderCode(orderCode);
        vaCreateRequest.setBankCode(bankCode);

        vaUpdateRequest = new VAUpdateRequest();
        vaUpdateRequest.setOrderCode(orderCode);
        vaUpdateRequest.setAccount(account);

        bankEntity = new BankEntity();
        bankEntity.setBankShortName(bankCode);

        merchantEntity = new MerchantEntity();
        merchantEntity.setId(1L);
        merchantEntity.setCode(merchantCode);

        virtualAccountEntity = new VirtualAccountEntity();
        virtualAccountEntity.setAccount("VA123456");
        virtualAccountEntity.setName("Test VA");
        virtualAccountEntity.setAmount(100000L);
        virtualAccountEntity.setType(VirtualAccountConstant.TYPE_DYNAMIC);
        virtualAccountEntity.setExpiredAt(LocalDateTime.now());
        virtualAccountEntity.setOrderCode(orderCode);
        virtualAccountEntity.setStatus(VirtualAccountConstant.STATUS_ACTIVE);
    }

    @Test
    void testCreateVirtualAccount_Success() throws Exception {
        // Arrange
        when(redisLockService.executeWithLock(
                anyString(),
                anyLong(),
                ArgumentMatchers.<Callable<Object>>any())).thenAnswer(invocation -> {
                    Callable<?> callable = invocation.getArgument(2);
                    return callable.call();
                });

        try (var mockedStatic = Mockito.mockStatic(SignatureHeaderPaser.class)) {
            mockedStatic.when(() -> SignatureHeaderPaser.parse(signatureHeader))
                    .thenReturn(new SignatureHeaderPaser.ParsedSignature("SHA256", "abc123"));

            when(objectMapper.writeValueAsString(vaCreateRequest))
                    .thenReturn("{\"orderCode\":\"ORDER_CODE_0002\",\"bankCode\":\"VPBANK\"}");
            when(bankRepository.findByBankShortName(bankCode)).thenReturn(bankEntity);
            when(merchantRepository.findByCode(merchantCode)).thenReturn(Optional.of(merchantEntity));
            when(virtualAccountRequestRepository.findByOrderCode(orderCode)).thenReturn(Optional.empty());
            when(virtualAccountRequestRepository.save(any(VirtualAccountRequestEntity.class)))
                    .thenReturn(new VirtualAccountRequestEntity());
            when(createVaFactory.get(bankCode)).thenReturn(createVaStrategy);
            when(createVaStrategy.createVirtualAccount(vaCreateRequest, merchantEntity, bankEntity))
                    .thenReturn(virtualAccountEntity);

            // Act
            VACreateResponse response = virtualAccountService.createVirtualAccount(merchantCode,
                    signatureHeader,
                    vaCreateRequest);

            // Assert
            assertNotNull(response);
            assertEquals("VA123456", response.getAccount());
            assertEquals("Test VA", response.getName());
            assertEquals(100000, response.getAmount());
            assertEquals(VirtualAccountConstant.TYPE_DYNAMIC, response.getType());
            assertEquals(orderCode, response.getOrderCode());
            assertEquals(bankCode, response.getBankCode());
            assertEquals(VirtualAccountConstant.STATUS_ACTIVE, response.getStatus());
            verify(virtualAccountRequestRepository).save(any(VirtualAccountRequestEntity.class));
            verify(createVaStrategy).createVirtualAccount(vaCreateRequest, merchantEntity, bankEntity);
        }
    }

    @Test
    void testCreateVirtualAccount_DuplicateOrderCode() throws Exception {
        // Arrange
        when(redisLockService.executeWithLock(
                anyString(),
                anyLong(),
                ArgumentMatchers.<Callable<Object>>any())).thenAnswer(invocation -> {
                    Callable<?> callable = invocation.getArgument(2);
                    return callable.call();
                });
        when(objectMapper.writeValueAsString(vaCreateRequest))
                .thenReturn("{\"orderCode\":\"ORDER_CODE_0001\",\"bankCode\":\"VPBANK\"}");

        try (var mockedStatic = Mockito.mockStatic(SignatureHeaderPaser.class)) {
            mockedStatic.when(() -> SignatureHeaderPaser.parse(signatureHeader))
                    .thenReturn(new SignatureHeaderPaser.ParsedSignature("SHA256", "abc123"));

            when(bankRepository.findByBankShortName(bankCode)).thenReturn(bankEntity);
            when(merchantRepository.findByCode(merchantCode)).thenReturn(Optional.of(merchantEntity));
            when(virtualAccountRequestRepository.findByOrderCode(orderCode))
                    .thenReturn(Optional.of(new VirtualAccountRequestEntity()));

            // Act & Assert
            VirtualAccountException exception = assertThrows(VirtualAccountException.class,
                    () -> virtualAccountService.createVirtualAccount(merchantCode, signatureHeader,
                            vaCreateRequest));
            assertEquals(ErrorCode.VIRTUAL_ACCOUNT_ORDER_CODE_DUPLICATED, exception.getErrorCode());
        }
    }

    @Test
    void testUpdateVirtualAccount_Success() throws Exception {
        virtualAccountEntity.setBankId(20L);
        bankEntity.setId(20L);
        when(redisLockService.executeWithLock(
                anyString(),
                anyLong(),
                ArgumentMatchers.<Callable<Object>>any())).thenAnswer(invocation -> {
                    Callable<?> callable = invocation.getArgument(2);
                    return callable.call();
                });

        try (var mockedStatic = Mockito.mockStatic(SignatureHeaderPaser.class)) {
            mockedStatic.when(() -> SignatureHeaderPaser.parse(signatureHeader))
                    .thenReturn(new SignatureHeaderPaser.ParsedSignature("SHA256", "abc123"));

            when(virtualAccountRepository.findByOrderCodeAndAccount(orderCode, account))
                    .thenReturn(Optional.of(virtualAccountEntity));

            when(bankRepository.findById(20L)).thenReturn(Optional.of(bankEntity));

            UpdateVaStrategy updateVaStrategy = Mockito.mock(UpdateVaStrategy.class);
            when(updateVaFactory.get(anyString())).thenReturn(updateVaStrategy);
            when(updateVaStrategy.updateVirtualAccount(any(), any())).thenReturn(virtualAccountEntity);

            // Act
            VAUpdateResponse response = virtualAccountService.updateVirtualAccount(merchantCode,
                    signatureHeader,
                    vaUpdateRequest);

            // Assert
            assertNotNull(response);
            assertEquals("VA123456", response.getAccount());
            assertEquals("Test VA", response.getName());
            assertEquals(100000, response.getAmount());
            assertEquals(VirtualAccountConstant.TYPE_DYNAMIC, response.getType());
            assertEquals(orderCode, response.getOrderCode());
            assertEquals(bankCode, response.getBankCode());
            assertEquals(VirtualAccountConstant.STATUS_ACTIVE, response.getStatus());
            verify(updateVaStrategy).updateVirtualAccount(vaUpdateRequest, virtualAccountEntity);
        }
    }

    @Test
    void testUpdateVirtualAccount_NotFoundVA() throws Exception {
        virtualAccountEntity.setAccount(account);
        when(redisLockService.executeWithLock(
                anyString(),
                anyLong(),
                ArgumentMatchers.<Callable<Object>>any())).thenAnswer(invocation -> {
                    Callable<?> callable = invocation.getArgument(2);
                    return callable.call();
                });

        try (var mockedStatic = Mockito.mockStatic(SignatureHeaderPaser.class)) {
            mockedStatic.when(() -> SignatureHeaderPaser.parse(signatureHeader))
                    .thenReturn(new SignatureHeaderPaser.ParsedSignature("SHA256", "abc123"));

            // Act & Assert
            VirtualAccountException exception = assertThrows(VirtualAccountException.class,
                    () -> virtualAccountService.updateVirtualAccount(merchantCode, signatureHeader,
                            vaUpdateRequest));
            assertEquals(ErrorCode.VIRTUAL_ACCOUNT_NOT_FOUND, exception.getErrorCode());
        }
    }
}
