package org.example.springbootapp.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.example.springbootapp.validator.SalaryBudgetValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = SalaryBudgetValidator.class)
@Target({ ElementType.TYPE }) // Can apply to classes
@Retention(RetentionPolicy.RUNTIME)
public @interface SalaryWithinBudget {
    String message() default "The total salaries exceed the company budget for the given currency";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    String currencyField();
    String salaryField();
}

