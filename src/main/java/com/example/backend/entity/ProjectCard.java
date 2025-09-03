package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@Entity
@Table(name = "project_cards")
public class ProjectCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** e.g. "Lab Auditing & Operation" */
    @Column(nullable = false, length = 200)
    private String title;

    /** Optional short tag or group, e.g. "Hydromet", "Environment" */
    @Column(length = 100)
    private String category;

    /** Order on page/grid */
    @Column(name = "sort_index")
    private Integer sortIndex;

    /** Bulleted lines under the title */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "project_card_bullets",
        joinColumns = @JoinColumn(name = "card_id")
    )
    @Column(name = "text", nullable = false, columnDefinition = "TEXT")
    @OrderColumn(name = "line_index")
    private List<String> bullets = new ArrayList<>();

    @Builder.Default
    private boolean active = true;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;
}
