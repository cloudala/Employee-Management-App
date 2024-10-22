package org.example.springbootapp;

import org.example.springbootapp.domain.Person;
import org.example.springbootapp.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ImportResource;

import java.io.IOException;
import java.util.List;

@ImportResource("classpath:beans.xml")
@SpringBootApplication
public class SpringbootappApplication {
	
	@Autowired
	private PersonService personService;

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(SpringbootappApplication.class, args);
		PersonService personService = context.getBean(PersonService.class);
		personService.displayKeyEmployees();
		String csvFilePath = "MOCK_DATA.csv";
		try {
			personService.loadEmployeesFromCSV(csvFilePath);
		} catch (IOException e) {
			System.err.println("Error loading employees from CSV: " + e.getMessage());
			return;
		}

		System.out.println("\nAll Employees:");
		List<Person> allEmployees = personService.getAllEmployees();
		allEmployees.forEach(System.out::println);

		System.out.println("\nEmployees from Google:");
		List<Person> googleEmployees = personService.filterByCompany("Google");
		googleEmployees.forEach(System.out::println);

		System.out.println("\nEmployees sorted by last name:");
		List<Person> sortedByLastName = personService.sortByLastName();
		sortedByLastName.forEach(System.out::println);
	}
}
