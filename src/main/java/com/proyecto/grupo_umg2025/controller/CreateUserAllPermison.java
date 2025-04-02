package com.proyecto.grupo_umg2025.controller;

import java.util.List;
import java.util.Map;

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
import com.proyecto.grupo_umg2025.model.auth.PasswordEncryptionService;
import com.proyecto.grupo_umg2025.model.entity.BaseResponse;
import com.proyecto.grupo_umg2025.model.entity.PermisosUsuario;
import com.proyecto.grupo_umg2025.model.entity.UserResponse;
import com.proyecto.grupo_umg2025.service.DatabaseService;

import lombok.RequiredArgsConstructor;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/proyecto/")
@SuppressWarnings("rawtypes")
@RequiredArgsConstructor
public class CreateUserAllPermison {
    @Autowired
    private DatabaseService databaseService;
    @Autowired
    private JdbcTemplate jdbcTemplateMain;

    private PasswordEncryptionService passwordEncryptionService;

    @PostMapping("/crearUsuario")
    public ResponseEntity<BaseResponse> crearUsuarioSeguro(@RequestBody LoginRequest loginRequest, @RequestParam String nombre,
            @RequestParam String pass, @RequestParam PermisosUsuario permiso) {
        try {
            if (!isValidateUsuario(nombre)) {
                String sql = "CREATE USER " + nombre + " IDENTIFIED BY '" + pass + "'";
                jdbcTemplateMain.execute(sql);
                if (!isValidateUsuario(nombre)) {
                    return ResponseEntity.ok(
                            BaseResponse.builder().code("400").message("Surgio un problema no se pudo crear usuario")
                                    .entity(new LoginRequest(nombre, "")).build());
                }

                JdbcTemplate jdbcTemplate = databaseService.createJdbcTemplate(loginRequest.getUsername(), loginRequest.getPassword());

                if (permiso != null && permiso.getPermisos() != null) {
                    for (String item : permiso.getPermisos()) {
                        sql = "GRANT " + item + " ON *.* TO " + nombre;
                        jdbcTemplate.execute(sql);
                    }
                }

                return ResponseEntity.ok(BaseResponse.builder().code("200").message("Se creo exitosamente")
                        .entity(new LoginRequest(nombre, "")).build());
            }
            return ResponseEntity.ok(BaseResponse.builder().code("400").message("Usuario ya existe")
                    .entity(new LoginRequest(nombre, "")).build());

        } catch (Exception e) {
            return ResponseEntity.ok(BaseResponse.builder().code("400").message("Error al crear usuario")
                    .entity(e).build());
        }
    }

    @PostMapping("/darPermisosUsuario")
    public ResponseEntity<BaseResponse> darPermisosSeguro(@RequestParam String nombre, @RequestBody PermisosUsuario permiso) {
        try {
            JdbcTemplate jdbcTemplate = databaseService.createJdbcTemplate(permiso.getUsername(), permiso.getPassword());

            if (permiso != null && permiso.getPermisos() != null) {
                for (String item : permiso.getPermisos()) {
                    String sql = "GRANT " + item + " ON *.* TO " + nombre;
                    jdbcTemplate.execute(sql);
                }
            }
            return ResponseEntity.ok(
                    BaseResponse.builder().code("200").message("Permisos otorgados exitosamente ").build());
        } catch (Exception e) {
            return ResponseEntity.ok(BaseResponse.builder().code("400").message(e.getMessage())
                    .entity(e).build());
        }
    }

    @PostMapping("/darPermisosUsuarioBaseDatos")
    public ResponseEntity<BaseResponse> darPermisosUsuarioBaseDatos(@RequestParam String nombre, @RequestParam String nameDataBase,
            @RequestBody PermisosUsuario permiso) {
        try {
            JdbcTemplate jdbcTemplate = databaseService.createJdbcTemplate(permiso.getUsername(), permiso.getPassword(),nameDataBase);

            if (permiso != null && permiso.getPermisos() != null) {
                for (String item : permiso.getPermisos()) {
                    String sql = "GRANT " + item + " ON " + nameDataBase + ".* TO " + nombre;
                    jdbcTemplate.execute(sql);
                }
            }
            return ResponseEntity.ok(
                    BaseResponse.builder().code("200").message("Permiso otorgado" + nombre).build());
        } catch (Exception e) {
            return ResponseEntity.ok(BaseResponse.builder().code("400").message("Error al otorgar permisos")
                    .entity(e).build());
        }
    }

    @PostMapping("/revocarPermisosUsuario")
    public ResponseEntity<BaseResponse> revocarPermisosSeguro(@RequestParam String nombre, @RequestBody PermisosUsuario permiso) {
        try {
            JdbcTemplate jdbcTemplate = databaseService.createJdbcTemplate(permiso.getUsername(), permiso.getPassword());

            if (permiso != null && permiso.getPermisos() != null) {
                for (String item : permiso.getPermisos()) {
                    String sql = "REVOKE " + item + " ON *.* FROM " + nombre;
                    jdbcTemplate.execute(sql);
                }
            }
            return ResponseEntity.ok(
                    BaseResponse.builder().code("200").message("Permisos revocados exitosamente").build());
        } catch (Exception e) {
            return ResponseEntity.ok(BaseResponse.builder().code("400").message("No Se puedo Revocar Permisos, verifique que si existan permisos")
                    .entity(e).build());
        }
    }

