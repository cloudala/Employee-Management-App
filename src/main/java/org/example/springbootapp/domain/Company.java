package org.example.springbootapp.domain;

import org.example.springbootapp.domain.entity.Person;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.math.BigDecimal;

public class Company {
    private List<Person> employees = new ArrayList<>();

    public Company(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            // Omit the header
            String line = br.readLine();
            int id = 1;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 6) {
                    String firstName = data[0];
                    String lastName = data[1];
                    String email = data[2];
                    String company = extractCompanyFromEmail(email);
                    BigDecimal salary = new BigDecimal(data[3]);
                    String currency = data[4];
                    String country = data[5];
                    Person employee = new Person(id, firstName, lastName, email, company, salary, currency, country);
                    employees.add(employee);
                }
                id++;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String extractCompanyFromEmail(String email) {
        String domain = email.split("@")[1];
        String company = domain.split("\\.")[0];
        return company.substring(0, 1).toUpperCase() + company.substring(1).toLowerCase();
    }

    public List<Person> getAllEmployees() {
        return employees;
    }

    public Optional<Person> getEmployeeById(int id) {
        return employees.stream().filter(e -> e.getId() == id).findFirst();
    }

    public Person addEmployee(Person newEmployee) {
        newEmployee.setId(employees.size() + 1);
        newEmployee.setCurrency(newEmployee.getCurrency().toUpperCase());
        employees.add(newEmployee);
        return newEmployee;
    }

    public Person updateEmployee(int id, Person newEmployee) {
        Optional<Person> existingPerson = getEmployeeById(id);

        if (existingPerson.isPresent()) {
            Person person = existingPerson.get();
            person.setFirstName(newEmployee.getFirstName());
            person.setLastName(newEmployee.getLastName());
            person.setEmail(newEmployee.getEmail());
            person.setCompany(newEmployee.getCompany());
            person.setSalary(newEmployee.getSalary());
            person.setCurrency(newEmployee.getCurrency().toUpperCase());
            person.setCountry(newEmployee.getCountry());
            return person;
        } else {
            throw new NoSuchElementException("Person not found with ID: " + id);
        }
    }


    public boolean deleteEmployee(int id) {
        return employees.removeIf(e -> e.getId() == id);
    }

    public boolean existsByEmail(String email) {
        return employees.stream().anyMatch(employee -> employee.getEmail().equals(email));
    }

    public List<Person> filterByCompany(String company) {
        return employees.stream()
                .filter(employee -> employee.getCompany().equalsIgnoreCase(company))
                .collect(Collectors.toList());
    }

    public List<Person> sortByLastName() {
        return employees.stream()
                .sorted((p1, p2) -> p1.getLastName().compareToIgnoreCase(p2.getLastName()))
                .collect(Collectors.toList());
    }

    public List<String> getEmployeeCountries() {
        return employees.stream().map(Person::getCountry).distinct().sorted().collect(Collectors.toList());
    }

    public Map<String, BigDecimal> getCurrencySalaryPairs() {
        Map<String, BigDecimal> currencySums = new HashMap<>();
        List<AbstractMap.SimpleEntry<String, BigDecimal>> currencySalaryPairs =
                employees.stream()
                        .map(person -> new AbstractMap.SimpleEntry<>(person.getCurrency(), person.getSalary()))
                        .collect(Collectors.toList());

        currencySalaryPairs.forEach(pair ->
                currencySums.put(pair.getKey(),
                        currencySums.getOrDefault(pair.getKey(), BigDecimal.ZERO).add(pair.getValue()))
        );

        return currencySums;
    }

    public BigDecimal getTotalSalaryByCurrency(String currency) {
        return employees.stream().filter(employee -> employee.getCurrency()
                .equalsIgnoreCase(currency)).map(Person::getSalary)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<Person> getEmployeesFromCountry(String country) {
        return employees.stream().
                filter(employee -> employee.getCountry().equalsIgnoreCase(country))
                .collect(Collectors.toList());
    }
}
