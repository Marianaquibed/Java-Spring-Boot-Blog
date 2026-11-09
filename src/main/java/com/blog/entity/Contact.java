package com.blog.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "contact")
@Getter
@Setter
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Field `First Name` is mandatory")
    @Column(name = "first_name", nullable = false, length = 255)
    private String firstName;

    @NotBlank(message = "Field `Last Name` is mandatory")
    @Column(name = "last_name", nullable = false, length = 255)
    private String lastName;

    @NotBlank(message = "Field `email` is mandatory")
    @Email(message = "{value} is not a valid email")
    @Column(nullable = false, length = 255)
    private String email;

    @NotBlank(message = "Field `Subject` is mandatory")
    @Column(nullable = false, length = 255)
    private String subject;

    @NotBlank(message = "Field `Message` is mandatory")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String message;

    public Contact() {
    }
}