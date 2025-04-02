package com.proyecto.grupo_umg2025.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.grupo_umg2025.model.auth.LoginRequest;
import com.proyecto.grupo_umg2025.model.database.DataBaseNameModel;
import com.proyecto.grupo_umg2025.model.database.TablesNameModel;
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

    @PostMapping("/obtenerBasesDeDatos")
    public ResponseEntity<BaseResponse> obtenerBasesDeDatos(@RequestBody LoginRequest loginRequest) {
        try {
            JdbcTemplate jdbcTemplate = databaseService.createJdbcTemplate(loginRequest.getUsername(),
                    loginRequest.getPassword());
            String sql = "SHOW DATABASES";
            return ResponseEntity.ok(BaseResponse.builder().code("200").message("Consulta exitosa")
                    .entity(jdbcTemplate.queryForList(sql)).build());
        } catch (Exception e) {
            return ResponseEntity.ok(BaseResponse.builder().code("400").message("Error al obtener Base de datos")
                    .entity(e.getMessage()).build());
        }
    }

    @PostMapping("/obtenerTablasDeBaseAll")
    public ResponseEntity<BaseResponse> obtenerTablasDeBase(@RequestBody LoginRequest loginRequest) {
        try {
            List<DataBaseNameModel> baseNameModelList = new ArrayList<>();
            JdbcTemplate jdbcTemplateDB = databaseService.createJdbcTemplate(
                    loginRequest.getUsername(), loginRequest.getPassword());

            String sqlDatabases = "SHOW DATABASES";
            String sqlTables = "SHOW TABLES";

            List<Map<String, Object>> listData = jdbcTemplateDB.queryForList(sqlDatabases);
            for (Map<String, Object> map : listData) {
                DataBaseNameModel baseNameModel = new DataBaseNameModel();

                String nameDa = (String) map.get("Database");
                baseNameModel.setDatabaseName(nameDa);
                System.err.println(nameDa);

                if (!nameDa.equals("information_schema") && !nameDa.equals("performance_schema")) {
                    JdbcTemplate jdbcTemplate = databaseService.createJdbcTemplate(loginRequest.getUsername(),
                            loginRequest.getPassword(), nameDa);
                    List<Map<String, Object>> tablas = jdbcTemplate.queryForList(sqlTables);

                    if (!tablas.isEmpty()) {
                        System.out.println("Keys disponibles: " + tablas.get(0).keySet());
                    }

                    String keyName = tablas.isEmpty() ? "" : tablas.get(0).keySet().iterator().next();

                    List<TablesNameModel> tablesList = tablas.stream()
                            .map(row -> new TablesNameModel(row.get(keyName).toString()))
                            .collect(Collectors.toList());

                    baseNameModel.setTables(tablesList);
                    baseNameModelList.add(baseNameModel);
                }
            }

            return ResponseEntity.ok(BaseResponse.builder().code("200").message("Consulta exitosa")
                    .entity(baseNameModelList).build());
        } catch (Exception e) {
            return ResponseEntity.ok(BaseResponse.builder().code("400").message("Error al obtener Tabla")
                    .entity(e).build());
        }
    }

    @PostMapping("/obtenerColums")
    public ResponseEntity<BaseResponse> obtenerColums(@RequestBody LoginRequest loginRequest,
            @RequestParam String nombreBaseDeDatos, @RequestParam String nameTable) {
        try {
            JdbcTemplate jdbcTemplate = databaseService.createJdbcTemplate(loginRequest.getUsername(),
                    loginRequest.getPassword(), nombreBaseDeDatos);

            String sqlTablas = "SHOW COLUMNS FROM " + nameTable;
            return ResponseEntity.ok(BaseResponse.builder().code("200").message("Consulta exitosa")
                    .entity(jdbcTemplate.queryForList(sqlTablas)).build());
        } catch (Exception e) {
            return ResponseEntity.ok(BaseResponse.builder().code("400").message("Error al obtener Tabla")
                    .entity(e.getMessage()).build());
        }
    }

    @PostMapping("/obtenerRegistrosDeBase")
    public ResponseEntity<BaseResponse> obtenerRegistrosDeBase(@RequestBody LoginRequest loginRequest,
            @RequestParam String nombreBaseDeDatos) {
        try {
            JdbcTemplate jdbcTemplate = databaseService.createJdbcTemplate(loginRequest.getUsername(),
                    loginRequest.getPassword());
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
            @RequestBody LoginRequest loginRequest,
            @RequestParam String query) {
        try {
            JdbcTemplate jdbcTemplate = databaseService.createJdbcTemplate(loginRequest.getUsername(),
                    loginRequest.getPassword());

            List<Map<String, Object>> resultados = jdbcTemplate.queryForList(query);

            return ResponseEntity.ok(
                    BaseResponse.builder().code("200").message("Consulta ejecutada correctamente")
                            .entity(resultados).build());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    BaseResponse.builder().code("500").message("Error al procesar la solicitud").entity(e.getMessage())
                            .build());
        }
    }

}
