package com.proyecto.grupo_umg2024.service;

import java.util.List;

public interface ServiceCRUD<T> {
    T createOrUpdate(T value);
    List<T> getDataList();
    T getFindUncle(Long value) ;
    void deleteFind(T value);
}
