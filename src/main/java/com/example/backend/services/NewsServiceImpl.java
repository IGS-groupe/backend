package com.example.backend.services;

import com.example.backend.entity.Image;
import com.example.backend.entity.News;
import com.example.backend.config.FileUploadUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NewsServiceImpl implements NewsService {

    private final com.example.backend.repository.NewsRepository newsRepository;

    @Override
    public News createNews(News news, MultipartFile image) throws IOException {
        if (image != null && !image.isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
            FileUploadUtil.saveFile("uploads", fileName, image);
            Image img = new Image();
            img.setFileName(fileName);
            img.setContentType(image.getContentType());
            img.setUrl("/uploads/" + fileName);
            img.setFilePath("uploads\\" + fileName);
            news.setImage(img);
        }
        return newsRepository.save(news);
    }

    @Override
    public List<News> getAllNews() {
        return newsRepository.findAll();
    }

    @Override
    public News getNewsBySlug(String slug) {
        return newsRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("News not found with slug: " + slug));
    }

    @Override
    public News getNewsById(Long id) {
        return newsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("News not found with id: " + id));
    }

    @Override
    public News updateNews(Long id, News updatedNews, MultipartFile image) throws IOException {
        News existing = getNewsById(id);
        existing.setTitle(updatedNews.getTitle());
        existing.setSlug(updatedNews.getSlug());
        existing.setDate(updatedNews.getDate());
        existing.setContent(updatedNews.getContent());

        if (image != null && !image.isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
            FileUploadUtil.saveFile("uploads", fileName, image);
            Image img = new Image();
            img.setFileName(fileName);
            img.setContentType(image.getContentType());
            img.setUrl("/uploads/" + fileName);
            img.setFilePath("uploads\\" + fileName);
            existing.setImage(img);
        }

        return newsRepository.save(existing);
    }

    @Override
    public void deleteNews(Long id) {
        newsRepository.deleteById(id);
    }
}
