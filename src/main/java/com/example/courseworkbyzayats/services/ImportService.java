package com.example.courseworkbyzayats.services;

import com.example.courseworkbyzayats.exceptions.FileUploadException;
import com.example.courseworkbyzayats.models.Group;
import com.example.courseworkbyzayats.repositories.GroupRepository;
import com.example.courseworkbyzayats.services.validators.FileValidator;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.el.parser.Token;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

@Service
public class ImportService {
    private final FileValidator fileValidator;
    private final GroupRepository groupRepository;

    public ImportService(FileValidator fileValidator, GroupRepository groupRepository) {
        this.fileValidator = fileValidator;
        this.groupRepository = groupRepository;
    }

    public void importGroupsFromJSON(MultipartFile jsonFile) throws FileUploadException,
            IOException,
            DataIntegrityViolationException {
        try {
            fileValidator.validateFile(jsonFile, "JSON");
            List<Group> objectsList = mapJSONFileToList(jsonFile, Group.class);
            for (Group group : objectsList
                 ) {
                groupRepository.createGroup(group);
            }
        } catch (IOException | FileUploadException | DataIntegrityViolationException e) {
            throw e;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private <T> List<T> mapJSONFileToList(MultipartFile jsonFile, Class<T> object)
            throws FileUploadException, IOException, ClassNotFoundException {
        fileValidator.validateFile(jsonFile, "JSON");

        ObjectMapper mapper = new ObjectMapper();
        Class<T[]> arrayClass = (Class<T[]>) Class.forName("[L" + object.getName() + ";");
        InputStream inputStream = jsonFile.getInputStream();
        T[] objectsArray = mapper.readValue(inputStream, arrayClass);

        return Arrays.asList(objectsArray);
    }
}
