package com.dulceshop.backend.config;

import com.dulceshop.backend.entity.Role;
import com.dulceshop.backend.entity.User;
import com.dulceshop.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Profile("local")
@RequiredArgsConstructor
public class LocalAdminInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner createLocalAdmin(
            @Value("${ADMIN_EMAIL:}") String adminEmail,
            @Value("${ADMIN_PASSWORD:}") String adminPassword
    ) {
        return args -> {
            if (adminEmail.isBlank() || adminPassword.isBlank()) {
                return;
            }

            if (userRepository.existsByEmail(adminEmail)) {
                return;
            }

            User admin = new User();
            admin.setFirstName("Local");
            admin.setLastName("Administrator");
            admin.setEmail(adminEmail);
            admin.setPasswordHash(passwordEncoder.encode(adminPassword));
            admin.setRole(Role.ADMIN);
            admin.setActive(true);

            userRepository.save(admin);
        };
    }
}