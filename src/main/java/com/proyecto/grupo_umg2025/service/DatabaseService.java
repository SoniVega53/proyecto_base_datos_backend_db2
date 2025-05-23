package com.proyecto.grupo_umg2025.service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.proyecto.grupo_umg2025.model.auth.PasswordEncryptionService;
import com.proyecto.grupo_umg2025.model.entity.UserResponse;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.sql.SQLException;

@Service
@SuppressWarnings("static-access")
public class DatabaseService {
    @Autowired
    private JdbcTemplate jdbcTemplateMain;

    private PasswordEncryptionService passwordEncryptionService;
    private String baseSQL = "jdbc:mysql://localhost:3306/";
    //private String baseSQL = "jdbc:mysql://localhost:6446/";

    public DataSource createDataSource(String username, String password) {
        try {
            String decryptedPassword = passwordEncryptionService.decrypt(password);

            DriverManagerDataSource dataSource = new DriverManagerDataSource();
            dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
            dataSource.setUrl(baseSQL);
            dataSource.setUsername(username);
            dataSource.setPassword(decryptedPassword);

            return dataSource;
        } catch (Exception e) {
            return null;
        }
    }

    public DataSource createDataSourceNoEncry(String username, String password) {
        try {

            DriverManagerDataSource dataSource = new DriverManagerDataSource();
            dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
            dataSource.setUrl(baseSQL);
            dataSource.setUsername(username);
            dataSource.setPassword(password);

            return dataSource;
        } catch (Exception e) {
            return null;
        }
    }

    public DataSource createDataSource(String username, String password, String databaseName) {
        try {

            if (databaseName == null || databaseName.trim().isEmpty()) {
                throw new IllegalArgumentException("El nombre de la base de datos no puede estar vacío.");
            }
            String decryptedPassword = passwordEncryptionService.decrypt(password);

            DriverManagerDataSource dataSource = new DriverManagerDataSource();
            dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
            dataSource.setUrl(baseSQL + databaseName + "?serverTimezone=UTC");
            dataSource.setUsername(username);
            dataSource.setPassword(decryptedPassword);

            return dataSource;
        } catch (Exception e) {
            throw new RuntimeException("Error al crear la conexión a la base de datos: " + e.getMessage(), e);
        }
    }

    public JdbcTemplate createJdbcTemplate(String username, String password) {
        return new JdbcTemplate(createDataSource(username, password));
    }

    public JdbcTemplate createJdbcTemplate(String username, String password, String databaseName) {
        return new JdbcTemplate(createDataSource(username, password, databaseName));
    }

    public List<Map<String, Object>> executeQuery(String sql, JdbcTemplate jdbcTemplate) {
        return jdbcTemplate.queryForList(sql);
    }

    public List<UserResponse> listUser() {
        String sql = "SELECT user, host FROM mysql.user";
        String listUser = new Gson().toJson(jdbcTemplateMain.queryForList(sql));

        Type listType = new TypeToken<List<UserResponse>>() {
        }.getType();
        return new Gson().fromJson(listUser, listType);
    }

    public List<String> obtenerPermisosUsuario(String usuario, JdbcTemplate jdbcTemplate) {
        String sql = "SHOW GRANTS FOR " + usuario;

        return jdbcTemplate.query(sql, new Object[] { usuario }, (rs, rowNum) -> rs.getString(1));
    }

    public void closeDataSource(DataSource dataSource) {
        if (dataSource instanceof DriverManagerDataSource) {
            try {
                ((DriverManagerDataSource) dataSource).getConnection().close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public String obtenerSoloNombresPermisos(String usuario) {
        String sql = "SHOW GRANTS FOR " + usuario;
        List<Map<String, Object>> grants = jdbcTemplateMain.queryForList(sql);

        Set<String> permisos = new HashSet<>();

        for (Map<String, Object> grant : grants) {
            for (Object value : grant.values()) {
                String permisosExtraidos = value.toString().replaceAll("GRANT (.+) ON .*", "$1");
                permisos.addAll(Arrays.asList(permisosExtraidos.split(", ")));
            }
        }

        return String.join(", ", permisos);
    }
    public String obtenerSoloNombresPermisos(String usuario,JdbcTemplate jdbcTemplate) {
        String sql = "SHOW GRANTS FOR " + usuario;
        List<Map<String, Object>> grants = jdbcTemplate.queryForList(sql);

        Set<String> permisos = new HashSet<>();

        for (Map<String, Object> grant : grants) {
            for (Object value : grant.values()) {
                String permisosExtraidos = value.toString().replaceAll("GRANT (.+) ON .*", "$1");
                permisos.addAll(Arrays.asList(permisosExtraidos.split(", ")));
            }
        }

        return String.join(", ", permisos);
    }
}
