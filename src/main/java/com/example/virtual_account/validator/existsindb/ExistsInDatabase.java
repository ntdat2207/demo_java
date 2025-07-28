package com.example.virtual_account.validator.existsindb;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.example.virtual_account.constant.BankConstant;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = ExistsInDatabaseValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ExistsInDatabase {
    String message() default "Value does not exist in database";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String table(); // tên bảng

    String column(); // tên cột

    String statusColumn() default ""; // cột trạng thái (optional)

    int statusValue() default BankConstant.STATUS_ACTIVE; // giá trị trạng thái cần match (optional)
}
