package com.example.backend.controllers;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.backend.entity.Image;
import com.example.backend.entity.News;
import com.example.backend.services.ContactService;
import com.example.backend.services.NewsService;
import java.time.LocalDate;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/news")
public class NewsController {

    private final NewsService newsService;
    @Autowired  // Ensure constructor is autowired if you're not on Spring 4.3+
    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping
    public List<News> getAllNews() {
        return newsService.getAllNews();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<News> updateNews(
            @PathVariable Long id,
            @RequestParam("title") String title,
            @RequestParam("slug") String slug,
            @RequestParam("date") String date,
            @RequestParam("content") String content,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) throws IOException {
        News updated = News.builder()
                .title(title)
                .slug(slug)
                .date(LocalDate.parse(date))
                .content(content)
                .build();
        return ResponseEntity.ok(newsService.updateNews(id, updated, image));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> deleteNews(@PathVariable Long id) {
        newsService.deleteNews(id);
        return ResponseEntity.ok().build();
    }


    
    

}

