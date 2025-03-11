package com.proyecto.grupo_umg2024.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.grupo_umg2024.model.auth.LoginRequest;
import com.proyecto.grupo_umg2024.model.auth.RegisterRequest;
import com.proyecto.grupo_umg2024.model.entity.BaseResponse;
import com.proyecto.grupo_umg2024.model.entity.User;
import com.proyecto.grupo_umg2024.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/proyecto/noauth")
@SuppressWarnings("rawtypes")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class AuthenticationController {

    private final UserService service;

    @PostMapping("register")
    public ResponseEntity<BaseResponse> register(@RequestBody RegisterRequest entity) {
        try {
            User userCreate = service.register(entity);
            if (userCreate != null) {
                return ResponseEntity.ok(BaseResponse.builder()
                        .code("200")
                        .message("Se creo correctamente")
                        .entity(userCreate).build());
            }
            return ResponseEntity.ok(BaseResponse.builder().code("400").message("Por favor revise sus datos").build());
        } catch (Exception e) {
            return ResponseEntity.ok(BaseResponse.builder().code("400").message("Este usuario ya existe").build());
        }
    }

    @PostMapping("login")
    public ResponseEntity<BaseResponse> login(@RequestBody LoginRequest request) {
        try {
            return ResponseEntity.ok(BaseResponse.builder().code("200").message("Inicio Correctamente")
                    .entity(service.login(request)).build());
        } catch (Exception e) {
            return ResponseEntity.ok(BaseResponse.builder().code("400").message("Usuario no Existe o Contraseña es invalida").build());
        }
    }

}
