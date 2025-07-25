package com.example.virtual_account.validator.afternow;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import jakarta.validation.ConstraintValidator;

public class AfterNowValidator implements ConstraintValidator<AfterNow, String> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void initialize(AfterNow constraintAnnotation) {
        // Initialization logic if needed
    }

    @Override
    public boolean isValid(String value, jakarta.validation.ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }

        try {
            LocalDateTime inputTime = LocalDateTime.parse(value, FORMATTER);
            return inputTime.isAfter(LocalDateTime.now());
        } catch (DateTimeParseException ex) {
            return false;
        }
    }

}
