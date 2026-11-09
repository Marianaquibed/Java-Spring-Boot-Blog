package com.blog.repository;

import com.blog.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Equivalente a loadUserByIdentifier: Busca un usuario por su email
    // Spring Data JPA permite usar Optional<User> para manejar de forma limpia cuando el usuario no existe.
    Optional<User> findByEmail(String email);

    // Si en tu entidad o base de datos también necesitas buscar por nombre/username además de email:
    @Query("SELECT u FROM User u WHERE u.email = :query OR u.name = :query")
    Optional<User> findByEmailOrName(@Param("query") String query);
}