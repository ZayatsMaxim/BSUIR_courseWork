package com.example.courseworkbyzayats.repositories;

import com.example.courseworkbyzayats.models.Group;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GroupRepository extends JpaRepository<Group,Integer> {

    @Query(
            value = "SELECT * FROM `group` WHERE id = :groupId",
            nativeQuery = true
    )
    Group findGroupById(@Param("groupId") Integer groupId);

    @Query(
            value = "SELECT * FROM `group` WHERE course_id=:courseId",
            nativeQuery = true
    )
    List<Group> findGroupsByCourseId(@Param("courseId") Integer courseId);

    @Query(
            value = "SELECT * FROM `group` WHERE `name`= :name",
            nativeQuery = true
    )
    Group findGroupByName(@Param("name") String name);

    @Modifying
    @Transactional
    @Query(
            value = "INSERT INTO studentsgroups(group_id,student_id) VALUES (:groupId,:studentId)",
            nativeQuery = true
    )
    void registerStudentForGroup(@Param("groupId") Integer groupId, @Param("studentId") Integer studentId);

    @Query(
            value = "SELECT count(*) FROM studentsgroups WHERE student_id = :studentId AND group_id = :groupId",
            nativeQuery = true
    )
    int howManyTimesStudentIsRegisteredInGroup(@Param("groupId") Integer groupId,
                                               @Param("studentId") Integer studentId);

    @Query(
            value = "SELECT count(*) FROM studentsgroups " +
                    "JOIN `group` ON group_id = `group`.id " +
                    "JOIN course ON course_id = course.id " +
                    "WHERE student_id = :studentId AND course_id = :courseId",
            nativeQuery = true
    )
    int howManyTimesStudentIsRegisteredForCourse(@Param("courseId") Integer courseId,
                                                 @Param("studentId") Integer studentId);

    @Query(
            value = "SELECT * FROM `group`",
            countQuery = "SELECT count(*) FROM `group`",
            nativeQuery = true)
    Page<Group> getAllGroupsPage(Pageable pageable);

    @Modifying
    @Transactional
    @Query(
            value = "INSERT INTO `group`(`name`, course_id) VALUE (:#{#group.name}, :#{#group.courseId})",
            nativeQuery = true
    )
    void createGroup(@Param("group") Group group);

}
