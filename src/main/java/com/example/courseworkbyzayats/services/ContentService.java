package com.example.courseworkbyzayats.services;

import com.example.courseworkbyzayats.exceptions.FileUploadException;
import com.example.courseworkbyzayats.models.ContentInfo;
import com.example.courseworkbyzayats.models.HomeworkInfo;
import com.example.courseworkbyzayats.models.dto.HomeworkForRatingDTO;
import com.example.courseworkbyzayats.repositories.ContentInfoRepository;
import com.example.courseworkbyzayats.repositories.FileRepository;
import com.example.courseworkbyzayats.repositories.HomeworkForRatingRepository;
import com.example.courseworkbyzayats.repositories.HomeworkInfoRepository;
import com.example.courseworkbyzayats.services.validators.FileValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class ContentService {
    public static final Integer MIN_RATING_TO_PASS = 4;
    private static final int PAGE_SIZE = 5;
    private static final String STUDENT_HOMEWORK_REPO_PATH = "D:\\JavaProjects\\courseWorkByZayats\\src\\main\\resources\\homework\\";
    private static final String STUDENT_TEST_REPO_PATH = "D:\\JavaProjects\\courseWorkByZayats\\src\\main\\resources\\homework\\test\\";
    private static final String TEACHER_HOMEWORK_REPO_PATH = "D:\\JavaProjects\\courseWorkByZayats\\src\\main\\resources\\courseContent\\HOMEWORK\\";
    private static final String TEACHER_TEST_REPO_PATH = "D:\\JavaProjects\\courseWorkByZayats\\src\\main\\resources\\courseContent\\TEST\\";
    private static final String TEACHER_THEORY_REPO_PATH = "D:\\JavaProjects\\courseWorkByZayats\\src\\main\\resources\\courseContent\\THEORY\\";
    private static final String COURSE_ICON_REPO_PATH = "D:\\JavaProjects\\courseWorkByZayats\\src\\main\\resources\\static\\images\\icons\\";

    private final ContentInfoRepository contentInfoRepository;
    private final HomeworkInfoRepository homeworkInfoRepository;
    private final FileRepository fileRepository;
    private final FileValidator fileValidator;
    private final HomeworkForRatingRepository homeworkForRatingRepository;


    public ContentService(ContentInfoRepository contentInfoRepository,
                          HomeworkInfoRepository homeworkInfoRepository,
                          FileRepository fileRepository,
                          FileValidator fileValidator,
                          HomeworkForRatingRepository homeworkForRatingRepository) {
        this.contentInfoRepository = contentInfoRepository;
        this.homeworkInfoRepository = homeworkInfoRepository;
        this.fileRepository = fileRepository;
        this.fileValidator = fileValidator;
        this.homeworkForRatingRepository = homeworkForRatingRepository;
    }

    public List<ContentInfo> getCourseTheory(Integer courseId){
        return contentInfoRepository.getCourseContentInfo(courseId, "THEORY");
    }

    public List<ContentInfo> getCourseHomework(Integer courseId){
        return contentInfoRepository.getCourseContentInfo(courseId, "HOMEWORK");
    }

    public List<ContentInfo> getCourseTest(Integer courseId){
        return contentInfoRepository.getCourseContentInfo(courseId, "TEST");
    }

    public File getContentFile(Integer contentId){
        String contentFilePath = contentInfoRepository.getContentFilePath(contentId);
        return new File(contentFilePath);
    }

    public String getContentFilename(Integer contentId){
        return contentInfoRepository.getContentFilenameById(contentId);
    }

    public ContentInfo getContentInfo(Integer contentId){
        return contentInfoRepository.getContentInfoById(contentId);
    }

    public Short getTestToPassAmount(Integer courseId){
        return contentInfoRepository.getCourseTestToPassAmount(courseId);
    }

    public Double getStudentCourseCompletionPercent(Integer studentId, Integer courseId){
        Double completionPercent = ( (double) (contentInfoRepository.getStudentCompletedTestNumber(studentId, courseId, MIN_RATING_TO_PASS))) /
                ( (double) (contentInfoRepository.getCourseTestToPassAmount(courseId)));
        return completionPercent * 100;
    }

    public List<HomeworkInfo> getStudentHomeworkInfo(Integer studentId){
        return homeworkInfoRepository.getStudentHomeworkInfo(studentId);
    }

    public void saveHomework(MultipartFile file,
                             Integer studentId,
                             Integer courseContentId,
                             String type) throws IOException, FileUploadException {
        String filePath = STUDENT_HOMEWORK_REPO_PATH +
                file.getOriginalFilename();

        if (type.equals("TEST")){
            filePath = STUDENT_TEST_REPO_PATH +
                    file.getOriginalFilename();
        }

        fileValidator.validateFile(file,"HOMEWORK");
        fileRepository.save(file, filePath);
        contentInfoRepository.saveHomework(filePath,type,studentId,courseContentId);
        log.info("Saved homework: " + file.getOriginalFilename() + " from student: " + studentId + " type:" + type);
    }

    public void saveContent(MultipartFile file,
                            Integer userId,
                            Integer courseId,
                            String contentType,
                            String contentName,
                            String description) throws IOException, FileUploadException {
        String filePath = TEACHER_HOMEWORK_REPO_PATH +
                file.getOriginalFilename();

        if(contentType.equals("TEST")){
            filePath = TEACHER_TEST_REPO_PATH +
                    file.getOriginalFilename();
        }

        if(contentType.equals("THEORY")){
            filePath = TEACHER_THEORY_REPO_PATH +
                    file.getOriginalFilename();
        }
        fileValidator.validateFile(file, "CONTENT");
        fileRepository.save(file,filePath);
        contentInfoRepository.saveTeacherContent(courseId,filePath,contentType,contentName,description);
        log.info("Saved content: " + file.getOriginalFilename() + " from teacher: " + userId + " type:" +contentType);
    }

    public Page<HomeworkForRatingDTO> getTeacherHomeworkForRating(Integer teacherId, int pageNumber){
        Pageable pageable = PageRequest.of(pageNumber-1,PAGE_SIZE);
        return homeworkForRatingRepository.getTeacherHomeworkInfoForRating(teacherId,pageable);
    }

    public String getTeacherHomeworkFilename(Integer homeworkId){
        return homeworkForRatingRepository.getHomeworkFilename(homeworkId);
    }

    public String getHomeworkFilepath(Integer homeworkId){
        return homeworkForRatingRepository.getHomeworkPath(homeworkId);
    }

    public void saveRating(Integer homeworkId, Integer rate){
        homeworkForRatingRepository.saveHomeworkRating(homeworkId, rate);
    }

    public String saveCourseIcon(MultipartFile file) throws IOException, FileUploadException {
        fileRepository.save(file, COURSE_ICON_REPO_PATH + file.getOriginalFilename());
        return file.getOriginalFilename();
    }

    public void deleteHomeworkById(Integer studentId,Integer homeworkId){
        if (Objects.equals(studentId, homeworkInfoRepository.getHomeworkStudentId(homeworkId))){
            homeworkInfoRepository.deleteHomeworkInfoById(homeworkId);
        }
    }

    public void deleteContentInfo(Integer teacherId, Integer contentId){
        Integer courseOfContentTeacher = contentInfoRepository.getContentTeacherId(contentId);
        if (Objects.equals(teacherId, courseOfContentTeacher)) {
            contentInfoRepository.deleteContentInfoById(contentId);
        }
    }
}
