package com.proyecto.grupo_umg2024.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.proyecto.grupo_umg2024.jwt_token.JwtService;
import com.proyecto.grupo_umg2024.model.RoleUser;
import com.proyecto.grupo_umg2024.model.auth.AuthResponse;
import com.proyecto.grupo_umg2024.model.auth.LoginRequest;
import com.proyecto.grupo_umg2024.model.auth.RegisterRequest;
import com.proyecto.grupo_umg2024.model.entity.Articles;
import com.proyecto.grupo_umg2024.model.entity.User;
import com.proyecto.grupo_umg2024.model.repository.ArticlesRepository;
import com.proyecto.grupo_umg2024.model.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService implements ServiceCRUD<User> {

    @Autowired
    private UserRepository repository;

    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

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

    public UserDetails getFindUncle(String value) {
        UserDetails user = repository.findByUsername(value).orElseThrow();
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
                .password(passwordEncoder.encode(userFind.getPasswordChange()))
                .name(find.getName())
                .lastname(find.getLastname())
                .rol(find.getRol())
                .email(find.getEmail())
                .build();

        return repository.save(user);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        User user = repository.findByUsername(request.getUsername()).orElseThrow();
        String token = jwtService.getToken(user);
        return AuthResponse.builder()
                .token(token)
                .build();

    }

    public UserDetails obtenerUser(String toke) {
        UserDetails user = repository.findByUsername(jwtService.getUsernameFromToken(toke)).orElseThrow();
        return user;
    }

    public User register(RegisterRequest request) {
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
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
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .lastname(request.getLastname())
                .rol(request.getRol())
                .email(request.getEmail())
                .build();

        return repository.save(user);
    }
}
