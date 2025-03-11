package com.proyecto.grupo_umg2024.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proyecto.grupo_umg2024.model.entity.Articles;
import com.proyecto.grupo_umg2024.model.repository.ArticlesRepository;

@Service
public class ArticleService implements ServiceCRUD<Articles> {

    @Autowired
    private ArticlesRepository repository;

    @Override
    public Articles createOrUpdate(Articles value) {
        return repository.save(value);
    }

    @Override
    public List<Articles> getDataList() {
         return (List<Articles>) repository.findAll();
    }

    @Override
    public Articles getFindUncle(Long value) {
        Optional<Articles> res = repository.findById(value);
        return res.isPresent() ? res.get() : null;
    }
    

    @Override
    public void deleteFind(Articles value) {
        repository.delete(value);
    }

    
}
