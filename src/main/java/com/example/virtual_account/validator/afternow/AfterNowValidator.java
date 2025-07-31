package com.example.virtual_account.validator.afternow;

import java.time.LocalDateTime;

import jakarta.validation.ConstraintValidator;

public class AfterNowValidator implements ConstraintValidator<AfterNow, LocalDateTime> {

    @Override
    public void initialize(AfterNow constraintAnnotation) {
        // Initialization logic if needed
    }

    @Override
    public boolean isValid(LocalDateTime value, jakarta.validation.ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }

        return value.isAfter(LocalDateTime.now());
    }

}
