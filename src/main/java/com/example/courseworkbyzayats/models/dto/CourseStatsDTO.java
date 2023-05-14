package com.example.courseworkbyzayats.models.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "courseStats")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CourseStatsDTO {
    @Id

    private Integer id;

    private String name;
    private Integer total_stud;
}
