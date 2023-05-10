package com.example.courseworkbyzayats.controllers;


import com.example.courseworkbyzayats.exceptions.AlreadyInUseException;
import com.example.courseworkbyzayats.models.*;
import com.example.courseworkbyzayats.models.dto.HomeworkForRatingDTO;
import com.example.courseworkbyzayats.models.dto.UserUpdateDTO;
import com.example.courseworkbyzayats.services.*;
import com.ibm.icu.text.Transliterator;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.apache.commons.io.FilenameUtils;
import org.springframework.data.domain.Page;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/zayct/courses/user")
public class ProfileController {
    public final static SimpleGrantedAuthority STUDENT = new SimpleGrantedAuthority("ROLE_STUDENT");
    public final static  SimpleGrantedAuthority TEACHER = new SimpleGrantedAuthority("ROLE_TEACHER");
    public final static  SimpleGrantedAuthority ADMIN = new SimpleGrantedAuthority("ROLE_ADMIN");
    private final String CYRILLIC_TO_LATIN = "Russian-Latin/BGN";


    private final JpaUserDetailsService userDetailsService;
    private final CourseService courseService;
    private final ContentService contentService;
    private final RegistrationService registrationService;
    private final GroupService groupService;

    private final static int DEFAULT_PAGE_NUMBER = 1;

    public ProfileController(JpaUserDetailsService userDetailsService,
                             CourseService courseService,
                             ContentService contentService,
                             RegistrationService registrationService,
                             GroupService groupService) {
        this.userDetailsService = userDetailsService;
        this.courseService = courseService;
        this.contentService = contentService;
        this.registrationService = registrationService;
        this.groupService = groupService;
    }

    @GetMapping("")
    public String userProfile(Model model) {
        UserSecurityDetails currentUserDetails = userDetailsService.getCurrentLoggedUserDetails();

        User currentUser = currentUserDetails.getUser();
        model.addAttribute("user", currentUser);
        return "accountProfile";
    }

    @GetMapping("/myCourses/page/{pageNumber}")
    public String userCourses(@PathVariable("pageNumber") Optional<Integer> pageNumber, Model model) {
        UserSecurityDetails currentUserDetails = userDetailsService.getCurrentLoggedUserDetails();

        User currentUser = currentUserDetails.getUser();
        Integer currentUserId = currentUser.getId();

        int requestedPageNumber = DEFAULT_PAGE_NUMBER;
        if (pageNumber.isPresent()){
            requestedPageNumber = pageNumber.get();
        }

        if (currentUserDetails.getAuthorities().contains(STUDENT)) {
            Page<Course> page = courseService.getStudentCourses(currentUserId,requestedPageNumber);
            List<Course> courseList = page.getContent();
            model.addAttribute("currentPage", requestedPageNumber);
            model.addAttribute("totalPages", page.getTotalPages());
            model.addAttribute("totalItems", page.getTotalElements());
            model.addAttribute("courseList", courseList);
        }

        if(currentUserDetails.getAuthorities().contains(TEACHER)) {
            Page<Course> page = courseService.getTeacherCourses(currentUserId,requestedPageNumber);
            List<Course> courseList = page.getContent();
            model.addAttribute("currentPage", requestedPageNumber);
            model.addAttribute("totalPages", page.getTotalPages());
            model.addAttribute("totalItems", page.getTotalElements());
            model.addAttribute("courseList", courseList);
        }

        return  "studentCourses";
    }

    @GetMapping("/myHomework")
    public String studentHomework(Model model) {
        Integer currentUserId = userDetailsService.getCurrentLoggedUserDetails().getUser().getId();
        List<HomeworkInfo> studentHomework = contentService.getStudentHomeworkInfo(currentUserId);

        model.addAttribute("homeworkList", studentHomework);
        model.addAttribute("minRating", ContentService.MIN_RATING_TO_PASS);
        return "studentHomework";
    }

    @GetMapping("/teacherHomework/page/{pageNumber}")
    public String teacherHomeworkForRating(@PathVariable Optional<Integer> pageNumber,
                                           Model model) {
        UserSecurityDetails currentUserDetails = userDetailsService.getCurrentLoggedUserDetails();
        if (currentUserDetails.getAuthorities().contains(TEACHER)) {
            Integer currentUserId = currentUserDetails.getUser().getId();
            int requestedPageNumber = DEFAULT_PAGE_NUMBER;
            if (pageNumber.isPresent()) {
                requestedPageNumber = pageNumber.get();
            }

            Page<HomeworkForRatingDTO> page = contentService.getTeacherHomeworkForRating(currentUserId, requestedPageNumber);
            List<HomeworkForRatingDTO> homeworksList = page.getContent();


            model.addAttribute("currentPage", requestedPageNumber);
            model.addAttribute("totalPages", page.getTotalPages());
            model.addAttribute("totalItems", page.getTotalElements());
            model.addAttribute("homeworksList", homeworksList);

            return "teacherHomeworkRating";
        } else return "redirect:/zayct/courses/user/myHomework";
    }

