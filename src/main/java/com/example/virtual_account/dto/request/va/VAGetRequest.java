package com.example.virtual_account.dto.request.va;

import io.swagger.v3.oas.annotations.media.Schema;
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
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class VAGetRequest {
    @NotEmpty
    @Size(max = 50, min = 8)
    @Pattern(regexp = "^[a-zA-Z0-9_.-]+$")
    @Schema(description = "Order code", example = "ORDER_CODE_0001")
    String orderCode;

    @NotNull
    String account;
}
