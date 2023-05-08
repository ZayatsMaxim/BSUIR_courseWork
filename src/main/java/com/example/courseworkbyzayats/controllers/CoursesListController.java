package com.example.courseworkbyzayats.controllers;

import com.example.courseworkbyzayats.exceptions.AlreadyRegisteredException;
import com.example.courseworkbyzayats.models.ContentInfo;
import com.example.courseworkbyzayats.models.Course;
import com.example.courseworkbyzayats.models.dto.CourseSaveDTO;
import com.example.courseworkbyzayats.services.ContentService;
import com.example.courseworkbyzayats.services.CourseService;
import com.example.courseworkbyzayats.services.GroupService;
import com.example.courseworkbyzayats.services.JpaUserDetailsService;
import com.ibm.icu.text.Transliterator;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.FilenameUtils;
import org.springframework.data.domain.Page;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/zayct/courses/list")
public class CoursesListController {
    private final SimpleGrantedAuthority TEACHER = new SimpleGrantedAuthority("ROLE_TEACHER");
    public final SimpleGrantedAuthority STUDENT = new SimpleGrantedAuthority("ROLE_STUDENT");
    public final SimpleGrantedAuthority ADMIN = new SimpleGrantedAuthority("ROLE_ADMIN");

    private final int DEFAULT_PAGE_NUMBER = 1;
    private final String CYRILLIC_TO_LATIN = "Russian-Latin/BGN";

    private final String DEFAULT_ICON = "http://localhost:8080/images/icons/defaultIcon.png";
    private final String ICONS_REPO_URL = "http://localhost:8080/images/icons/";

    private final CourseService courseService;
    private final JpaUserDetailsService userDetailsService;
    private final GroupService groupService;
    private final ContentService contentService;


    public CoursesListController(CourseService courseService,
                                 JpaUserDetailsService userDetailsService,
                                 GroupService groupService,
                                 ContentService contentService) {
        this.courseService = courseService;
        this.userDetailsService = userDetailsService;
        this.groupService = groupService;
        this.contentService = contentService;
    }


    @GetMapping(value = "/page/{pageNumber}")
    public String getPage(@PathVariable("pageNumber") Optional<Integer> pageNumber, Model model) {
        int requestedPageNumber = DEFAULT_PAGE_NUMBER;
        if (pageNumber.isPresent()) {
            requestedPageNumber = pageNumber.get();
        }

        boolean isAdmin = userDetailsService.getCurrentLoggedUserDetails().getAuthorities()
                .contains(ADMIN);

        Page<Course> page = courseService.getAllCourses(requestedPageNumber);
        List<Course> courseList = page.getContent();

        model.addAttribute("currentPage", requestedPageNumber);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("courseList", courseList);
        model.addAttribute("isAdmin", isAdmin);
        return  "coursesList";
    }

    @GetMapping("/find/byTitle")
    public String getCourseByTitle(@RequestParam String title,
                                   Model model){
        List<Course> foundCourses = courseService.getCoursesByTitle(title);
        model.addAttribute("courseList", foundCourses);
        return "findCourses";
    }

    @GetMapping("/find/byTeacherName")
    public String getCourseByTeacherName(@RequestParam String teacherName,
                                   Model model){
        List<Course> foundCourses = courseService.getCoursesByTeacherName(teacherName);
        model.addAttribute("courseList", foundCourses);
        return "findCourses";
    }

    @GetMapping(value = "/info/{courseId}")
    public String getCourseInfo(@PathVariable("courseId") Integer courseId, Model model) {
        addCourseInfoIntoModel(courseId,model);
        return "courseInfo";
    }

    @PostMapping(value = "/info/{courseId}")
    public String registerUserForGroup(@PathVariable("courseId") Integer courseId,
                                       @RequestParam(name = "currentUserId") Integer currentUserId,
                                       @RequestParam(name = "groupName") String groupName,
                                       Model model) {
        try {
            groupService.registerStudentForGroup(currentUserId,groupName);
        } catch (AlreadyRegisteredException e) {
            model.addAttribute("errorMessage",e.getMessage());
            addCourseInfoIntoModel(courseId, model);
            return "courseInfo";
        }
        return "redirect:/zayct/courses/user/myCourses/page/1";
    }

