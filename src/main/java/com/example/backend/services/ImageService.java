package com.example.backend.services;

import com.example.backend.entity.Image;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImageService {
    Image storeImage(MultipartFile file) throws IOException;
}

