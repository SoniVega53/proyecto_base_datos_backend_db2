package com.proyecto.grupo_umg2024.model.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    String username;
    String name;
    String lastname;
    String email;
    String password;
    String passwordChange;
    String rol;
}
