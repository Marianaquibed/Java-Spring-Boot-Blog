package com.blog.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user", uniqueConstraints = {
    @UniqueConstraint(name = "UNIQ_IDENTIFIER_EMAIL", columnNames = {"email"})
})
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 180, unique = true)
    private String email;

    @Column(nullable = false, length = 255)
    private String name;

    // En JPA guardamos los roles como un String separado por comas o JSON, 
    // por defecto almacenaremos una cadena simple como "ROLE_USER,ROLE_ADMIN"
    @Column(nullable = false)
    private String roles = "ROLE_USER";

    @Column(nullable = false)
    private String password;

    // Relación OneToMany con Post (equivalente a mappedBy = "postUser")
    @OneToMany(mappedBy = "postUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    public User() {
    }

    // Métodos equivalentes para la gestión de roles asegurando que siempre tenga ROLE_USER
    public List<String> getRolesList() {
        List<String> roleList = new ArrayList<>();
        if (this.roles != null && !this.roles.isEmpty()) {
            for (String role : this.roles.split(",")) {
                roleList.add(role.trim());
            }
        }
        if (!roleList.contains("ROLE_USER")) {
            roleList.add("ROLE_USER");
        }
        return roleList;
    }

    public void setRolesList(List<String> roleList) {
        this.roles = String.join(",", roleList);
    }

    // Métodos auxiliares para sincronizar la relación bidireccional con Post
    public void addPost(Post post) {
        posts.add(post);
        post.setPostUser(this);
    }

    public void removePost(Post post) {
        posts.remove(post);
        post.setPostUser(null);
    }
}