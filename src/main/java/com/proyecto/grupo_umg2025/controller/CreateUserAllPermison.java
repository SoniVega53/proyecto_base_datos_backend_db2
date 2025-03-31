package com.proyecto.grupo_umg2025.controller;

import java.lang.reflect.Array;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.grupo_umg2025.model.auth.LoginRequest;
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

    @PostMapping("/crearUsuario")
    public ResponseEntity<BaseResponse> crearUsuarioSeguro(@RequestParam String username,
            @RequestParam String password, @RequestParam String nombre,
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

                JdbcTemplate jdbcTemplate = databaseService.createJdbcTemplate(username, password);

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

    @PostMapping("/darPermisosUsuarioAll")
    public ResponseEntity<BaseResponse> darPermisosSeguro(@RequestParam String username,
            @RequestParam String password, @RequestParam String nombre, @RequestParam String permiso) {
        try {
            JdbcTemplate jdbcTemplate = databaseService.createJdbcTemplate(username, password);

            String sql = "GRANT ALL PRIVILEGES ON *.* TO " + nombre;
            jdbcTemplate.execute(sql);
            return ResponseEntity.ok(
                    BaseResponse.builder().code("200").message("Permiso " + permiso + " otorgado a " + nombre).build());
        } catch (Exception e) {
            return ResponseEntity.ok(BaseResponse.builder().code("400").message("Error al otorgar permisos")
                    .entity(e).build());
        }
    }

    @PostMapping("/revocarPermisosUsuarioAll")
    public ResponseEntity<BaseResponse> revocarPermisosSeguro(@RequestParam String username,
            @RequestParam String password, @RequestParam String nombre, @RequestParam String permiso) {
        try {
            JdbcTemplate jdbcTemplate = databaseService.createJdbcTemplate(username, password);
            String sql = "REVOKE ALL PRIVILEGES, GRANT OPTION FROM " + nombre;
            jdbcTemplate.execute(sql);
            return ResponseEntity.ok(
                    BaseResponse.builder().code("200").message("Permiso " + permiso + " revocado a " + nombre).build());
        } catch (Exception e) {
            return ResponseEntity.ok(BaseResponse.builder().code("400").message("Error al revocar permisos")
                    .entity(e).build());
        }
    }

    @GetMapping("/verPermisos")
    public ResponseEntity<BaseResponse> verPermisos(@RequestParam String nombre) {
        try {
            String sql = "SHOW GRANTS FOR " + nombre + "@localhost";
            return ResponseEntity.ok(
                    BaseResponse.builder().code("200").message("Se realizo consulta exitosa")
                            .entity(jdbcTemplateMain.queryForList(sql)).build());
        } catch (Exception e) {
            return ResponseEntity.ok(BaseResponse.builder().code("400").message("Error consultar")
                    .entity(e).build());
        }
    }

    @GetMapping("/verUsuarios")
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
    public ResponseEntity<BaseResponse> eliminarUsurio(@RequestParam String username,
            @RequestParam String password, @RequestParam String nombre) {
        try {
            if (username.equals("root")) {
                String sql = "DROP USER " + nombre;
                return ResponseEntity.ok(
                        BaseResponse.builder().code("200").message("Se elimino exitosamente")
                                .entity(jdbcTemplateMain.queryForList(sql)).build());
            }

            return ResponseEntity.ok(
                    BaseResponse.builder().code("400").message("No tiene los permisos para eliminar Usuario")
                            .entity(new LoginRequest(username,"")).build());
        } catch (Exception e) {
            return ResponseEntity.ok(BaseResponse.builder().code("400").message("Error consultar")
                    .entity(e).build());
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
