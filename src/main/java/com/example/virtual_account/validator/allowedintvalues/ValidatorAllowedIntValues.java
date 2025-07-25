package com.example.virtual_account.validator.allowedintvalues;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.HashSet;
import java.util.Set;

public class ValidatorAllowedIntValues implements ConstraintValidator<AllowedIntValues, Integer> {

    private Set<Integer> allowedValues;

    @Override
    public void initialize(AllowedIntValues constraintAnnotation) {
        allowedValues = new HashSet<>();
        for (int val : constraintAnnotation.value()) {
            allowedValues.add(val);
        }
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if (value == null)
            return false;
        return allowedValues.contains(value);
    }

}
