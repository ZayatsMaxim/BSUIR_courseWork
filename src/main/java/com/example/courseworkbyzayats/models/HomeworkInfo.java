package com.example.courseworkbyzayats.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "homework_info")
@Getter
@Setter
@NoArgsConstructor
public class HomeworkInfo {

    @Id
    @GeneratedValue
    private Integer id;

    private Integer userId;
    private Integer rating;
    private Integer courseId;
    private String fileName;
}
