package org.example.springbootapp.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.example.springbootapp.annotation.UniqueEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.example.springbootapp.service.PersonService;

public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {

    @Autowired
    private PersonService personService;

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null || email.isEmpty()) {
            return true;
        }
        return !personService.existsByEmail(email);
    }
}

