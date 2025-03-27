package com.proyecto.grupo_umg2025.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.proyecto.grupo_umg2025.model.RoleUser;
import com.proyecto.grupo_umg2025.model.auth.LoginRequest;
import com.proyecto.grupo_umg2025.model.auth.RegisterRequest;
import com.proyecto.grupo_umg2025.model.entity.BaseResponse;
import com.proyecto.grupo_umg2025.model.entity.User;
import com.proyecto.grupo_umg2025.model.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService implements ServiceCRUD<User> {

    @Autowired
    private UserRepository repository;

    @Override
    public User createOrUpdate(User value) {
        return repository.save(value);
    }

    @Override
    public List<User> getDataList() {
        return (List<User>) repository.findAll();
    }

    @Override
    public User getFindUncle(Long value) {
        Optional<User> res = repository.findById(value);
        return res.isPresent() ? res.get() : null;
    }

    public User getFindUncleUsername(String value) {
        User user = repository.findByUsername(value).orElseThrow();
        return user;
    }

    public User getFindUncle(String value) {
        User user = repository.findByUsername(value).orElseThrow();
        return user;
    }

    @Override
    public void deleteFind(User value) {
        repository.delete(value);
    }

    public User updateUser(RegisterRequest request, User userFind) {
        User user = User.builder()
                .id(userFind.getId())
                .username(request.getUsername())
                .password(userFind.getPassword())
                .name(request.getName())
                .lastname(request.getLastname())
                .rol(userFind.getRol())
                .email(request.getEmail())
                .build();

        return repository.save(user);
    }

    public User changePassword(RegisterRequest userFind,User find) {
        User user = User.builder()
                .id(find.getId())
                .username(find.getUsername())
                .password(userFind.getPasswordChange())
                .name(find.getName())
                .lastname(find.getLastname())
                .rol(find.getRol())
                .email(find.getEmail())
                .build();

        return repository.save(user);
    }


    public User obtenerUser(String usuario) {
        User user = repository.findByUsername(usuario).orElseThrow();
        return user;
    }

    public User register(RegisterRequest request) {
        User user = User.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .name(request.getName())
                .lastname(request.getLastname())
                .rol(RoleUser.USER.name())
                .email(request.getEmail())
                .build();

        return repository.save(user);
    }

    public User registerAdmin(RegisterRequest request) {
        User user = User.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .name(request.getName())
                .lastname(request.getLastname())
                .rol(request.getRol())
                .email(request.getEmail())
                .build();

        return repository.save(user);
    }
}
