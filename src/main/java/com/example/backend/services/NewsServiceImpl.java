package com.example.backend.services;

import com.example.backend.config.FileUploadUtil;
import com.example.backend.dto.NewsDTO;
import com.example.backend.entity.Image;
import com.example.backend.entity.News;
import com.example.backend.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NewsServiceImpl implements NewsService {

    private final NewsRepository newsRepository;
    
    @Value("${upload.path}")
    private String uploadPath;
    
    @Value("${server.port:4000}")
    private String serverPort;

    @Override
    @Transactional
    public News createNews(News news, MultipartFile imageFile) throws IOException {
        // Handle image upload if provided
        if (imageFile != null && !imageFile.isEmpty()) {
            Image image = saveImageFile(imageFile);
            news.setImage(image);
        }
        
        return newsRepository.save(news);
    }
    @Override
    @Transactional(readOnly = true)
    public NewsDTO getNewsBySlug(String slug) {
        News news = newsRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("News not found with slug: " + slug));

        return new NewsDTO(
                news.getId(),
                news.getTitle(),
                news.getSlug(),
                news.getContent(),
                news.getDate(),
                news.getImage() != null ? news.getImage().getUrl() : null
        );
    }


    @Override
    @Transactional
    public News updateNews(Long id, News updatedNews, MultipartFile imageFile) throws IOException {
        News existingNews = newsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("News not found with id: " + id));
        
        // Update basic fields
        existingNews.setTitle(updatedNews.getTitle());
        existingNews.setSlug(updatedNews.getSlug());
        existingNews.setDate(updatedNews.getDate());
        existingNews.setContent(updatedNews.getContent());
        
        // Handle image update if provided
        if (imageFile != null && !imageFile.isEmpty()) {
            Image newImage = saveImageFile(imageFile);
            existingNews.setImage(newImage);
        }
        
        return newsRepository.save(existingNews);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NewsDTO> getAllNews() {
        return newsRepository.findAll().stream().map(news -> {
            return new NewsDTO(
                news.getId(),
                news.getTitle(),
                news.getSlug(),
                news.getContent(),
                news.getDate(),
                news.getImage() != null ? news.getImage().getUrl() : null
            );
        }).collect(Collectors.toList());
    }


    @Override
    @Transactional
    public void deleteNews(Long id) {
        newsRepository.deleteById(id);
    }

    // ✅ Implement missing getNewsById method
    @Override
    public News getNewsById(Long id) {
        return newsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("News not found with id: " + id));
    }
    
    // ✅ Implement missing getNewsBySlug method
    // @Override
    // public News getNewsBySlug(String slug) {
    //     // Assuming you have this method in your repository
    //     // If not, we'll need to implement it or use a query method
    //     return newsRepository.findBySlug(slug)
    //             .orElseThrow(() -> new RuntimeException("News not found with slug: " + slug));
    // }

    private Image saveImageFile(MultipartFile file) throws IOException {
        // Fix for the null type mismatch
        String originalFileName = file != null ? StringUtils.cleanPath(file.getOriginalFilename() != null ? 
                                file.getOriginalFilename() : "unnamed") : "unnamed";
        
        String fileName = UUID.randomUUID().toString() + "_" + originalFileName;
        
        // Save file to filesystem
        FileUploadUtil.saveFile(uploadPath, fileName, file);
        
        // Generate the public URL for accessing the image
        String imageUrl = "http://localhost:" + serverPort + "/uploads/" + fileName;
        
        // Create and return Image entity
        return Image.builder()
                .fileName(originalFileName)
                .filePath(uploadPath + "/" + fileName)
                .url(imageUrl) // Now using the url field
                .contentType(file.getContentType())
                .fileSize(file.getSize())
                .build();
    }
}