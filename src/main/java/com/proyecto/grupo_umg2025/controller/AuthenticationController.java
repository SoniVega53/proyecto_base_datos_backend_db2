package com.proyecto.grupo_umg2025.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.grupo_umg2025.model.auth.LoginRequest;
import com.proyecto.grupo_umg2025.model.auth.RegisterRequest;
import com.proyecto.grupo_umg2025.model.entity.BaseResponse;
import com.proyecto.grupo_umg2025.model.entity.User;
import com.proyecto.grupo_umg2025.model.entity.UserResponse;
import com.proyecto.grupo_umg2025.service.UserService;

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
            User user = service.getFindUncle(request.getUsername());
            
            if (user != null) {
                UserResponse resp = new UserResponse(user.getUsername(),user.getName(),user.getLastname(),user.getEmail(),user.getRol());
                if (user.getPassword().equals(request.getPassword())) {
                    return ResponseEntity.ok(BaseResponse.builder().code("200").message("Se Inicio Sesion Correctamente")
                            .entity(resp).build());
                }else{
                    return ResponseEntity.ok(BaseResponse.builder().code("400").message("Contraseña Incorrecta, Porfavor verifique.")
                            .entity(resp).build());
                }
            }
            return ResponseEntity.ok(BaseResponse.builder().code("400").message("Este Usuario No existe")
                            .entity(null).build());
        } catch (Exception e) {
            return ResponseEntity.ok(BaseResponse.builder().code("400").message("Usuario no Existe o Contraseña es invalida").build());
        }
    }

}