    @GetMapping(value = "/content/{courseId}")
    public String getCourseContent(@PathVariable Integer courseId, Model model) {
        Integer currentUserId = userDetailsService.getCurrentLoggedUserDetails().getUser().getId();
        List<ContentInfo> courseTheory = contentService.getCourseTheory(courseId);
        List<ContentInfo> courseHomework = contentService.getCourseHomework(courseId);
        List<ContentInfo> courseTest = contentService.getCourseTest(courseId);
        Short testToPassAmount = contentService.getTestToPassAmount(courseId);
        Double courseCompletion = contentService.getStudentCourseCompletionPercent(currentUserId, courseId);
        ContentInfo contentInfo = new ContentInfo();


        model.addAttribute("courseTheory", courseTheory);
        model.addAttribute("courseHomework", courseHomework);
        model.addAttribute("courseTest", courseTest);
        model.addAttribute("testToPassAmount", testToPassAmount);
        model.addAttribute("completion", courseCompletion);
        model.addAttribute("contentInfo",contentInfo);
        model.addAttribute("courseId", courseId);

        return "courseContent";
    }

    @GetMapping(value = "/content/download/{contentId}")
    @ResponseBody
    public void downloadContent(@PathVariable Integer contentId,
                                HttpServletResponse response) throws IOException {
        ContentInfo requestedContent = contentService.getContentInfo(contentId);
        String filename = new String(requestedContent.getFileName().getBytes(StandardCharsets.UTF_8));
        Path filePath = Path.of(requestedContent.getContentPath());
        String extension = FilenameUtils.getExtension(filePath.toString());
        Transliterator toLatinTrans = Transliterator.getInstance(CYRILLIC_TO_LATIN);

        if (Files.exists(filePath)) {
            response.setContentType(extension);
            response.setCharacterEncoding("UTF-8");
            response.addHeader("Content-Disposition", "attachment; filename=" +
                    toLatinTrans.transliterate(filename) +
                    "." + extension);
            try {
                Files.copy(filePath, response.getOutputStream());
                response.getOutputStream().flush();
            } catch (IOException e) {
                throw new IOException(e);
            }
        }
    }

    @PostMapping("/content/upload/homework")
    public String uploadHomework(@RequestParam("file") MultipartFile file,
                                 @RequestParam("courseContentId") Integer courseContentId,
                                 @RequestParam("type") String type,
                                 Model model) {
        Integer currentUserId = userDetailsService.getCurrentLoggedUserDetails().getUser().getId();
        try {
            contentService.saveHomework(file,currentUserId,courseContentId,type);
        } catch (IOException e){
            model.addAttribute("errorMessage",e.getMessage());
            return "uploadFailed";
        }
        return "redirect:/zayct/courses/user/myCourses/page/1";
    }

    @PostMapping("/content/upload")
    public String uploadContent(@RequestParam("file") MultipartFile file,
                                @RequestParam("courseId") Integer courseId,
                                @RequestParam("contentType") String contentType,
                                @RequestParam("contentName") String contentName,
                                @RequestParam("description") String description,
                                Model model) throws IOException {
        Integer currentUserId = userDetailsService.getCurrentLoggedUserDetails().getUser().getId();
        try {
            contentService.saveContent(file,currentUserId,courseId,contentType,contentName,description);
        } catch (IOException e){
            model.addAttribute("errorMessage",e.getMessage());
            return "uploadFailed";
        }
        return "redirect:/zayct/courses/user/myCourses/page/1";
    }

    @GetMapping ("/addCourse")
    public String showAddCoursePage(Model model){
        //Course course = new Course();
        CourseSaveDTO course = new CourseSaveDTO();
        course.setTeacherId("");
        course.setIcon(DEFAULT_ICON);
        model.addAttribute("newCourse",course);
        model.addAttribute("teacherId",null);
        return "adminAddCourse";
    }

    @PostMapping("/addCourse")
    public String addCourse(@RequestParam(value = "iconFile", required = false) MultipartFile file,
                            @ModelAttribute("newCourse") CourseSaveDTO newCourse,
                            @RequestParam("teacherId") Integer teacherId) throws IOException {
        if (!file.isEmpty()){
           String savedIconName = contentService.saveCourseIcon(file);
            newCourse.setIcon(ICONS_REPO_URL + savedIconName);
        }

        courseService.addCourse(newCourse, teacherId);

        return "redirect:/zayct/courses/list/page/1";
    }



    private void addCourseInfoIntoModel(Integer courseId, Model model) {
        Course neededCourse = courseService.getCourseById(courseId);
        List<String> groupNames = groupService.getCourseGroupName(courseId);
        Integer currentUserId = userDetailsService.getCurrentLoggedUserDetails().getUser().getId();
        boolean isStudent = userDetailsService.getCurrentLoggedUserDetails().getAuthorities()
                            .contains(STUDENT);

        model.addAttribute("groups",groupNames);
        model.addAttribute("course", neededCourse);
        model.addAttribute("currentUserId",currentUserId);
        model.addAttribute("isStudent", isStudent);
    }
}
