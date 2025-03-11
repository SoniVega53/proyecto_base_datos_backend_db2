package com.proyecto.grupo_umg2024.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class BaseResponse <T>{
    private String code;
    private String message;
    private T entity;
}
