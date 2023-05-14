package com.example.courseworkbyzayats.controllers;

import com.example.courseworkbyzayats.exceptions.FileUploadException;
import com.example.courseworkbyzayats.models.Group;
import com.example.courseworkbyzayats.services.ImportService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequestMapping("/zayct/import")
public class ImportController {
    private final ImportService importService;

    public ImportController(ImportService importService) {
        this.importService = importService;
    }

    @PostMapping("/groups")
    public String importGroupsJSON(@RequestParam("file") MultipartFile JSONFile, Model model) {
        try {
            importService.importGroupsFromJSON(JSONFile);
        } catch (DataIntegrityViolationException e) {
            model.addAttribute("errorMessage", "Данные в файле нарушают целостность данных!");
            return "uploadFailed";
        } catch (FileUploadException | IOException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "uploadFailed";
        }

        return "redirect:/zayct/courses/user/admin/groupsList/page/1";
    }
}
