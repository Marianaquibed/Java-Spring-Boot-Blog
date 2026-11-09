package com.blog.repository;

import com.blog.entity.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
    // Al igual que los anteriores, heredamos de JpaRepository para gestionar las operaciones CRUD de Contact.
}