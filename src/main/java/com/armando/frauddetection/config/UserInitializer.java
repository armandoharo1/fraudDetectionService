package com.armando.frauddetection.config;

import com.armando.frauddetection.domain.model.Role;
import com.armando.frauddetection.domain.model.User;
import com.armando.frauddetection.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    @Value("${app.admin.username}")
    private String adminUser;

    @Value("${app.admin.password}")
    private String adminPass;

    @Override
    public void run(String... args) {

        if (userRepository.findByUsername(adminUser).isEmpty()) {

            User admin = User.builder()
                    .username(adminUser)
                    .password(encoder.encode(adminPass))
                    .role(Role.ADMIN)
                    .enabled(true)
                    .build();

            userRepository.save(admin);

            System.out.println("✔ ADMIN creado automáticamente: " + adminUser);
        }
    }
}
