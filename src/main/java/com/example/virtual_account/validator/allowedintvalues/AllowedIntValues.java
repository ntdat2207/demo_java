package com.example.virtual_account.validator.allowedintvalues;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidatorAllowedIntValues.class)
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface AllowedIntValues {

    int[] value();

    String message() default "Invalid value. Allowed values are: {value}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
