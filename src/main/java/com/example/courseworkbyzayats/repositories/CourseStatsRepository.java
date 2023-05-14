package com.example.courseworkbyzayats.repositories;

import com.example.courseworkbyzayats.models.dto.CourseStatsDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CourseStatsRepository extends JpaRepository<CourseStatsDTO, Integer> {

    @Query(
            value = "SELECT * FROM courseStats",
            nativeQuery = true
    )
    List<CourseStatsDTO> getCoursesStudentsAmounts();
}
