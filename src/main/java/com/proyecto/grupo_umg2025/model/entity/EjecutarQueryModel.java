package com.proyecto.grupo_umg2025.model.entity;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EjecutarQueryModel {
    String username;
    String password; 
    String nameDataBase; 
    String query;
}
