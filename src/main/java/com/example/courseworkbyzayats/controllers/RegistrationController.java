package com.example.courseworkbyzayats.controllers;

import com.example.courseworkbyzayats.exceptions.AlreadyInUseException;
import com.example.courseworkbyzayats.models.User;
import com.example.courseworkbyzayats.models.UserSecurityDetails;
import com.example.courseworkbyzayats.services.RegistrationService;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/zayct/courses/signup")
public class RegistrationController {
    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @GetMapping("")
    public String openRegisterForm(Model model){
        User newUser = new User();
        model.addAttribute("newUser", newUser);
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("newUser") User newUser,
                               BindingResult result) {
        if (result.hasErrors()){
            return "register";
        }
        try{
            registrationService.saveUser(newUser);
        } catch (AlreadyInUseException e) {
            ObjectError ValidationError = new ObjectError("globalError", e.getMessage());
            result.addError(ValidationError);
            return "register";
        }
        return "registerSuccess";
    }
}
