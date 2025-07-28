package com.example.virtual_account.validator.existsindb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import io.micrometer.common.util.StringUtils;
import jakarta.validation.ConstraintValidator;

public class ExistsInDatabaseValidator implements ConstraintValidator<ExistsInDatabase, String> {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private String table;
    private String column;
    private String statusColumn;
    private int statusValue;

    @Override
    public void initialize(ExistsInDatabase constraintAnnotation) {
        this.table = constraintAnnotation.table();
        this.column = constraintAnnotation.column();
        this.statusColumn = constraintAnnotation.statusColumn();
        this.statusValue = constraintAnnotation.statusValue();
    }

    @Override
    public boolean isValid(String value, jakarta.validation.ConstraintValidatorContext context) {
        if (StringUtils.isEmpty(value))
            return true; // để @NotEmpty xử lý

        String sql;
        Object[] params;

        if (!statusColumn.isEmpty()) {
            sql = String.format("SELECT COUNT(1) FROM %s WHERE %s = ? AND %s = ?", table, column, statusColumn);
            params = new Object[] { value, statusValue };
        } else {
            sql = String.format("SELECT COUNT(1) FROM %s WHERE %s = ?", table, column);
            params = new Object[] { value };
        }

        Integer count = jdbcTemplate.queryForObject(
                sql,
                Integer.class,
                params);
        return count != null && count > 0;
    }

}
