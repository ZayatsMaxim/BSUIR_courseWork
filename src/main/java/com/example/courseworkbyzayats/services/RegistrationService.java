package com.example.courseworkbyzayats.services;

import com.example.courseworkbyzayats.exceptions.AlreadyInUseException;
import com.example.courseworkbyzayats.models.User;
import com.example.courseworkbyzayats.repositories.UserRepository;
import com.example.courseworkbyzayats.services.validators.AlreadyInUseValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RegistrationService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AlreadyInUseValidator alreadyInUseValidator;


    public RegistrationService(UserRepository userRepository,
                               BCryptPasswordEncoder bCryptPasswordEncoder,
                               AlreadyInUseValidator alreadyInUseValidator) {
        this.userRepository = userRepository;
        this.passwordEncoder = bCryptPasswordEncoder;
        this.alreadyInUseValidator = alreadyInUseValidator;
    }

    public void saveUser(User newUser) throws AlreadyInUseException {
        try {
            alreadyInUseValidator.validateUsername(newUser.getUsername());
            alreadyInUseValidator.validateEmail(newUser.getEmail());
            alreadyInUseValidator.validatePhoneNumber(newUser.getPhoneNumber());
        } catch (AlreadyInUseException e){
            throw e;
        }
        String hashPassword = passwordEncoder.encode(newUser.getPassword());
        newUser.setPassword(hashPassword);
        userRepository.saveUser(newUser);
        log.info("Created newUser: {}", newUser.getUsername());
    }

}
