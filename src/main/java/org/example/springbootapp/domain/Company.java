package org.example.springbootapp.domain;

import org.example.springbootapp.util.CsvValidationResult;
import org.example.springbootapp.domain.entity.Person;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVFormat;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.util.*;
import java.util.stream.Collectors;
import java.math.BigDecimal;
import org.example.springbootapp.util.PersonValidator;

public class Company {
    private List<Person> employees = new ArrayList<>();
    List<String> validationErrors = new ArrayList<>();

    public Company(String filePath) {
        loadEmployeesFromCSVWithValidation(filePath);
    }

    public List<String> getValidationErrors() {
        return validationErrors;
    }

    public List<Person> loadEmployeesFromCSV(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            // Omit the header
            String line = br.readLine();
            int id = 1;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length < 6) {
                    throw new IllegalArgumentException("Invalid row format: " + line);
                }
                String firstName = data[0];
                String lastName = data[1];
                String imagePath = (data.length > 6 && !data[6].isEmpty()) ? data[6] : "";
                String email = data[2];
                String company = extractCompanyFromEmail(email);
                BigDecimal salary = new BigDecimal(data[3]);
                String currency = data[4];
                String country = data[5];
                Person employee = new Person(id, firstName, lastName, email, company, salary, currency, country, imagePath);
                employees.add(employee);
                id++;
            }
            return employees;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Person> loadEmployeesFromCSVWithValidation(String filePath) {
        List<String> currentValidationErrors = new ArrayList<>();
        List<Person> newEmployees = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            // Omit the header
            String line = br.readLine();
            int id = 0;

            while ((line = br.readLine()) != null) {
                id++; // Increment the row number for error messages
                String[] data = line.split(",");
                System.out.println("Row " + id + " data: " + Arrays.toString(data));

                if (data.length < 6) {
                    currentValidationErrors.add("Row " + id + ": Invalid column format, expected at least 6 columns but got " + data.length);
                    continue; // Skip invalid rows
                }

                try {
                    String firstName = data[0];
                    String lastName = data[1];
                    String imagePath = (data.length > 6 && !data[6].isEmpty()) ? data[6] : "";
                    String email = data[2];
                    String company = extractCompanyFromEmail(email);
                    BigDecimal salary = new BigDecimal(data[3]);
                    String currency = data[4];
                    String country = data[5];

                    Person employee = new Person(id, firstName, lastName, email, company, salary, currency, country, imagePath);

                    // Validate the employee using the Validator
                    List<String> errors = PersonValidator.validate(employee, id);

                    if (!errors.isEmpty()) {
                        String errorMessage = String.join("; ", errors);
                        currentValidationErrors.add(errorMessage);
                    } else {
                        newEmployees.add(employee); // Only add valid employees
                    }
                } catch (Exception e) {
                    currentValidationErrors.add("Row " + id + ": Error processing row: " + e.getMessage());
                }
            }

            if (currentValidationErrors.isEmpty()) {
                employees = newEmployees;
            } else {
                validationErrors = currentValidationErrors;
                System.out.println("Some data in the file was invalid, employee import failed.");
                printValidationResults(validationErrors);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading the CSV file: " + e.getMessage(), e);
        }

        return employees;
    }

    private void printValidationResults(List<String> validationResults) {
        System.out.println("Validation Errors:");
        for (String result : validationResults) {
            System.out.println(result);
        }
    }

    public String collectValidationResults(List<String> validationResults) {
        StringBuilder builder = new StringBuilder();
        builder.append("Validation Errors:\n");
        for (String result : validationResults) {
            builder.append(result).append("\n");
        }
        return builder.toString();
    }

    public void reloadEmployeesFromCSV(String filePath) {
        this.employees.clear();
        this.employees = loadEmployeesFromCSVWithValidation(filePath);
    }

    public static String extractCompanyFromEmail(String email) {
        if (email == null || !email.contains("@")) {
            return "Unknown"; // Return a default or error value
        }

        String[] splitEmail = email.split("@");
        if (splitEmail.length < 2) {
            return "Unknown"; // Handle malformed email
        }

        String domain = splitEmail[1];
        return domain.contains(".") ? domain.substring(0, domain.indexOf('.')) : domain;
    }

    public String exportEmployeesToCSV(List<String> columns) throws IOException {
        StringWriter stringWriter = new StringWriter();
        CSVPrinter csvPrinter = new CSVPrinter(stringWriter, CSVFormat.DEFAULT);

        // Add column names as the first row
        csvPrinter.printRecord(columns);
        for (Person employee : employees) {
            List<String> row = new ArrayList<>();
            for (String column : columns) {
                switch (column.toLowerCase()) {
                    case "first_name" -> row.add(employee.getFirstName());
                    case "last_name" -> row.add(employee.getLastName());
                    case "email" -> row.add(employee.getEmail());
                    case "company" -> row.add(employee.getCompany());
                    case "salary" -> row.add(employee.getSalary().toString());
                    case "currency" -> row.add(employee.getCurrency());
                    case "country" -> row.add(employee.getCountry());
                    case "image_path" -> row.add(employee.getImagePath());
                    default -> throw new IllegalArgumentException("Invalid column: " + column);
                }
            }
            csvPrinter.printRecord(row);
        }
        csvPrinter.flush();
        return stringWriter.toString();
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
            person.setImagePath(newEmployee.getImagePath());
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
