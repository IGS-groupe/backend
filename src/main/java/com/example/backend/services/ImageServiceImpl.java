package com.example.backend.services;

import com.example.backend.config.FileUploadUtil;
import com.example.backend.entity.Image;
import com.example.backend.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;

    @Value("${upload.path}")
    private String uploadPath;

    @Override
    public Image storeImage(MultipartFile file) throws IOException {
        String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();

        // Save the file using the utility class
        FileUploadUtil.saveFile(uploadPath, filename, file);

        // Build and save image metadata
        Image image = Image.builder()
                .fileName(filename)
                .contentType(file.getContentType())
                .url("/uploads/" + filename)
                .filePath(Paths.get(uploadPath, filename).toString())
                .build();

        return imageRepository.save(image);
    }
}
