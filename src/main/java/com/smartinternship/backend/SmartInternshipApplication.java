package com.smartinternship.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Smart Internship Tracker Backend Application.
 *
 * Package: com.smartinternship.backend
 *
 * @SpringBootApplication combines:
 *   - @Configuration: Marks this class as a source of bean definitions
 *   - @EnableAutoConfiguration: Auto-configures Spring Boot based on class-path dependencies
 *   - @ComponentScan: Scans controller, service, repository, config, security packages
 */
@SpringBootApplication
public class SmartInternshipApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartInternshipApplication.class, args);
    }
}
