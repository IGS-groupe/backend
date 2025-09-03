package com.example.backend.controllers;


import com.example.backend.entity.ProjectCard;
import com.example.backend.services.ProjectCardService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/project-cards")
public class ProjectCardController {

    private final ProjectCardService service;

    // ------- READ -------
    @GetMapping
    public List<ProjectCard> list() {
        return service.list();
    }

    @GetMapping("/{id}")
    public ProjectCard get(@PathVariable Long id) {
        return service.get(id);
    }

    // ------- WRITE (admin) -------
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    @PostMapping
    public ResponseEntity<ProjectCard> create(@RequestBody ProjectCard body) {
        return ResponseEntity.ok(service.create(body));
    }

    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    @PutMapping("/{id}")
    public ProjectCard update(@PathVariable Long id, @RequestBody ProjectCard body) {
        return service.update(id, body);
    }

    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    @PatchMapping("/{id}/active")
    public ProjectCard setActive(@PathVariable Long id, @RequestParam boolean active) {
        return service.setActive(id, active);
    }

    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    @PostMapping("/reorder")
    public List<ProjectCard> reorder(@RequestBody List<Long> orderedIds) {
        return service.reorder(orderedIds);
    }

    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
