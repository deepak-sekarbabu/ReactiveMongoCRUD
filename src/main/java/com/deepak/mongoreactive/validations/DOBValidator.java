package com.deepak.mongoreactive.validations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DOBValidator implements ConstraintValidator<ValidDOB, String> {
    private static final String DATE_FORMAT = "dd-MM-yyyy";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // null values are considered valid, adjust as per your use case
        }

        try {
            LocalDate.parse(value, DateTimeFormatter.ofPattern(DATE_FORMAT));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
