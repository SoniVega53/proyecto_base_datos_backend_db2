package com.proyecto.grupo_umg2024;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.proyecto.grupo_umg2024.model.RoleUser;
import com.proyecto.grupo_umg2024.model.auth.RegisterRequest;
import com.proyecto.grupo_umg2024.service.UserService;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class ConfigurationAuth {
    private final UserService service;

    @Bean
    CommandLineRunner init() {
        return args -> {
            try {
                service.getFindUncle("admin");
            } catch (Exception e) {
                service.registerAdmin(RegisterRequest.builder()
                        .username("admin")
                        .password("admin")
                        .name("admin")
                        .lastname("admin")
                        .rol(RoleUser.ADMIN.name())
                        .email("admin@gmail.com")
                        .build());
            }
        };
    }
       
}
