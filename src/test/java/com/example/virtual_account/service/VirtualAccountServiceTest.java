package com.example.virtual_account.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
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
import com.example.virtual_account.dto.request.BaseRequest;
import com.example.virtual_account.dto.request.VACreateRequest;
import com.example.virtual_account.dto.response.CreateVaResponse;
import com.example.virtual_account.entity.BankEntity;
import com.example.virtual_account.entity.MerchantEntity;
import com.example.virtual_account.entity.VirtualAccountEntity;
import com.example.virtual_account.entity.VirtualAccountRequestEntity;
import com.example.virtual_account.exception.VirtualAccountException;
import com.example.virtual_account.repository.BankRepository;
import com.example.virtual_account.repository.MerchantRepository;
import com.example.virtual_account.repository.VirtualAccountRequestRepository;
import com.example.virtual_account.service.createva.CreateVaFactory;
import com.example.virtual_account.service.createva.CreateVaStrategy;
import com.example.virtual_account.service.redis.RedisLockService;
import com.example.virtual_account.util.signature.SignatureHeaderPaser;
import com.example.virtual_account.validator.filter.RequestValidator;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
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
        private CreateVaStrategy createVaStrategy;

        @InjectMocks
        private VirtualAccountService virtualAccountService;

        private VACreateRequest vaCreateRequest;
        private BankEntity bankEntity;
        private MerchantEntity merchantEntity;
        private VirtualAccountEntity virtualAccountEntity;
        private String merchantCode = "DATNT";
        private String signatureHeader = "algo=SHA256&signature=abc123xyz";
        private String orderCode = "ORDER_CODE_0002";
        private String bankCode = "VPBANK";

        @BeforeEach
        void setUp() {
                vaCreateRequest = new VACreateRequest();
                vaCreateRequest.setOrderCode(orderCode);
                vaCreateRequest.setBankCode(bankCode);

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

                        doNothing().when(requestValidator).validate(any(BaseRequest.class));
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
                        CreateVaResponse response = virtualAccountService.createVirtualAccount(merchantCode,
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

                        doNothing().when(requestValidator).validate(any(BaseRequest.class));
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
}
