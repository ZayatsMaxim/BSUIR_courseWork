package com.example.courseworkbyzayats.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="coursecontent")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ContentInfo {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "course_id")
    private Integer courseId;

    @Column(name = "content")
    private String contentPath;

    @NotNull
    private String fileType;

    @NotNull
    private String fileName;

    @NotNull
    private String description;

    public ContentInfo(Integer courseId, String fileType, String fileName, String description) {
        this.courseId = courseId;
        this.fileType = fileType;
        this.fileName = fileName;
        this.description = description;
    }
}
