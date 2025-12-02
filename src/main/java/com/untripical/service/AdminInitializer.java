package com.untripical.service;
import com.untripical.enums.UserRole;
import com.untripical.model.User;
import com.untripical.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminInitializer implements CommandLineRunner {

    @Value("${ADMIN1_USERNAME}")
    private String admin1Username;

    @Value("${ADMIN1_PASSWORD}")
    private String admin1Password;

    @Value("${ADMIN1_EMAIL}")
    private String admin1email;

    @Value("${ADMIN2_USERNAME}")
    private String admin2Username;

    @Value("${ADMIN2_PASSWORD}")
    private String admin2Password;

    @Value("${ADMIN2_EMAIL}")
    private String admin2email;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        createAdminIfNotExists(admin1Username, admin1Password, admin1email);
        createAdminIfNotExists(admin2Username, admin2Password, admin2email);
    }

    private void createAdminIfNotExists(String username, String password, String email) {
        userRepository.findByUsername(username).orElseGet(() -> {
            User admin = new User();
            admin.setUsername(username);
            admin.setPassword(passwordEncoder.encode(password));
            admin.setUserRole(UserRole.ADMIN);
            admin.setEmail(email);
            return userRepository.save(admin);
        });
    }
}