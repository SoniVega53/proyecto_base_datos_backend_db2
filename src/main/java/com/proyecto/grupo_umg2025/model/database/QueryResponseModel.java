package com.proyecto.grupo_umg2025.model.database;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QueryResponseModel {
    String type;
    String message;
    List<Map<String, Object>> listadoRepuesta;
}
