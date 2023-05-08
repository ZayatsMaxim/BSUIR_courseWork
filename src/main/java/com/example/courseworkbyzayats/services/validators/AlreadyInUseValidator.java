package com.example.courseworkbyzayats.services.validators;

import com.example.courseworkbyzayats.exceptions.AlreadyInUseException;
import com.example.courseworkbyzayats.repositories.UserRepository;
import org.springframework.stereotype.Component;

import java.io.InvalidObjectException;
import java.util.Objects;

@Component
public class AlreadyInUseValidator {

    private final UserRepository userRepository;

    public AlreadyInUseValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void validateUsername(String username) throws AlreadyInUseException {
        if (userRepository.getUsername(username) != null){
            throw new AlreadyInUseException("Аккаунт с таким логином уже существует!");
        }
    }

    public void validateEmail(String email) throws AlreadyInUseException{
        if (userRepository.getEmail(email) != null){
            throw new AlreadyInUseException("Адрес электронной почты уже используется!");
        }
    }

    public void validatePhoneNumber(String phoneNumber) throws AlreadyInUseException {
        if (userRepository.getPhoneNumber(phoneNumber) != null) {
            throw new AlreadyInUseException("Номер телефона уже используется!");
        }
    }

    public void validatePhoneNumberForUpdate(String phoneNumber, Integer userId) throws AlreadyInUseException {

        if (userRepository.getPhoneNumber(phoneNumber) != null) {
            if (!Objects.equals(userRepository.getPhoneNumber(phoneNumber), userRepository.getPhoneNumberByUserId(userId))){
                throw new AlreadyInUseException("Номер телефона уже используется!");
            }
        }
    }
}
