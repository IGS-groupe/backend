package com.example.backend.repository;

import com.example.backend.entity.ProjectCard;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectCardRepository extends JpaRepository<ProjectCard, Long> {
    default List<ProjectCard> findAllOrdered() {
        return findAll(Sort.by(Sort.Order.asc("sortIndex"), Sort.Order.asc("id")));
    }
}
