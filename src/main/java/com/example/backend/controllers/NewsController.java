package com.example.backend.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.backend.dto.NewsDTO;
import com.example.backend.entity.News;
import com.example.backend.services.NewsService;
import java.time.LocalDate;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/news")
public class NewsController {

    private final NewsService newsService;

    @Autowired
    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    // Add a test endpoint without @PreAuthorize to check if authentication works
    @PostMapping("/test")
    public ResponseEntity<String> testEndpoint(
            @RequestParam("title") String title,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("=== DEBUG INFO ===");
        System.out.println("Authentication object: " + auth);
        System.out.println("Is authenticated: " + (auth != null ? auth.isAuthenticated() : "null"));
        System.out.println("Principal: " + (auth != null ? auth.getPrincipal() : "null"));
        System.out.println("Authorities: " + (auth != null ? auth.getAuthorities() : "null"));
        System.out.println("Title received: " + title);
        System.out.println("File received: " + (image != null ? image.getOriginalFilename() : "null"));
        System.out.println("==================");
        
        return ResponseEntity.ok("Test successful - Authentication working");
    }

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<News> add(
            @RequestParam("title") String title,
            @RequestParam("slug") String slug,
            @RequestParam("date") String date,
            @RequestParam("content") String content,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) throws IOException {
        
        // This should not be reached if @PreAuthorize fails
        System.out.println("=== INSIDE ADD METHOD ===");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("User: " + auth.getName());
        System.out.println("Authorities: " + auth.getAuthorities());
        
        News news = News.builder()
                .title(title)
                .slug(slug)
                .date(LocalDate.parse(date))
                .content(content)
                .build();

        News created = newsService.createNews(news, image);
        return ResponseEntity.ok(created);
    }

    @GetMapping
    public List<NewsDTO> getAllNews() {
        return newsService.getAllNews();
    }
    @GetMapping("/slug/{slug}")
    // @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> getNewsBySlug(@PathVariable String slug) {
        try {
            NewsDTO newsDTO = newsService.getNewsBySlug(slug);
            if (newsDTO != null) {
                return ResponseEntity.ok(newsDTO);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace(); // Log dans la console
            return ResponseEntity.status(500).body("Erreur lors de la récupération de la news : " + e.getMessage());
        }
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
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