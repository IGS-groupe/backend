package com.example.backend.config;


import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;

public class FileUploadUtil {

    /**
     * Saves a file to the specified upload directory.
     *
     * @param uploadDir     the directory to save the file in
     * @param fileName      the name of the file to save
     * @param multipartFile the uploaded file
     */
    public static void saveFile(String uploadDir, String fileName, MultipartFile multipartFile) {
        Path uploadPath = Paths.get(uploadDir);

        // Create the directory if it doesn't exist
        if (!Files.exists(uploadPath)) {
            try {
                Files.createDirectories(uploadPath);
            } catch (IOException e) {
                throw new RuntimeException("Could not create upload directory: " + uploadDir, e);
            }
        }

        try (InputStream inputStream = multipartFile.getInputStream()) {
            Path filePath = uploadPath.resolve(fileName);

            // Copy the file, replacing it if it already exists
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ioe) {
            throw new RuntimeException("Could not save file: " + fileName, ioe);
        }
    }
}
