package com.armando.frauddetection.config;

import com.armando.frauddetection.domain.model.Role;
import com.armando.frauddetection.domain.model.User;
import com.armando.frauddetection.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import jakarta.annotation.PostConstruct;

@Configuration
@RequiredArgsConstructor
public class UserInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {

        // 👑 Usuario ADMIN
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("123456"))
                    .role(Role.ADMIN)
                    .build();
            userRepository.save(admin);
        }

        // 👤 Usuario ANALYST
        if (userRepository.findByUsername("analyst").isEmpty()) {
            User analyst = User.builder()
                    .username("analyst")
                    .password(passwordEncoder.encode("123456"))
                    .role(Role.ANALYST)
                    .build();
            userRepository.save(analyst);
        }

        // 👤 Usuario AUDITOR
        if (userRepository.findByUsername("auditor").isEmpty()) {
            User auditor = User.builder()
                    .username("auditor")
                    .password(passwordEncoder.encode("123456"))
                    .role(Role.AUDITOR)
                    .build();
            userRepository.save(auditor);
        }
    }
}
