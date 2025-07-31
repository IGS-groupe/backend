package com.example.backend.services;

import com.example.backend.dto.NewsDTO;
import com.example.backend.entity.News;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface NewsService {
    News createNews(News news, MultipartFile image) throws IOException;
    List<NewsDTO> getAllNews();
    NewsDTO getNewsBySlug(String slug);
    NewsDTO getNewsByIdDTO(Long id);
    News updateNews(Long id, News news, MultipartFile image) throws IOException;
    void deleteNews(Long id);
}