    @PostMapping("/teacherHomework/rate")
    public String rateHomework(@RequestParam("homeworkId") Integer homeworkId,
                               @RequestParam("rate") Integer rate) {
        UserSecurityDetails currentUserDetails = userDetailsService.getCurrentLoggedUserDetails();
        if (currentUserDetails.getAuthorities().contains(TEACHER)) {
            contentService.saveRating(homeworkId, rate);
            return "redirect:/zayct/courses/user/teacherHomework/page/1";
        } else return "redirect:/zayct/courses/user/myHomework";
    }

    @GetMapping("/update")
    public String showUpdateForm(Model model) {
        UserUpdateDTO updateInfo = new UserUpdateDTO();
        model.addAttribute("userToUpdate", updateInfo);
        return "updateProfile";
    }

    @PostMapping("/update")
    public String updateInfo(@Valid @ModelAttribute("userToUpdate") UserUpdateDTO updateInfo,
                             BindingResult result){
        if (result.hasErrors()){
            return "updateProfile";
        }

        try{
            userDetailsService.updateUserInfo(updateInfo);
        } catch (AlreadyInUseException e) {
            ObjectError ValidationError = new ObjectError("globalError", e.getMessage());
            result.addError(ValidationError);
            return "updateProfile";
        }
        return "redirect:/zayct/courses/user";
    }

    @PostMapping("/updateAvatar")
    public String updateUserAvatar(@RequestParam("avatar") MultipartFile avatar) throws IOException {
        Integer userId = userDetailsService.getCurrentLoggedUserDetails().getUser().getId();
        userDetailsService.updateAvatar(userId,avatar);
        return "redirect:/zayct/courses/user";
    }

    @GetMapping("/admin/teacherRegister")
    public String showRegisterTeacherPage(Model model){
        UserSecurityDetails currentUserDetails = userDetailsService.getCurrentLoggedUserDetails();
        if (currentUserDetails.getAuthorities().contains(ADMIN)){
            User newUser = new User();

            model.addAttribute("newUser", newUser);
            return "adminTeacherRegister";
        }
        else return "redirect:/zayct/courses/user";
    }

    @PostMapping("/admin/teacherRegister")
    public String registerTeacher(@Valid @ModelAttribute("newUser") User newUser,
                                  BindingResult result) {
        if (result.hasErrors()) {
            return "adminTeacherRegister";
        }
        try{
            newUser.setRole("ROLE_TEACHER");
            registrationService.saveUser(newUser);
        } catch (AlreadyInUseException e) {
            ObjectError ValidationError = new ObjectError("globalError", e.getMessage());
            result.addError(ValidationError);
            return "adminTeacherRegister";
        }
        return "redirect:/zayct/courses/user";
    }

    @GetMapping("/admin/studentsList/page/{pageNumber}")
    public String showStudentList(@PathVariable("pageNumber") Integer pageNumber,
                                  Model model) {
        UserSecurityDetails currentUserDetails = userDetailsService.getCurrentLoggedUserDetails();
        if (currentUserDetails.getAuthorities().contains(ADMIN)) {
            Page<User> studentsPage = userDetailsService.getAllStudentsPage(pageNumber);
            List<User> studentsList = studentsPage.getContent();

            model.addAttribute("currentPage", pageNumber);
            model.addAttribute("totalPages", studentsPage.getTotalPages());
            model.addAttribute("totalItems", studentsPage.getTotalElements());
            model.addAttribute("studentsList", studentsList);

            return "adminStudentsList";
        }
        return "redirect:/zayct/courses/user";
    }

    @GetMapping("/admin/teachersList/page/{pageNumber}")
    public String showTeacherList(@PathVariable("pageNumber") Integer pageNumber,
                                  Model model) {
        UserSecurityDetails currentUserDetails = userDetailsService.getCurrentLoggedUserDetails();
        if (currentUserDetails.getAuthorities().contains(ADMIN)) {
            Page<User> teachersPage = userDetailsService.getAllTeachersPage(pageNumber);
            List<User> teachersList = teachersPage.getContent();

            model.addAttribute("currentPage", pageNumber);
            model.addAttribute("totalPages", teachersPage.getTotalPages());
            model.addAttribute("totalItems", teachersPage.getTotalElements());
            model.addAttribute("teachersList", teachersList);

            return "adminTeachersList";
        }
        return "redirect:/zayct/courses/user";
    }

