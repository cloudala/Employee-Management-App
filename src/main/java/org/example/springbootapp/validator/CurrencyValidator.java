package org.example.springbootapp.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.example.springbootapp.annotation.CurrencyConstraint;

import java.util.Arrays;
import java.util.List;

public class CurrencyValidator implements ConstraintValidator<CurrencyConstraint, String> {

    private static final List<String> ALLOWED_CURRENCIES = Arrays.asList("EUR", "USD", "GBP", "JPY");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value != null && ALLOWED_CURRENCIES.contains(value.toUpperCase());
    }
}

