package com.blog.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user", uniqueConstraints = {
        @UniqueConstraint(name = "UNIQ_IDENTIFIER_EMAIL", columnNames = { "email" })
})
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email")
    @Column(nullable = false, length = 180, unique = true)
    private String email;

    @NotBlank(message = "Name is required")
    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false)
    private String roles = "ROLE_USER";

    @Column(nullable = false)
    private String password;

    @OneToMany(mappedBy = "postUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    public User() {
    }

    public String getUserIdentifier() {
        return this.email;
    }

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

    public void addPost(Post post) {
        posts.add(post);
        post.setPostUser(this);
    }

    public void removePost(Post post) {
        posts.remove(post);
        post.setPostUser(null);
    }
}