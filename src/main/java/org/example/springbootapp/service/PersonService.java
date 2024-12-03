package org.example.springbootapp.service;

import org.example.springbootapp.domain.Company;
import org.example.springbootapp.domain.entity.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class PersonService {
    @Autowired
    private FileService fileService;

    @Autowired
    private Person president;

    @Autowired
    private Person vicePresident;

    @Autowired
    private Person secretary;

    @Autowired
    private Company company;

    public List<Person> loadEmployeesFromCsv(String filepath) {
        return company.loadEmployeesFromCSV(filepath);
    }

    public List<Person> loadEmployeesFromCsvWithValidation(String filepath) {
        return company.loadEmployeesFromCSVWithValidation(filepath);
    }

    public String collectValidationResults(List<String> validationResults) {
        return company.collectValidationResults(validationResults);
    }

    public List<String> getValidationErrors() {
        return company.getValidationErrors();
    }

    public void reloadEmployeesFromCsv(String fileName) {
        String fullFilePath = fileService.getPathInUploadDir(fileName);
        company.reloadEmployeesFromCSV(fullFilePath);
    }


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
        Optional<Person> existingPerson = getEmployeeById(id);

        if (existingPerson.isPresent()) {
            String oldImagePath = existingPerson.get().getImagePath();
            if (oldImagePath != null && !oldImagePath.isEmpty() &&
                    !oldImagePath.equals(newEmployee.getImagePath())) {
                fileService.deleteFile(oldImagePath);
            }
            return company.updateEmployee(id, newEmployee);
        } else {
            throw new NoSuchElementException("Person not found with ID: " + id);
        }
    }

    public boolean deleteEmployee(int id) {
        Optional<Person> employee = company.getEmployeeById(id);
        if (employee.isPresent()) {
            String imagePath = employee.get().getImagePath();
            if (imagePath != null && !imagePath.isEmpty()) {
                fileService.deleteFile(imagePath);
            }
            return company.deleteEmployee(id);
        }
        return false;
    }

    public boolean existsByEmail(String email) {
        return company.existsByEmail(email);
    }

    public  List<String> getEmployeeCountries() {
        return company.getEmployeeCountries();
    }

    public Map<String, BigDecimal> getCurrencySalaryPairs() {
        return company.getCurrencySalaryPairs();
    }

    public BigDecimal getTotalSalaryByCurrency(String currency) {
        return company.getTotalSalaryByCurrency(currency);
    }

    public List<Person> getEmployeesFromCountry(String country) {
        return company.getEmployeesFromCountry(country);
    }

    public Path exportPhotosToZip() throws IOException {
        return fileService.exportPhotosToZip("[id]_[surname].jpg", company.getAllEmployees());
    }

    public String exportEmployeesToCSV(List<String> columns) throws IOException {
        return company.exportEmployeesToCSV(columns);
    }
}
