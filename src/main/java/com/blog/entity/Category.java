package com.blog.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "category")
@Getter
@Setter
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String name;

    // Relación OneToMany con la entidad Image
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> images = new ArrayList<>();

    public Category() {
    }

    public Category(String name) {
        this.name = name;
    }

    // Métodos auxiliares para sincronizar la relación bidireccional con Image
    public void addImage(Image image) {
        images.add(image);
        image.setCategory(this);
    }

    public void removeImage(Image image) {
        images.remove(image);
        image.setCategory(null); // Corregido de setImageCategory a setCategory
    }
}