    @GetMapping("/admin/findUser/byName")
    public String findUserByName(@RequestParam("name") String name,
                                 @RequestParam("role") String role,
                                 Model model) {
        UserSecurityDetails currentUserDetails = userDetailsService.getCurrentLoggedUserDetails();
        if (currentUserDetails.getAuthorities().contains(ADMIN)) {
            List<User> foundUsers = userDetailsService.findUsersByName(name, role);

            model.addAttribute("usersList", foundUsers);
            model.addAttribute("role", role);
            return "adminFindUsers";
        }
        return "redirect:/zayct/courses/user";
    }

    @GetMapping("/admin/findUser/byUsername")
    public String findUserByUsername(@RequestParam("username") String username,
                                     @RequestParam("role") String role,
                                     Model model) {
        UserSecurityDetails currentUserDetails = userDetailsService.getCurrentLoggedUserDetails();
        if (currentUserDetails.getAuthorities().contains(ADMIN)) {
            List<User> foundUsers = userDetailsService.findUsersByUsername(username, role);

            model.addAttribute("usersList", foundUsers);
            model.addAttribute("role", role);

            return "adminFindUsers";
        }
        return "redirect:/zayct/courses/user";
    }

    @GetMapping("/admin/groupsList/page/{pageNumber}")
    public String getGroupsList(@PathVariable Integer pageNumber, Model model){
        UserSecurityDetails currentUserDetails = userDetailsService.getCurrentLoggedUserDetails();
        if (currentUserDetails.getAuthorities().contains(ADMIN)) {
            Page<Group> groupsPage = groupService.getAllGroupsPage(pageNumber);
            List<Group> groupsList = groupsPage.getContent();

            model.addAttribute("currentPage", pageNumber);
            model.addAttribute("totalPages", groupsPage.getTotalPages());
            model.addAttribute("totalItems", groupsPage.getTotalElements());
            model.addAttribute("groupsList", groupsList);
            model.addAttribute("courseService", courseService);

            return "adminGroupsList";
        }
        else return "redirect:/zayct/courses/user";
    }

    @GetMapping("/admin/createGroup")
    public String showCreateGroupPage(Model model){
        UserSecurityDetails currentUserDetails = userDetailsService.getCurrentLoggedUserDetails();
        if (currentUserDetails.getAuthorities().contains(ADMIN)) {
            Group newGroup = new Group();
            model.addAttribute("newGroup", newGroup);
            return "adminGroupCreation";
        }
        else return "redirect:/zayct/courses/user";
    }

    @PostMapping ("/admin/createGroup")
    public String createNewGroup(@ModelAttribute("newGroup") Group newGroup){
        UserSecurityDetails currentUserDetails = userDetailsService.getCurrentLoggedUserDetails();
        if (currentUserDetails.getAuthorities().contains(ADMIN)) {
            groupService.saveGroup(newGroup);
            return "redirect:/zayct/courses/user/admin/groupsList/page/1";
        }
        else return "redirect:/zayct/courses/user";
    }

    @GetMapping("/admin/groupInfo/{groupId}")
    public String getGroupInfo(@PathVariable Integer groupId, Model model){
        UserSecurityDetails currentUserDetails = userDetailsService.getCurrentLoggedUserDetails();
        if (currentUserDetails.getAuthorities().contains(ADMIN)) {
            Group requestedGroup = groupService.getGroupById(groupId);
            if (requestedGroup == null){
                return "redirect:/zayct/courses/user";
            }
            List<User> groupStudents = userDetailsService.getGroupStudentsList(groupId);
            String courseName = courseService.getCourseName(requestedGroup.getCourseId());

            model.addAttribute("groupStudents", groupStudents);
            model.addAttribute("group", requestedGroup);
            model.addAttribute("courseName", courseName);
            return "adminGroupInfo";

        }
        else return "redirect:/zayct/courses/user";
    }

    @GetMapping("/teacherHomework/download/{homeworkId}")
    @ResponseBody
    public void teacherDownloadContent(@PathVariable Integer homeworkId,
                                HttpServletResponse response) throws IOException {
        //HomeworkForRatingDTO requestedHomework = contentService.getTeacherHomeworkById(homeworkId);
        String filename = contentService.getTeacherHomeworkFilename(homeworkId);
        //String filename = new String(requestedHomework.getContentName().getBytes(StandardCharsets.UTF_8));
        Path filePath = Path.of(contentService.getHomeworkFilepath(homeworkId));
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
}
