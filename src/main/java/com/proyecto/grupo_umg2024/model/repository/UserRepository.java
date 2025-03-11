package com.proyecto.grupo_umg2024.model.repository;
import java.util.Optional;
import com.proyecto.grupo_umg2024.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

            
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);
    Optional<User> findByUsername(String username);
}
