package com.proyecto.grupo_umg2025.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.grupo_umg2025.model.entity.BaseResponse;
import com.proyecto.grupo_umg2025.service.DatabaseService;

import lombok.RequiredArgsConstructor;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/proyecto/")
@SuppressWarnings("rawtypes")
@RequiredArgsConstructor
public class ObtenerDatosDB {
    @Autowired
    private DatabaseService databaseService;

    @GetMapping("/obtenerBasesDeDatos")
    public ResponseEntity<BaseResponse> obtenerBasesDeDatos(@RequestParam String username,
            @RequestParam String password) {
        try {
            JdbcTemplate jdbcTemplate = databaseService.createJdbcTemplate(username, password);
            String sql = "SHOW DATABASES";
            return ResponseEntity.ok(BaseResponse.builder().code("200").message("Consulta exitosa")
                    .entity(jdbcTemplate.queryForList(sql)).build());
        } catch (Exception e) {
            return ResponseEntity.ok(BaseResponse.builder().code("400").message("Error al obtener Base de datos")
                    .entity(e).build());
        }
    }

    @GetMapping("/obtenerTablasDeBase")
    public ResponseEntity<BaseResponse> obtenerTablasDeBase(@RequestParam String username,
            @RequestParam String password, @RequestParam String nombreBaseDeDatos) {
        try {
            JdbcTemplate jdbcTemplate = databaseService.createJdbcTemplate(username, password);
            String useDbSql = "USE " + nombreBaseDeDatos;
            jdbcTemplate.execute(useDbSql);
            String sqlTablas = "SHOW TABLES";
            return ResponseEntity.ok(BaseResponse.builder().code("200").message("Consulta exitosa")
                    .entity(jdbcTemplate.queryForList(sqlTablas)).build());
        } catch (Exception e) {
            return ResponseEntity.ok(BaseResponse.builder().code("400").message("Error al obtener Tabla")
                    .entity(e).build());
        }
    }

    @GetMapping("/obtenerRegistrosDeBase")
    public ResponseEntity<BaseResponse> obtenerRegistrosDeBase(@RequestParam String username,
            @RequestParam String password, @RequestParam String nombreBaseDeDatos) {
        try {
            JdbcTemplate jdbcTemplate = databaseService.createJdbcTemplate(username, password);
            String useDbSql = "USE " + nombreBaseDeDatos;
            jdbcTemplate.execute(useDbSql);

            String sqlTablas = "SHOW TABLES";
            List<Map<String, Object>> tablas = jdbcTemplate.queryForList(sqlTablas);

            for (Map<String, Object> tabla : tablas) {
                String nombreTabla = (String) tabla.values().iterator().next();
                String sqlRegistros = "SELECT * FROM " + nombreTabla;
                List<Map<String, Object>> registros = jdbcTemplate.queryForList(sqlRegistros);
                tabla.put("registros", registros);
            }

            return ResponseEntity.ok(BaseResponse.builder().code("200").message("Consulta exitosa")
                    .entity(tablas).build());
        } catch (Exception e) {
            return ResponseEntity.ok(BaseResponse.builder().code("400").message("Error al obtener datos")
                    .entity(e).build());
        }
    }


    @PostMapping("/ejecutarQuery")
    public ResponseEntity<BaseResponse> ejecutarQuery(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String query) {
        try {
            JdbcTemplate jdbcTemplate = databaseService.createJdbcTemplate(username, password);
            return ResponseEntity.ok(
                BaseResponse.builder().code("200").message("Se realizo consulta exitosa")
                        .entity(jdbcTemplate.queryForList(query)).build());
        } catch (Exception e) {
            return ResponseEntity.ok(BaseResponse.builder().code("400").message("Error consultar")
                    .entity(e).build());
        }
    }
}
