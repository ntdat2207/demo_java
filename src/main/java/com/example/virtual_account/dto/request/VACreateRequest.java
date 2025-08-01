package com.example.virtual_account.dto.request;

import java.time.LocalDateTime;

import com.example.virtual_account.constant.BankConstant;
import com.example.virtual_account.validator.afternow.AfterNow;
import com.example.virtual_account.validator.allowedintvalues.AllowedIntValues;
import com.example.virtual_account.validator.allowedstringvalues.AllowedStringValues;
import com.example.virtual_account.validator.existsindb.ExistsInDatabase;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.micrometer.common.lang.Nullable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class VACreateRequest {
    @NotEmpty
    @Size(max = 50, min = 1)
    @Pattern(regexp = "^[a-zA-Z0-9\\s']+$")
    @Schema(description = "Merchant name", example = "Example")
    String accountName;

    @NotNull
    @AllowedIntValues(value = { 1, 2 })
    @Schema(description = "Account type: 1 - Dynamic VA collects the exact amount once, 2 - Static VA collects the free amount, free number of times", allowableValues = {
            "1", "2" }, example = "1")
    int accountType;

    @NotNull
    @Min(1000)
    @Schema(description = "Amount in VND, min 1000", example = "1000000")
    Long amount;

    @NotBlank
    @Nullable
    @Schema(description = "Description of the virtual account", example = "Payment for order ORDER_CODE_0001")
    String description;

    @NotEmpty
    @Size(max = 50, min = 8)
    @Pattern(regexp = "^[a-zA-Z0-9_.-]+$")
    @Schema(description = "Order code", example = "ORDER_CODE_0001")
    String orderCode;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(type = "string", example = "2025-07-25 14:00:00", description = "Expire time (format: yyyy-MM-dd HH:mm:ss)")
    @AfterNow
    LocalDateTime expiredAt;

    @NotEmpty
    @Schema(description = "Bank code, valid value: VPBANK, BIDV", example = "VPBANK")
    @AllowedStringValues(value = { "VPBANK", "BIDV" })
    @ExistsInDatabase(table = "banks", column = "bank_short_name", statusColumn = "status", statusValue = BankConstant.STATUS_ACTIVE, message = "Bank not found or inactive")
    String bankCode;
}
