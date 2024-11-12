package org.example.springbootapp.domain;

public class Person {
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String company;
    private Double salary;
    private String currency;
    private String country;

    public Person() {
        System.out.println("Creating person " + this);
    }

    public Person(int id, String firstName, String lastName, String email, String company, Double salary, String currency, String country) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.company = company;
        this.salary = salary;
        this.currency = currency;
        this.country = country;
        System.out.println("Creating person " + this);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double salary) {
        this.salary = salary;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", firstName=" + firstName +
                ", lastName=" + lastName +
                ", email=" + email +
                ", company=" + company +
                ", salary=" + salary +
                ", currency=" + currency +
                ", country=" + country +
                '}';
    }
}
