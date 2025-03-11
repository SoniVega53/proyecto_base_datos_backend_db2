package com.proyecto.grupo_umg2024.model;

import lombok.Data;

@Data
public class BaseResponse<T> {
    private String message;
    private T entidad;
    
    public BaseResponse() {
        
    }

    public BaseResponse(T entidad) {
        this.entidad = entidad;
    }

    public BaseResponse(String message) {
        this.message = message;
    }
}

