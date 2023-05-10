package com.example.courseworkbyzayats.repositories;

import com.example.courseworkbyzayats.models.Course;
import com.example.courseworkbyzayats.models.dto.CourseSaveDTO;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Integer> {

    @Query(
            value = "SELECT * FROM courseAndTeacher",
            countQuery = "SELECT count(*) FROM courseAndTeacher",
            nativeQuery = true
    )
    Page<Course> findAllCourses(Pageable pageable);

    @Query(
            value = "SELECT * FROM courseAndTeacher WHERE name LIKE :title",
            countQuery = "SELECT count(*) FROM courseAndTeacher WHERE name LIKE :title",
            nativeQuery = true
    )
    List<Course> findCoursesByTitle(@Param("title") String title);

    @Query(
            value = "SELECT * FROM courseandteacher\n" +
                    "WHERE teacher_name LIKE :teacherName ",
            nativeQuery = true
    )
    List<Course> findAllCoursesByTeacherName(@Param("teacherName") String teacherName);

    @Query(
            value = "SELECT * FROM courseAndTeacher WHERE id = :id",
            nativeQuery = true
    )
    Course findCourseById(@Param("id") Integer courseId);

    @Query(
            value = "SELECT course.id, course.`name`, course.icon, `user`.`name` AS teacher_name ,course.description, course.test_to_pass_amount "+
                    "FROM studentsgroups\n" +
                    "JOIN `group` ON group_id = `group`.id\n" +
                    "JOIN course ON course_id = course.id\n" +
                    "JOIN `user` ON teacher_id = `user`.id\n" +
                    "WHERE student_id = :userId",
            countQuery = "SELECT count(*) FROM studentsgroups\n" +
                    "JOIN `group` ON group_id = `group`.id\n" +
                    "JOIN course ON course_id = course.id\n" +
                    "WHERE student_id = :userId",
            nativeQuery = true
    )
    Page<Course> findStudentCourses(@Param("userId") Integer userId, Pageable pageable);

    @Query(
            value = "SELECT course_id FROM `group` " +
                    "JOIN course ON course_id = course.id " +
                    "WHERE `group`.id = :groupId ",
            nativeQuery = true
    )
    Integer findCourseIdByGroupId(@Param("groupId") Integer groupId);

    @Query(
            value = "SELECT course.id, course.`name`, icon, `user`.`name` AS teacher_name, `description`, test_to_pass_amount FROM `course`\n" +
                    "JOIN `user` ON `user`.id = teacher_id\n" +
                    "WHERE teacher_id = :teacherId ",
            countQuery = "SELECT count(*) FROM `course` " +
                         "JOIN `user` ON `user`.id = teacher_id\n" +
                         "WHERE teacher_id=:teacherId ",
            nativeQuery = true
    )
    Page<Course> findTeacherCourses(@Param("teacherId") Integer teacherId, Pageable pageable);

    @Modifying
    @Transactional
    @Query(
            value = "INSERT INTO course(`name`, icon, teacher_id, description, test_to_pass_amount)\n" +
                    "VALUES (:#{#course.name}, :#{#course.icon}, :teacherId, :#{#course.description}, :#{#course.testToPassAmount}) ",
            nativeQuery = true
    )
    void saveCourse(@Param("course") CourseSaveDTO course, @Param("teacherId") Integer teacherId);

    @Query(
            value = "SELECT `name` FROM course WHERE id = :courseId ",
            nativeQuery = true
    )
    String getCourseNameById(@Param("courseId") Integer courseId);
}
