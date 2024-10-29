package org.example.springbootapp.service;

import org.example.springbootapp.domain.Company;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CompanyConfig {

    @Bean
    public Company company() {
        String filePath = "MOCK_DATA.csv";
        return new Company(filePath);
    }
}
