package org.example.springbootapp.domain.entity;

import org.example.springbootapp.annotation.SalaryWithinBudget;
import org.example.springbootapp.validator.AddGroup;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.example.springbootapp.annotation.CurrencyConstraint;
import org.example.springbootapp.annotation.UniqueEmail;
import org.example.springbootapp.validator.UpdateGroup;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SalaryWithinBudget(
        groups = {AddGroup.class, UpdateGroup.class},
        currencyField = "currency",
        salaryField = "salary",
        message = "Total salary in the specified currency exceeds the company's budget!"
)
public class Person {

    private Integer id;

    @NotBlank(groups = {AddGroup.class, UpdateGroup.class}, message = "First name cannot be blank")
    @Size(groups = {AddGroup.class, UpdateGroup.class}, min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    @Pattern(groups = {AddGroup.class, UpdateGroup.class}, regexp = "^[a-zA-Z\\-]+$", message = "First name can only contain letters and hyphens")
    private String firstName;

    @NotBlank(groups = {AddGroup.class, UpdateGroup.class}, message = "Last name cannot be blank")
    @Size(groups = {AddGroup.class, UpdateGroup.class}, min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    @Pattern(groups = {AddGroup.class, UpdateGroup.class}, regexp = "^[a-zA-Z\\-]+$", message = "Last name can only contain letters and hyphens")
    private String lastName;

    @NotBlank(groups = {AddGroup.class, UpdateGroup.class}, message = "Email cannot be blank")
    @Email(groups = {AddGroup.class, UpdateGroup.class}, message = "Invalid email format")
    @UniqueEmail(groups = AddGroup.class, message = "Email address must be unique")
    private String email;

    @NotBlank(groups = {AddGroup.class, UpdateGroup.class}, message = "Company cannot be blank")
    private String company;

    @NotNull(groups = {AddGroup.class, UpdateGroup.class}, message = "Salary cannot be blank")
    @DecimalMin(groups = {AddGroup.class, UpdateGroup.class}, value = "0.01", message = "Salary must be positive")
    @Digits(groups = {AddGroup.class, UpdateGroup.class}, integer = 9, fraction = 2, message = "Salary must be a number with up to 2 decimal places")
    @DecimalMax(groups = {AddGroup.class, UpdateGroup.class}, value = "1000000", message = "Salary must not exceed 1,000,000")
    private BigDecimal salary;

    @NotBlank(groups = {AddGroup.class, UpdateGroup.class}, message = "Currency cannot be blank")
    @CurrencyConstraint(groups = {AddGroup.class, UpdateGroup.class}, message = "Currency must be one of the following: JPY, EUR, GBP, USD")
    private String currency;

    @NotBlank(groups = {AddGroup.class, UpdateGroup.class}, message = "Country cannot be blank")
    private String country;
}
