package com.example.courseworkbyzayats.models.dto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Entity
//@Table(name = "course")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CourseSaveDTO {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    Integer id;

    String name;
    String icon;
    String teacherId;
    String description;
    Short testToPassAmount;
}
