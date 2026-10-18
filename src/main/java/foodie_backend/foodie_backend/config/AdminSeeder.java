package foodie_backend.foodie_backend.config;

import foodie_backend.foodie_backend.model.Admin;
import foodie_backend.foodie_backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminSeeder.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminEmail;

    public AdminSeeder(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       @Value("${foodie.admin.email:admin@foodies.com}") String adminEmail) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminEmail = adminEmail;
    }

    @Override
    public void run(String... args) {
        String adminPassword = System.getenv("ADMIN_PASSWORD");
        if (adminPassword == null || adminPassword.isBlank()) {
            log.warn("ADMIN_PASSWORD is not set, the admin account was not created");
            return;
        }

        if (userRepository.findByEmail(adminEmail).isPresent()) {
            return;
        }

        Admin admin = new Admin("Restaurant Admin", null, adminEmail);
        admin.setPasswordHash(passwordEncoder.encode(adminPassword));
        userRepository.insert(admin);
        log.info("Admin account created for {}", adminEmail);
    }
}
