package com.blog.repository;

import com.blog.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    // Equivalente a findRecents(): Devuelve los 5 posts más recientes ordenados por fecha de publicación descendente
    List<Post> findTop5ByOrderByPublishedAtDesc();

    // Equivalente a findPaginados: Spring Boot maneja la paginación de forma nativa con Pageable
    // Devuelve un objeto Page que contiene tanto la lista de posts como el total de registros para la paginación.
    Page<Post> findAllByOrderByPublishedAtDesc(Pageable pageable);

    // Equivalente a findBuscadosPaginados: Busca por contenido (LIKE) y pagina los resultados
    @Query("SELECT p FROM Post p WHERE p.content LIKE %:searchTerm% ORDER BY p.publishedAt DESC")
    Page<Post> findByContentContainingIgnoreCaseOrderByPublishedAtDesc(@Param("searchTerm") String searchTerm, Pageable pageable);
}