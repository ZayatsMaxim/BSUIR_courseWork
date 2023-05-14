package com.example.courseworkbyzayats.controllers;

import com.example.courseworkbyzayats.models.Course;
import com.example.courseworkbyzayats.models.Group;
import com.example.courseworkbyzayats.models.User;
import com.example.courseworkbyzayats.models.dto.CourseStatsDTO;
import com.example.courseworkbyzayats.services.StatisticsService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.DocumentException;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@Secured("ROLE_ADMIN")
@RequestMapping("/zayct/stats")
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/coursesJSON")
    public ResponseEntity<InputStreamResource> getAllCoursesInJSON() throws JsonProcessingException {
        List<Course> allCourses = statisticsService.getAllCoursesListForJSON();

        return prepareJSONResponseEntity(allCourses, "courseTable");
    }

    @GetMapping("/groupsJSON")
    public ResponseEntity<InputStreamResource> getAllGroupsInJSON() throws JsonProcessingException {
        List<Group> allGroups = statisticsService.getAllGroupsListForJSON();

        return prepareJSONResponseEntity(allGroups, "groupTable");
    }

    @GetMapping("/usersJSON")
    public ResponseEntity<InputStreamResource> getAllUsersInJSON() throws JsonProcessingException {
        List <User> allUsers = statisticsService.getAllUsersListForJSON();

        return prepareJSONResponseEntity(allUsers, "userTable");
    }

    @GetMapping("/groupPDFReport/{groupId}")
    public ResponseEntity<InputStreamResource> getGroupPDFReport(@PathVariable Integer groupId)
            throws DocumentException, IOException {
        ByteArrayInputStream pdfInputStream = statisticsService.createGroupPDFReport(groupId);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=groupId" + groupId + "Report.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(new InputStreamResource(pdfInputStream));
    }

    @GetMapping("/coursesComparison")
    public String getCourseComparison(Model model) {
        List<CourseStatsDTO> coursesStudentsAmounts = statisticsService.getCoursesStudentsAmount();

        List<String> courses = coursesStudentsAmounts.stream().map(CourseStatsDTO
                ::getName).collect(Collectors.toList());
        List<Integer> studentsAmounts = coursesStudentsAmounts.stream().map(CourseStatsDTO
                ::getTotal_stud).collect(Collectors.toList());

        model.addAttribute("courses", courses);
        model.addAttribute("studentsAmounts", studentsAmounts);

        return "coursesStats";
    }

    private ResponseEntity<InputStreamResource> prepareJSONResponseEntity(List<?> objects,
                                                                          String fileName)
            throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        byte[] buffer = mapper.writeValueAsBytes(objects);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(buffer);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        headers.add("Content-Disposition", "attachment; filename=" + fileName + ".json");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentLength(buffer.length)
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(new InputStreamResource(inputStream));
    }
}
