package com.example.courseworkbyzayats.repositories;

import com.example.courseworkbyzayats.models.ContentInfo;
import jakarta.transaction.Transactional;
import org.hibernate.annotations.Cascade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContentInfoRepository extends JpaRepository<ContentInfo,Integer> {

    @Query(
            value = "SELECT * FROM coursecontent\n" +
                    "WHERE id = :contentId ",
            nativeQuery = true
    )
    ContentInfo getContentInfoById(@Param("contentId") Integer contentId);

    @Query(
            value = "SELECT content FROM coursecontent\n" +
                    "WHERE id = :contentId ",
            nativeQuery = true
    )
    String getContentFilePath(@Param("contentId") Integer contentId);

    @Query(  value = "SELECT * FROM coursecontent\n" +
                     "WHERE course_id = :courseId AND file_type = :fileType ",
            nativeQuery = true)
    List<ContentInfo> getCourseContentInfo(@Param("courseId") Integer courseId,
                                           @Param("fileType") String fileType);

    @Query(
            value = "SELECT file_name FROM coursecontent\n" +
                    "WHERE id = :contentId",
            nativeQuery = true
    )
    String getContentFilenameById(@Param("contentId") Integer contentId);

    @Modifying
    @Transactional
    @Query(
            value = "INSERT INTO homework(type, content, rating, user_id, coursecontent_id) VALUES " +
            "(:type, :filePath, 0, :studentId, :courseContentId) ",
            nativeQuery = true
    )
    void saveHomework(@Param("filePath") String filePath,
                      @Param("type") String type,
                      @Param("studentId") Integer studentId,
                      @Param("courseContentId") Integer courseContentId);

    @Query(
            value = "SELECT test_to_pass_amount FROM course WHERE id=:courseId ",
            nativeQuery = true
    )
    Short getCourseTestToPassAmount(@Param("courseId") Integer courseId);

    @Query(
            value = "SELECT count(*) FROM homework\n" +
                    "JOIN coursecontent ON coursecontent.id = coursecontent_id\n" +
                    "WHERE course_id = :courseId AND user_id = :studentId " +
                    "AND `type` = 'TEST' AND rating >= :minRating " ,
            nativeQuery = true
    )
    Integer getStudentCompletedTestNumber(@Param("studentId") Integer studentId,
                                          @Param("courseId") Integer courseId,
                                          @Param("minRating") Integer minRating);

    @Modifying
    @Transactional
    @Query(
            value = "INSERT INTO coursecontent(course_id, content, file_type, file_name, `description`)\n" +
                    "VALUES (:courseId, :contentPath, :contentType, :contentName, :description) ",
            nativeQuery = true
    )
    void saveTeacherContent(@Param("courseId") Integer courseId,
            @Param("contentPath") String contentPath,
            @Param("contentType") String contentType,
            @Param("contentName") String contentName,
            @Param("description") String description);

    @Modifying
    @Transactional
    @Query(
            value = "DELETE FROM coursecontent WHERE id=:contentId",
            nativeQuery = true
    )
    void deleteContentInfoById(@Param("contentId") Integer contentId);

    @Query(
            value = "SELECT teacher_id FROM coursecontent " +
                    "JOIN course ON course_id = course.id " +
                    "JOIN `user` ON teacher_id = `user`.id " +
                    "WHERE coursecontent.id = :teacherId ",
            nativeQuery = true
    )
    Integer getContentTeacherId(@Param("teacherId") Integer teacherId);
}
