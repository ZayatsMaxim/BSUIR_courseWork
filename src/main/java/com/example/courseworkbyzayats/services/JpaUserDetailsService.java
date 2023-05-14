package com.example.courseworkbyzayats.services;


import com.example.courseworkbyzayats.exceptions.AlreadyInUseException;
import com.example.courseworkbyzayats.exceptions.FileUploadException;
import com.example.courseworkbyzayats.models.User;
import com.example.courseworkbyzayats.models.UserSecurityDetails;
import com.example.courseworkbyzayats.models.dto.UserUpdateDTO;
import com.example.courseworkbyzayats.repositories.FileRepository;
import com.example.courseworkbyzayats.repositories.UserRepository;
import com.example.courseworkbyzayats.services.validators.AlreadyInUseValidator;
import com.example.courseworkbyzayats.services.validators.FileValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


@Service
@Slf4j
public class JpaUserDetailsService implements UserDetailsService {

    private static final int PAGE_SIZE = 8;
    private final UserRepository userRepository;
    private final AlreadyInUseValidator alreadyInUseValidator;
    private final FileValidator fileValidator;
    private final FileRepository fileRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    private static final String AVATAR_REPO_PATH = "D:\\JavaProjects\\courseWorkByZayats\\src\\main\\resources\\static\\images\\avatars\\";
    private final String AVATAR_REPO_URL = "http://localhost:8080/images/avatars/";

    @Autowired
    public JpaUserDetailsService(UserRepository userRepository,
                                 AlreadyInUseValidator alreadyInUseValidator,
                                 FileValidator fileValidator,
                                 FileRepository fileRepository,
                                 @Lazy BCryptPasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.alreadyInUseValidator = alreadyInUseValidator;
        this.fileValidator = fileValidator;
        this.fileRepository = fileRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.getUserByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("Could not find user: " + username);
        }

        return new UserSecurityDetails(user);
    }

    public boolean identifyUserByCredentials(Integer userId, String password) {
        User user = userRepository.getUserById(userId);
        return (passwordEncoder.matches(password, user.getPassword()));
    }

    public boolean identifyTeacherForCourse(Integer teacherId, Integer courseId){
        return (teacherId.equals(userRepository.getCourseTeacherId(courseId)));
    }

    public boolean identityUserForContentPreview(Integer userId, Integer courseId){
        User user = userRepository.getUserById(userId);
        switch (user.getRole()) {
            case "ROLE_STUDENT" -> {
               return userRepository.getCourseIdIfUserIsItsStudent(userId, courseId).isPresent();
            }
            case "ROLE_TEACHER" -> {
                return (identifyTeacherForCourse(userId, courseId));
            }
            case "ROLE_ADMIN" -> {
                return true;
            }
        }
        return false;
    }

    public UserSecurityDetails getCurrentLoggedUserDetails(){
        return (UserSecurityDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public Page<User> getAllStudentsPage(int pageNumber){
        Pageable pageable = PageRequest.of(pageNumber-1,PAGE_SIZE);
        return userRepository.getAllUsersByRole("ROLE_STUDENT", pageable);
    }

    public Page<User> getAllTeachersPage(int pageNumber){
        Pageable pageable = PageRequest.of(pageNumber-1,PAGE_SIZE);
        return userRepository.getAllUsersByRole("ROLE_TEACHER", pageable);
    }

    public List<User> findUsersByName(String name, String role){
        return userRepository.findUsersByName(name, role);
    }

    public List<User> findUsersByUsername(String username, String role){
        return userRepository.findUsersByUserName(username, role);
    }

    public void updateUserInfo(UserUpdateDTO updateInfo) throws AlreadyInUseException {
        User userToUpdate = getCurrentLoggedUserDetails().getUser();
        Integer userToUpdateId = userToUpdate.getId();
        try {
            alreadyInUseValidator.validateUsername(updateInfo.getUsername());
            alreadyInUseValidator.validateEmail(updateInfo.getEmail());
            if(!updateInfo.getPhoneNumber().isEmpty() || !updateInfo.getPhoneNumber().isBlank()){
                alreadyInUseValidator.validatePhoneNumberForUpdate(updateInfo.getPhoneNumber(), userToUpdateId);
            }
        } catch (AlreadyInUseException e){
            throw e;
        }
        //User userToUpdate = getCurrentLoggedUserDetails().getUser();

        updateInfo.setId(userToUpdate.getId());

        if(updateInfo.getUsername().isEmpty() || updateInfo.getUsername().isBlank()){
            updateInfo.setUsername(userToUpdate.getUsername());
        }

        if(updateInfo.getName().isEmpty() || updateInfo.getName().isBlank()){
            updateInfo.setName(userToUpdate.getName());
        }

        if(updateInfo.getEmail().isEmpty() || updateInfo.getEmail().isBlank()){
            updateInfo.setEmail(userToUpdate.getEmail());
        }

        if(updateInfo.getPhoneNumber().isEmpty() || updateInfo.getPhoneNumber().isBlank()){
            updateInfo.setPhoneNumber(userToUpdate.getPhoneNumber());
        }

        if(updateInfo.getPassword().isEmpty() || updateInfo.getPassword().isBlank()){
            updateInfo.setPassword(userToUpdate.getPassword());
        }
        else{
            String hashPassword = passwordEncoder.encode(updateInfo.getPassword());
            updateInfo.setPassword(hashPassword);
        }

        userRepository.updateUser(updateInfo);
    }

    public void updateAvatar(Integer userId, MultipartFile avatar) throws IOException, FileUploadException {

        try {
            fileValidator.validateFile(avatar, "AVATAR");
            fileRepository.save(avatar, AVATAR_REPO_PATH+avatar.getOriginalFilename());
            userRepository.updateUserAvatar(userId,AVATAR_REPO_URL+avatar.getOriginalFilename());
        } catch (IOException | FileUploadException e){
            throw e;
        }
    }

    public List<User> getGroupStudentsList(Integer groupId){
        return userRepository.findGroupStudents(groupId);
    }

}