    @PostMapping("/revocarPermisosUsuarioBaseDatos")
    public ResponseEntity<BaseResponse> revocarPermisosUsuarioBaseDatos( @RequestParam String nombre, @RequestParam String nameDataBase, @RequestBody PermisosUsuario permiso) {
        try {
            JdbcTemplate jdbcTemplate = databaseService.createJdbcTemplate(permiso.getUsername(), permiso.getPassword());

            if (permiso != null && permiso.getPermisos() != null) {
                for (String item : permiso.getPermisos()) {
                    String sql = "REVOKE " + item + " ON "+ nameDataBase + ".* FROM " + nombre;
                    jdbcTemplate.execute(sql);
                }
            }
            return ResponseEntity.ok(
                    BaseResponse.builder().code("200").message("Permisos revocados exitosamente").build());
        } catch (Exception e) {
            return ResponseEntity.ok(BaseResponse.builder().code("400").message(e.getMessage())
                    .entity(e).build());
        }
    }

    @PostMapping("/verPermisos")
    public ResponseEntity<BaseResponse> verPermisos(@RequestBody LoginRequest loginRequest, @RequestParam String nombre) {
        try {
            return ResponseEntity.ok(
                    BaseResponse.builder().code("200").message("Se realizo consulta exitosa")
                            .entity(databaseService.obtenerSoloNombresPermisos(nombre)).build());
        } catch (Exception e) {
            return ResponseEntity.ok(BaseResponse.builder().code("400").message(e.getMessage())
                    .entity(e).build());
        }
    }

    @PostMapping("/verPermisosDataBase")
    public ResponseEntity<BaseResponse> verPermisosDataBase(@RequestBody LoginRequest loginRequest, @RequestParam String nombre,@RequestParam String nameDataBase) {
        try {
            JdbcTemplate jdbcTemplate = databaseService.createJdbcTemplate(loginRequest.getUsername(), loginRequest.getPassword(),nameDataBase);

            return ResponseEntity.ok(
                    BaseResponse.builder().code("200").message("Se realizo consulta exitosa")
                            .entity(databaseService.obtenerSoloNombresPermisos(nombre,jdbcTemplate)).build());
        } catch (Exception e) {
            return ResponseEntity.ok(BaseResponse.builder().code("400").message(e.getMessage())
                    .entity(e).build());
        }
    }

    @PostMapping("/verUsuarios")
    public ResponseEntity<BaseResponse> verUsuarios() {
        try {
            return ResponseEntity.ok(
                    BaseResponse.builder().code("200").message("Se realizo consulta exitosa")
                            .entity(databaseService.listUser()).build());
        } catch (Exception e) {
            return ResponseEntity.ok(BaseResponse.builder().code("400").message("Error consultar")
                    .entity(e).build());
        }
    }

    @PostMapping("/eliminarUsurio")
    public ResponseEntity<BaseResponse> eliminarUsurio(@RequestBody LoginRequest loginRequest, @RequestParam String nombre) {
        try {
            if (loginRequest.getUsername().equals("root")) {
                String sql = "DROP USER " + nombre;
                jdbcTemplateMain.execute(sql);
                return ResponseEntity.ok(
                        BaseResponse.builder().code("200").message("Se elimino exitosamente").build());
            }

            return ResponseEntity.ok(
                    BaseResponse.builder().code("400").message("No tiene los permisos para eliminar Usuario")
                            .entity(new LoginRequest(loginRequest.getUsername(), "")).build());
        } catch (Exception e) {
            return ResponseEntity.ok(BaseResponse.builder().code("400").message(e.getMessage())
                    .entity(e).build());
        }
    }

    @PostMapping("/cambiarPassword")
    public ResponseEntity<BaseResponse> cambiarPassword(@RequestBody LoginRequest loginRequest, @RequestParam String validPassword, @RequestParam String newPassword) {
        try {
            String decryptedPassword = passwordEncryptionService.decrypt(loginRequest.getPassword());
            if (decryptedPassword.equals(validPassword)) {
                String sql = "ALTER USER '" + loginRequest.getUsername() + "'@'localhost' IDENTIFIED BY '" + newPassword + "'";

                return ResponseEntity.ok(
                        BaseResponse.builder().code("200").message("Contraseña cambiada exitosamente")
                                .entity(jdbcTemplateMain.update(sql)).build());
            }
            return ResponseEntity.ok(
                    BaseResponse.builder().code("400").message("Contraseña no valida, verifique su Contraseña")
                            .entity(null).build());

        } catch (Exception e) {
            return ResponseEntity.ok(BaseResponse.builder().code("400").message("Verifique su contraseña")
                    .entity(e.getMessage()).build());
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
