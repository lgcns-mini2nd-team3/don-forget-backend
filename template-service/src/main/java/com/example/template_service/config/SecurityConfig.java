package com.example.template_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/api/v1/templates/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/templates/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/templates/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/templates/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}