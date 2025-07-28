package com.example.virtual_account.validator.allowedstringvalues;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidatorAllowedStringValues implements ConstraintValidator<AllowedStringValues, String> {

    private Set<String> allowedValues;

    @Override
    public void initialize(AllowedStringValues constraintAnnotation) {
        allowedValues = new HashSet<>(Arrays.asList(constraintAnnotation.value()));
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return false;
        }
        return allowedValues.contains(value);
    }

}
