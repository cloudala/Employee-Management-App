package org.example.springbootapp.controller;
import org.example.springbootapp.domain.Person;
import org.example.springbootapp.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

    @GetMapping("/welcome")
    public String welcomePage(Model model) {
        model.addAttribute("message", "Hello from Thymeleaf!");
        return "index"; // Thymeleaf will look for a template named "index.html"
    }

    @GetMapping
    public String listEmployees(@RequestParam(value = "country", required = false) String country, Model model) {
        List<Person> employees;
        if (country != null && !country.isEmpty()) {
            employees = personService.getEmployeesFromCountry(country);
        } else {
            employees = personService.getAllEmployees();
        }
        model.addAttribute("employees", employees);
        model.addAttribute("country", country);
        model.addAttribute("currencySummary", personService.getCurrencySalaryPairs());
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
}
