package com.example.user_service.service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.user_service.dao.UserRepository;
import com.example.user_service.domain.entity.UserEntity;

import lombok.RequiredArgsConstructor;



@Component
@RequiredArgsConstructor
public class InitalDataLoader implements CommandLineRunner{
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

        if(userRepository.findByEmail("admin@admin.com").isPresent()){
            return;
        }

        UserEntity user = UserEntity.builder()
                                    .email("admin@admin.com")
                                    .password(passwordEncoder.encode("admin"))
                                    .role("ROLE_ADMIN")
                                    .name("admin")
                                    .build();
        userRepository.save(user);
    }
    
}
