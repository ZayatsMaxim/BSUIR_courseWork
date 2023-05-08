package com.example.courseworkbyzayats.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "group")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Group {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    Integer id;

    String name;

    @Column(name = "course_id")
    Integer courseId;
}
