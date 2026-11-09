package com.blog.repository;

import com.blog.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    // Aquí puedes añadir métodos de consulta personalizados si los necesitas en el futuro,
    // equivalentes a los métodos comentados que tenías en Doctrine.
}