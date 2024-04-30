package com.example.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.backend.entity.Contact;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
}
