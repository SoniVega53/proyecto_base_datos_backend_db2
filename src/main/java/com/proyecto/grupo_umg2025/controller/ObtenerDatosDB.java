package com.proyecto.grupo_umg2025.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.proyecto.grupo_umg2025.model.entity.BaseResponse;
import com.proyecto.grupo_umg2025.service.DatabaseService;

import jakarta.transaction.Transactional;
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

    @Transactional
    @PostMapping("/ejecutarQuery")
    public ResponseEntity<BaseResponse> ejecutarQuery(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String query) {
        try {
            JdbcTemplate jdbcTemplate = databaseService.createJdbcTemplate(username, password);

            List<Map<String, Object>> resultados = jdbcTemplate.queryForList(query);

            return ResponseEntity.ok(
                    BaseResponse.builder().code("200").message("Consulta ejecutada correctamente")
                            .entity(resultados).build());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    BaseResponse.builder().code("500").message("Error al procesar la solicitud").entity(e.getMessage()).build());
        }
    }

}
