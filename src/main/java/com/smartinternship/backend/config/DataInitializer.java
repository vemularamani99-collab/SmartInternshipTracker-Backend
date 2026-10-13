package com.smartinternship.backend.config;

import com.smartinternship.backend.model.Internship;
import com.smartinternship.backend.model.Role;
import com.smartinternship.backend.model.User;
import com.smartinternship.backend.repository.InternshipRepository;
import com.smartinternship.backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Initializes essential demo data and controlled Administrator account.
 *
 * SECURE INITIALIZATION:
 * Admin credentials are provided via environment variables (ADMIN_EMAIL, ADMIN_PASSWORD)
 * and hashed with BCrypt. No raw passwords are hardcoded in Java code.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final InternshipRepository internshipRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email:admin@example.com}")
    private String adminEmail;

    @Value("${app.admin.password:Admin@Secure2026}")
    private String adminPassword;

    @Value("${app.admin.name:Placement Administrator}")
    private String adminName;

    public DataInitializer(UserRepository userRepository,
                           InternshipRepository internshipRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.internshipRepository = internshipRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        initAdminAccount();
        seedDemoStudentIfEmpty();
        seedSampleInternshipsIfEmpty();
    }

    /**
     * Controlled initial Administrator account creation.
     * Only runs if no ADMIN account exists yet.
     */
    private void initAdminAccount() {
        if (!userRepository.existsByRole(Role.ADMIN)) {
            User admin = new User();
            admin.setName(adminName);
            admin.setEmail(adminEmail.trim().toLowerCase());
            // Hash the password with BCrypt before saving
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setRole(Role.ADMIN);
            admin.setCollege("Placement Cell");
            admin.setDegree("Administration & Recruitment");

            userRepository.save(admin);
            logger.info("Created initial Administrator account: {} (Configured via ADMIN_EMAIL/ADMIN_PASSWORD env vars)", adminEmail);
        }
    }

    /**
     * Seeds one demo student for testing if no student exists.
     */
    private void seedDemoStudentIfEmpty() {
        if (!userRepository.existsByRole(Role.STUDENT)) {
            User student = new User();
            student.setName("Alex Johnson");
            student.setEmail("student@example.com");
            student.setPassword(passwordEncoder.encode("password123"));
            student.setRole(Role.STUDENT);
            student.setCollege("National Institute of Technology");
            student.setDegree("B.Tech Computer Science");
            student.setGraduationYear(2026);

            userRepository.save(student);
            logger.info("Created demo Student account: student@example.com");
        }
    }

    /**
     * Seeds base internship listings if the repository is empty.
     */
    private void seedSampleInternshipsIfEmpty() {
        if (internshipRepository.count() == 0) {
            Internship google = new Internship();
            google.setTitle("Frontend Developer Intern");
            google.setCompany("Google");
            google.setLocation("Bangalore (Hybrid)");
            google.setStipend(45000.0);
            google.setDuration("3 Months");
            google.setDeadline(LocalDate.now().plusMonths(2));
            google.setLogo("🟢");
            google.setTags("React, JavaScript, CSS, Redux");
            google.setDescription("Work with Google Cloud Console team to build responsive, accessible UI components using React.");
            google.setRequirements("Proficiency in modern JavaScript, React, and responsive CSS.");
            google.setApplicationUrl("https://careers.google.com/jobs/results/");
            google.setInternshipType("Hybrid");
            google.setOpenings(3);
            internshipRepository.save(google);

            Internship msft = new Internship();
            msft.setTitle("Backend Engineering Intern");
            msft.setCompany("Microsoft");
            msft.setLocation("Hyderabad (On-site)");
            msft.setStipend(50000.0);
            msft.setDuration("6 Months");
            msft.setDeadline(LocalDate.now().plusMonths(3));
            msft.setLogo("🔵");
            msft.setTags("Java, Spring Boot, MySQL, Azure");
            msft.setDescription("Join the Azure DevOps team to design and implement RESTful microservices.");
            msft.setRequirements("Strong Java fundamentals, Spring Boot, and relational database concepts.");
            msft.setApplicationUrl("https://careers.microsoft.com/v2/global/en/home.html");
            msft.setInternshipType("On-site");
            msft.setOpenings(5);
            internshipRepository.save(msft);

            Internship amazon = new Internship();
            amazon.setTitle("Data Science Intern");
            amazon.setCompany("Amazon");
            amazon.setLocation("Remote");
            amazon.setStipend(40000.0);
            amazon.setDuration("3 Months");
            amazon.setDeadline(LocalDate.now().plusMonths(1));
            amazon.setLogo("🟠");
            amazon.setTags("Python, Machine Learning, SQL, Pandas");
            amazon.setDescription("Analyze customer behaviour data to build recommendation models for Amazon Prime.");
            amazon.setRequirements("Python, Pandas, SQL data wrangling, and machine learning fundamentals.");
            amazon.setApplicationUrl("https://www.amazon.jobs/en/");
            amazon.setInternshipType("Remote");
            amazon.setOpenings(2);
            internshipRepository.save(amazon);

            logger.info("Seeded 3 initial sample internships into database.");
        }
    }
}
