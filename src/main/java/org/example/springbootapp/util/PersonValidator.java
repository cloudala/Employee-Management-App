package org.example.springbootapp.util;

import org.example.springbootapp.domain.entity.Person;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;
import java.util.regex.Pattern;

public class PersonValidator {

    // List of validation messages
    public static List<String> validate(Person person, int rowNumber) {
        List<String> errors = new ArrayList<>();

        // Validate first name
        if (person.getFirstName() == null || person.getFirstName().isEmpty()) {
            errors.add("Row " + rowNumber + ": First name is required.");
        } else if (person.getFirstName().length() < 2 || person.getFirstName().length() > 50) {
            errors.add("Row " + rowNumber + ": First name must be between 2 and 50 characters.");
        } else if (!Pattern.matches("^[a-zA-Z\\-]+$", person.getFirstName())) {
            errors.add("Row " + rowNumber + ": First name can only contain letters and hyphens.");
        }

        // Validate last name
        if (person.getLastName() == null || person.getLastName().isEmpty()) {
            errors.add("Row " + rowNumber + ": Last name is required.");
        } else if (person.getLastName().length() < 2 || person.getLastName().length() > 50) {
            errors.add("Row " + rowNumber + ": Last name must be between 2 and 50 characters.");
        } else if (!Pattern.matches("^[a-zA-Z\\-'\\s]+$", person.getLastName())) { // Updated regex to allow apostrophes and spaces
            errors.add("Row " + rowNumber + ": Last name can only contain letters, hyphens, apostrophes, and spaces.");
        }

        // Validate email
        if (person.getEmail() == null || person.getEmail().isEmpty()) {
            errors.add("Row " + rowNumber + ": Email is required.");
        } else if (!person.getEmail().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            errors.add("Row " + rowNumber + ": Invalid email format.");
        }

        // Validate salary
        if (person.getSalary() == null) {
            errors.add("Row " + rowNumber + ": Salary is required.");
        } else if (person.getSalary().compareTo(BigDecimal.ZERO) <= 0) {
            errors.add("Row " + rowNumber + ": Salary must be positive.");
        } else if (person.getSalary().compareTo(new BigDecimal("1000000")) > 0) {
            errors.add("Row " + rowNumber + ": Salary must not exceed 1,000,000.");
        }

        // Validate currency
        if (person.getCurrency() == null || person.getCurrency().isEmpty()) {
            errors.add("Row " + rowNumber + ": Currency is required.");
        } else if (!person.getCurrency().matches("^(JPY|EUR|GBP|USD)$")) {
            errors.add("Row " + rowNumber + ": Currency must be one of the following: JPY, EUR, GBP, USD.");
        }

        // Validate country
        if (person.getCountry() == null || person.getCountry().isEmpty()) {
            errors.add("Row " + rowNumber + ": Country is required.");
        }

        return errors;
    }
}
