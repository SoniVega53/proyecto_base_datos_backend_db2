package com.proyecto.grupo_umg2025.controller;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.grupo_umg2025.model.auth.LoginRequest;
import com.proyecto.grupo_umg2025.model.entity.BaseResponse;
import com.proyecto.grupo_umg2025.model.entity.UserResponse;
import com.proyecto.grupo_umg2025.service.DatabaseService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/proyecto")
@SuppressWarnings("rawtypes")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class AuthenticationController {
    @Autowired
    private DatabaseService databaseService;

    @Autowired
    private JdbcTemplate jdbcTemplateMain;

    @PostMapping("/login")
    public ResponseEntity<BaseResponse> login(@RequestBody LoginRequest loginRequest) {
        try {
            
            DataSource data = databaseService.createDataSource(loginRequest.getUsername(), loginRequest.getPassword());
            System.err.println(data.getConnection());
            UserResponse response = null;
            for (UserResponse item : databaseService.listUser()) {
                if (loginRequest.getUsername().equals(item.getUser())){
                    response = item;
                }
            }

            return ResponseEntity.ok(BaseResponse.builder().code("200").message("Conexion exitosa")
                    .entity(response).build());
        } catch (Exception e) {
            return ResponseEntity.ok(
                    BaseResponse.builder().code("400").message("Usuario no Existe o Contraseña es invalida").build());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<BaseResponse> crearUsuarioSeguro(@RequestBody LoginRequest loginRequest) {
        try {
            if (!isValidateUsuario(loginRequest.getUsername())) {
                String sql = "CREATE USER " + loginRequest.getUsername() + " IDENTIFIED BY '"
                        + loginRequest.getPassword() + "'";
                jdbcTemplateMain.execute(sql);
                return ResponseEntity.ok(BaseResponse.builder().code("200").message("Se creo exitosamente")
                        .entity(new LoginRequest(loginRequest.getUsername(), "")).build());
            }
            return ResponseEntity.ok(BaseResponse.builder().code("400").message("Usuario ya existe")
                    .entity(new LoginRequest(loginRequest.getUsername(), "")).build());
        } catch (Exception e) {
            return ResponseEntity.ok(
                    BaseResponse.builder().code("400").message("Usuario no Existe o Contraseña es invalida").build());
        }
    }

    private boolean isValidateUsuario(String username) {
        List<UserResponse> responses = databaseService.listUser();
        for (UserResponse item : responses) {
            if (username.equals(item.getUser()))
                return true;
        }
        return false;
    }
}
