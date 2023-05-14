package com.example.courseworkbyzayats.repositories;

import com.example.courseworkbyzayats.models.HomeworkInfo;
import com.example.courseworkbyzayats.models.dto.HomeworkForRatingDTO;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface HomeworkInfoRepository extends JpaRepository<HomeworkInfo, Integer> {

    @Query(
            value = "SELECT * FROM homework_info\n" +
                    "WHERE user_id = :studentId ",
            nativeQuery = true
    )
    List<HomeworkInfo> getStudentHomeworkInfo(@Param("studentId") Integer studentId);

    @Modifying
    @Transactional
    @Query(
            value = "DELETE FROM homework WHERE id = :homeworkId",
            nativeQuery = true
    )
    void deleteHomeworkInfoById(@Param("homeworkId") Integer homeworkId);

    @Query(
            value = "SELECT user_id FROM homework WHERE id = :homeworkId",
            nativeQuery = true
    )
    Integer getHomeworkStudentId(@Param("homeworkId") Integer homeworkId);
}
