package org.example.springbootapp.controller;
import org.example.springbootapp.domain.entity.Person;
import org.example.springbootapp.service.PersonService;
import org.example.springbootapp.validator.AddGroup;
import org.example.springbootapp.validator.UpdateGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/employees")
public class PersonViewController {
    private final PersonService personService;

    @Autowired
    public PersonViewController(PersonService personService) {
        this.personService = personService;
    }

    @ModelAttribute("totalEmployees")
    public int populateEmployeeCount() {
        return personService.getAllEmployees().size();
    }

    @ModelAttribute("employeeCountries")
    public List<String> populateEmployeeCountries() {
        return personService.getEmployeeCountries();
    }

    @ModelAttribute("currencySummary")
    public Map<String, BigDecimal> populateCurrencySummary() {
        return personService.getCurrencySalaryPairs();
    }

    @GetMapping
    public String listEmployees(@RequestParam(value = "filterCountry", required = false) String country, Model model) {
        List<Person> employees;
        if (country != null && !country.isEmpty()) {
            employees = personService.getEmployeesFromCountry(country);
        } else {
            employees = personService.getAllEmployees();
        }
        model.addAttribute("employees", employees);
        if (!model.containsAttribute("employee")) {
            model.addAttribute("employee", new Person());
        }
        model.addAttribute("country", country);
        return "employees/list";
    }

    @GetMapping("/details/{id}")
    public String employeeDetails(@PathVariable int id, Model model) {
        Optional<Person> employee = personService.getEmployeeById(id);
        if (employee.isPresent()) {
            model.addAttribute("employee", employee.get());
        } else {
            model.addAttribute("error", "Employee not found");
            return "employees/error";
        }
        return "employees/details";
    }

    @PostMapping("/add")
    public String addEmployee(@Validated(AddGroup.class) @ModelAttribute("employee") Person employee,
                             BindingResult result,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("employees", personService.getAllEmployees());
            model.addAttribute("errorMessage", "Adding new employee failed");
            return "employees/list";
        }

        try {
            personService.addEmployee(employee);
            redirectAttributes.addFlashAttribute("successMessage", "New employee has been added successfully");
        } catch (Exception e) {
            model.addAttribute("employees", personService.getAllEmployees());
            redirectAttributes.addFlashAttribute("errorMessage", "Adding new employee failed");
        }
        return "redirect:/employees";
    }

    @GetMapping("/edit/{id}")
    public String editEmployee(@PathVariable int id, Model model) {
        Optional<Person> employee = personService.getEmployeeById(id);
        if (employee.isPresent()) {
            model.addAttribute("employees", personService.getAllEmployees());
            model.addAttribute("employee", employee.get());
        } else {
            model.addAttribute("employees", personService.getAllEmployees());
            model.addAttribute("errorMessage", "Employee not found");
        }
        return "employees/list";
    }

    @PostMapping("/update/{id}")
    public String updateEmployee(@PathVariable("id") Integer id,
                                 @Validated(UpdateGroup.class) @ModelAttribute("employee") Person employee,
                                 BindingResult result,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("employees", personService.getAllEmployees());
            model.addAttribute("errorMessage", "Failed to update employee");
            return "employees/list";
        }
        try {
            employee.setId(id);
            personService.updateEmployee(id, employee);
            redirectAttributes.addFlashAttribute("successMessage", "Employee updated successfully");
        } catch (Exception e) {
            model.addAttribute("employees", personService.getAllEmployees());
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update employee");
        }
        return "redirect:/employees";
    }

    @PostMapping("/delete/{id}")
    public String deleteEmployee(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            personService.deleteEmployee(id);
            redirectAttributes.addFlashAttribute("successMessage", "Employee has been deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Deleting employee failed");
        }
        return "redirect:/employees";
    }
}
