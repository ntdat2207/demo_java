package com.example.virtual_account.validator.allowedstringvalues;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = ValidatorAllowedStringValues.class)
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface AllowedStringValues {
    String[] value();

    String message() default "Invalid value. Allowed values are: {value}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
