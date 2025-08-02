package com.example.backend.config;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FileUploadUtil {

    public static String saveFile(String uploadDir, String fileName, MultipartFile multipartFile) {
        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            try {
                Files.createDirectories(uploadPath);
            } catch (IOException e) {
                throw new RuntimeException("Could not create upload directory: " + uploadDir, e);
            }
        }

        try (InputStream inputStream = multipartFile.getInputStream()) {
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            return filePath.toString();
        } catch (IOException ioe) {
            throw new RuntimeException("Could not save file: " + fileName, ioe);
        }
    }

    /**
     * Saves a user image under /uploads/users/{userId}/ with a unique timestamp filename
     * @param userId the ID of the user
     * @param imageFile the image file to save
     * @return the relative path to store in the database (e.g., "/uploads/users/5/profile_20250802_1400.png")
     */
    public static String saveUserImage(Long userId, MultipartFile imageFile) {
        if (imageFile == null || imageFile.isEmpty()) {
            return null;
        }

        // Base folder for user uploads
        String uploadDir = "uploads/users/" + userId;

        // Generate unique filename based on timestamp
        String originalFilename = imageFile.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String fileName = "profile_" + timestamp + extension;

        saveFile(uploadDir, fileName, imageFile);

        // Return a relative path for DB
        return "/" + uploadDir + "/" + fileName;
    }
}
