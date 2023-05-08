package com.example.courseworkbyzayats.models.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@NoArgsConstructor
public class UserUpdateDTO {
    private Integer id;

    @Pattern(regexp = "^(?:[a-zA-Z][a-zA-Z0-9]*)?$",
            message = "Неправильный формат логина")
    private String username;

    @Pattern(regexp = "^(?:[a-zA-Z][a-zA-Z0-9!_@#&$?]{8,32})?$",
            message = "Неправильный формат пароля")
    private String password;

    @Pattern(regexp = "(?:^[a-zA-Zа-яА-Я]{1,}(?: [a-zA-Zа-яА-Я]+){1,6}$)?$",
            message = "Неправильный формат имени")
    private String name;

    @Pattern(regexp = "(?:^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,3})?$",
            message = "Неправильный формат почты")
    private String email;

    @Pattern(regexp = "(?:^\\+375\\d{9})?$",
            message = "Неправильный формат номера телефона")
    private String phoneNumber;
}
