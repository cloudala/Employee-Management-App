package org.example.springbootapp.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.example.springbootapp.validator.CurrencyValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CurrencyValidator.class)
public @interface CurrencyConstraint {
    String message() default "Invalid currency, please choose one of the following:" +
            " \"EUR\", \"USD\", \"GBP\", \"JPY\"";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

