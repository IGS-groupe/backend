package com.example.backend.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.entity.News;

public interface NewsRepository extends JpaRepository<News, Long> {
    Optional<News> findBySlug(String slug);    
    Optional<News> findById(Long id);
}

