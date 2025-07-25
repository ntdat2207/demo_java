package com.example.virtual_account.validator.afternow;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AfterNowValidator.class)
@Documented
public @interface AfterNow {
    String message() default "Thời gian phải lớn hơn hiện tại";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
