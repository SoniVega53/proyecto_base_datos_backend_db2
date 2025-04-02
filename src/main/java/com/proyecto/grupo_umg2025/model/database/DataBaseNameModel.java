package com.proyecto.grupo_umg2025.model.database;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DataBaseNameModel {
    String databaseName;
    List<TablesNameModel> tables;
}
