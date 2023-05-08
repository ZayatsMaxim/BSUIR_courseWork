package com.example.courseworkbyzayats.repositories;

import com.example.courseworkbyzayats.repositories.customInterfaces.FileRepositoryInterface;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;

@Repository
public class FileRepository implements FileRepositoryInterface {

    @Override
    public void save(MultipartFile file, String filePath) throws IOException {
       byte[] data = file.getBytes();
        try {
            FileOutputStream fileOut = new FileOutputStream(filePath);
            fileOut.write(data);
            fileOut.close();
        } catch (IOException e) {
            throw e;
        }
    }
}
