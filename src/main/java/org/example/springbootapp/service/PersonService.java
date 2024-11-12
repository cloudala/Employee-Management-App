package org.example.springbootapp.service;

import org.example.springbootapp.domain.Company;
import org.example.springbootapp.domain.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PersonService {
    @Autowired
    private Person president;

    @Autowired
    private Person vicePresident;

    @Autowired
    private Person secretary;

    @Autowired
    private Company company;


    public void displayKeyEmployees() {
        System.out.println("President: " + president);
        System.out.println("Vice President: " + vicePresident);
        System.out.println("Secretary: " + secretary);
    }

    public List<Person> getAllEmployees() {
        return company.getAllEmployees();
    }

    public List<Person> filterByCompany(String companyName) {
        return company.filterByCompany(companyName);
    }

    public List<Person> sortByLastName() {
        return company.sortByLastName();
    }

    public Optional<Person> getEmployeeById(int id) {
        return company.getEmployeeById(id);
    }

    public Person addEmployee(Person newEmployee) {
        return company.addEmployee(newEmployee);
    }

    public Person updateEmployee(int id, Person newEmployee) {
        return company.updateEmployee(id, newEmployee);
    }

    public boolean deleteEmployee(int id) {
        return company.deleteEmployee(id);
    }

    public  List<String> getEmployeeCountries() {
        return company.getEmployeeCountries();
    }

    public Map<String, Double> getCurrencySalaryPairs() {
        return company.getCurrencySalaryPairs();
    }

    public List<Person> getEmployeesFromCountry(String country) {
        return company.getEmployeesFromCountry(country);
    }
}
