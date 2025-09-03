package com.example.backend.services;



import com.example.backend.entity.ProjectCard;
import com.example.backend.repository.ProjectCardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectCardService {

    private final ProjectCardRepository repo;

    // --------- READ ----------
    @Transactional(readOnly = true)
    public List<ProjectCard> list() {
        return repo.findAllOrdered();
    }

    @Transactional(readOnly = true)
    public ProjectCard get(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project card not found"));
    }

    // --------- CREATE / UPDATE ----------
    public ProjectCard create(ProjectCard in) {
        // ignore client-provided id/timestamps
        if (in.getId() != null) in.setId(null);
        return repo.save(in);
    }

    public ProjectCard update(Long id, ProjectCard in) {
        ProjectCard pc = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project card not found"));

        // Only copy fields we actually allow to change
        pc.setTitle(in.getTitle());
        pc.setCategory(in.getCategory());
        pc.setSortIndex(in.getSortIndex());
        pc.setBullets(in.getBullets() == null ? List.of() : in.getBullets());
        pc.setActive(in.isActive());

        return pc; // managed entity; will be flushed on tx commit
    }

    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Project card not found");
        }
        repo.deleteById(id);
    }

    public ProjectCard setActive(Long id, boolean active) {
        ProjectCard pc = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project card not found"));
        pc.setActive(active);
        return pc;
    }

    /** Reorder cards by a provided ordered list of IDs (0..n). */
    public List<ProjectCard> reorder(List<Long> orderedIds) {
        int idx = 0;
        for (Long id : orderedIds) {
            ProjectCard pc = repo.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project card not found: " + id));
            pc.setSortIndex(idx++);
        }
        return list();
    }
}
