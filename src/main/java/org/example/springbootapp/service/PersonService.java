package org.example.springbootapp.service;

import org.example.springbootapp.domain.Company;
import org.example.springbootapp.domain.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

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
}
