package com.blog.repository;

import com.blog.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    // Al igual que los anteriores, heredamos de JpaRepository para las operaciones de la entidad Image.
}