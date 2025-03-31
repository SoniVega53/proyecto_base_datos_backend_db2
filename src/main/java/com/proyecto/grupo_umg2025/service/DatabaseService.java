package com.proyecto.grupo_umg2025.service;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.proyecto.grupo_umg2025.model.entity.BaseResponse;
import com.proyecto.grupo_umg2025.model.entity.UserResponse;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

@Service
public class DatabaseService {
    @Autowired
    private JdbcTemplate jdbcTemplateMain;

    public DataSource createDataSource(String username, String password) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/");
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        
        return dataSource;
    }

    public JdbcTemplate createJdbcTemplate(String username, String password) {
        return new JdbcTemplate(createDataSource(username, password));
    }

    public List<UserResponse> listUser() {
        String sql = "SELECT user, host FROM mysql.user";
        String listUser = new Gson().toJson(jdbcTemplateMain.queryForList(sql));

        Type listType = new TypeToken<List<UserResponse>>() {}.getType();
        return new Gson().fromJson(listUser,listType);
    }
}
