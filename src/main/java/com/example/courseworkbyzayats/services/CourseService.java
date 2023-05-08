package com.example.courseworkbyzayats.services;

import com.example.courseworkbyzayats.models.Course;
import com.example.courseworkbyzayats.models.dto.CourseSaveDTO;
import com.example.courseworkbyzayats.repositories.CourseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final static int PAGE_SIZE = 8;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public Page<Course> getAllCourses(int pageNumber){
        Pageable pageable = PageRequest.of(pageNumber - 1,PAGE_SIZE);
        return courseRepository.findAllCourses(pageable);
    }

    public List<Course> getCoursesByTitle(String title){
        String titleWithPercentsMarks = "%" + title + "%";
        return courseRepository.findCoursesByTitle(titleWithPercentsMarks);
    }

    public List<Course> getCoursesByTeacherName(String teacherName){
        String nameWithPercentsMarks = "%" + teacherName + "%";
        return courseRepository.findAllCoursesByTeacherName(nameWithPercentsMarks);
    }

    public Page<Course> getStudentCourses(Integer userId,int pageNumber){
        Pageable pageable = PageRequest.of(pageNumber - 1,PAGE_SIZE);
        return courseRepository.findStudentCourses(userId, pageable);
    }

    public Page<Course> getTeacherCourses(Integer teacherId, int pageNumber){
        Pageable pageable = PageRequest.of(pageNumber-1,PAGE_SIZE);
        return courseRepository.findTeacherCourses(teacherId, pageable);
    }

    public Course getCourseById(Integer id){
        return courseRepository.findCourseById(id);
    }

    public void addCourse(CourseSaveDTO course, Integer teacherId){
        courseRepository.saveCourse(course, teacherId);
    }
}
