package com.armando.frauddetection.config;

import com.armando.frauddetection.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // 👈 habilita @PreAuthorize
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;
    private final RequestCorrelationFilter requestCorrelationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable());

        http.authorizeHttpRequests(auth -> auth
                // 🔓 Público (sin autenticación)
                .requestMatchers("/api/v1/auth/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                // 🔓 Health público
                .requestMatchers("/actuator/health", "/actuator/health/**").permitAll()

                // 🔐 Resto de Actuator solo para ADMIN
                .requestMatchers("/actuator/**").hasRole("ADMIN")

                // 🔐 Todo lo demás requiere estar autenticado
                .anyRequest().authenticated()
        );

        // 1️⃣ Filtro de correlación (primero)
        http.addFilterBefore(requestCorrelationFilter, UsernamePasswordAuthenticationFilter.class);

        // 2️⃣ Filtro JWT (después)
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
