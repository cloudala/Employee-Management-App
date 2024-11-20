package org.example.springbootapp.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.example.springbootapp.annotation.SalaryWithinBudget;
import org.example.springbootapp.domain.entity.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.example.springbootapp.service.PersonService;

import java.math.BigDecimal;

public class SalaryBudgetValidator implements ConstraintValidator<SalaryWithinBudget, Object> {

    @Autowired
    private PersonService personService;

    private String currencyField;
    private String salaryField;

    @Override
    public void initialize(SalaryWithinBudget constraintAnnotation) {
        this.currencyField = constraintAnnotation.currencyField();
        this.salaryField = constraintAnnotation.salaryField();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (!(value instanceof Person)) {
            return false;
        }

        Person person = (Person) value;
        String currency = person.getCurrency();
        BigDecimal salary = person.getSalary();

        if (currency == null || salary == null) {
            return false;
        }

        BigDecimal totalSalary = personService.getTotalSalaryByCurrency(currency);

        BigDecimal budget = new BigDecimal("25000000.00");

        boolean isValid = totalSalary.add(salary).compareTo(budget) <= 0;

        if (!isValid) {
            context.disableDefaultConstraintViolation(); // Disable default error message
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode(salaryField) // Bind error to salary field
                    .addConstraintViolation();
        }

        return isValid;
    }
}