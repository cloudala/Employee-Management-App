package org.example.springbootapp.domain;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

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
                    Person employee = new Person(id, firstName, lastName, email, company);
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
            return person;
        } else {
            throw new NoSuchElementException("Person not found with ID: " + id);
        }
    }


    public boolean deleteEmployee(int id) {
        return employees.removeIf(e -> e.getId() == id);
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
}
