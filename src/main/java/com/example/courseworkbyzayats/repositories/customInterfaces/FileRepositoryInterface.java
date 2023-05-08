package com.example.courseworkbyzayats.repositories.customInterfaces;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileRepositoryInterface {
    void save(MultipartFile file, String filePath) throws IOException;
}
