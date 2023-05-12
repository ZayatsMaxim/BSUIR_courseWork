package com.example.courseworkbyzayats.services.validators;

import com.example.courseworkbyzayats.exceptions.FileUploadException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@Component
@Slf4j
public class FileValidator {

    public void validateFile(MultipartFile file, String fileType) throws FileUploadException {

        if (file.isEmpty()) {
            log.warn("Получен пустой файл: " + file.getOriginalFilename() + " тип: " + fileType);
            throw new FileUploadException("Вы не выбрали файл для загрузки!");
        }
        switch (fileType){
            case "AVATAR":
               if((Objects.equals(FilenameUtils.getExtension(file.getOriginalFilename()), "jpg")
                       || Objects.equals(FilenameUtils.getExtension(file.getOriginalFilename()), "png"))) {
                   log.warn("Получен аватар неправильного формата: " + file.getResource() + " тип: " + fileType);
                   throw new FileUploadException("Выбранный формат аватара не допускается! Выберете JPG или PNG");
               }
                break;
            case "HOMEWORK":
                // TO DO: validate homework!
                break;
            case "CONTENT":
                // TO DO: validate content!
                break;
        }

    }
}
