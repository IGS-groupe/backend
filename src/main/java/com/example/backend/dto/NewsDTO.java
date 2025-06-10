package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class NewsDTO {
    private Long id;
    private String title;
    private String slug;
    private String content;
    private LocalDate date;
    private String imageUrl;
}