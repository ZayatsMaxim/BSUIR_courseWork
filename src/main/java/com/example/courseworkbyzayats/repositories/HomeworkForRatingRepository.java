package com.example.courseworkbyzayats.repositories;

import com.example.courseworkbyzayats.models.dto.HomeworkForRatingDTO;
import jakarta.persistence.NamedNativeQuery;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface HomeworkForRatingRepository extends JpaRepository<HomeworkForRatingDTO, Integer> {

    @Query(
            value = "SELECT homework_info.id, `user`.`name` AS student_name, homework_info.rating, course.`name` AS course_name, " +
                    "homework_info.file_name AS content_name FROM homework_info\n" +
                    "JOIN course ON course_id = course.id\n" +
                    "JOIN `user` ON user_id = `user`.id\n" +
                    "WHERE teacher_id = :teacherId AND rating = 0\n" +
                    "ORDER BY course_id ",
            countQuery = "SELECT count(*) FROM homework_info\n" +
                    "JOIN course ON course_id = course.id\n" +
                    "JOIN `user` ON user_id = `user`.id\n" +
                    "WHERE teacher_id = :teacherId AND rating = 0\n" +
                    "ORDER BY course_id ",
            nativeQuery = true
    )
    Page<HomeworkForRatingDTO> getTeacherHomeworkInfoForRating(@Param("teacherId") Integer teacherId,
                                                               Pageable pageable);

    @Modifying
    @Transactional
    @Query(
            value = "UPDATE homework\n" +
                    "SET rating = :rate WHERE id = :homeworkId ",
            nativeQuery = true
    )
    void saveHomeworkRating(@Param("homeworkId") Integer homeworkId, @Param("rate") Integer rate);

    @Query(
            value = "SELECT content FROM homework WHERE id=:homeworkId",
            nativeQuery = true
    )
    String getHomeworkPath(@Param("homeworkId") Integer homeworkId);

    @Query(
            value = "SELECT file_name FROM homework_info WHERE id=:homeworkId",
            nativeQuery = true
    )
    String getHomeworkFilename(@Param("homeworkId") Integer homeworkId);
}
