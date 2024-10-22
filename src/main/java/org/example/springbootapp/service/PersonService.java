package org.example.springbootapp.service;

import org.example.springbootapp.domain.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PersonService {
    @Autowired
    private Person president;

    @Autowired
    private Person vicePresident;

    @Autowired
    private Person secretary;

    private List<Person> employees = new ArrayList<>();

    public void displayKeyEmployees() {
        System.out.println("President: " + president);
        System.out.println("Vice President: " + vicePresident);
        System.out.println("Secretary: " + secretary);
    }

    public void loadEmployeesFromCSV(String filePath) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            // Omit the header
            String line = br.readLine();

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 6) {
                    String firstName = data[0];
                    String lastName = data[1];
                    String email = data[2];
                    String company = extractCompanyFromEmail(email);
                    Person employee = new Person(firstName, lastName, email, company);
                    employees.add(employee);
                }
            }
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
