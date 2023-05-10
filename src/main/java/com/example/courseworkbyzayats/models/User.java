package com.example.courseworkbyzayats.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="user")
@AllArgsConstructor
@Getter
@Setter
public class User {

    private static final String DEFAULT_AVATAR = "http://localhost:8080/images/avatars/user.png";
    private static final String DEFAULT_ROLE = "ROLE_STUDENT";

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Заполните логин")
    @Size(min=4, max=30)
    @Pattern(regexp = "^[a-zA-Z][A-Za-z0-9_%@]{4,30}$",
            message = "Неправильный формат логина")
    private String username;

    @NotBlank(message = "Заполните пароль")
    @Size(min=8, max=32)
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d!@%_]{8,32}$",
            message = "Неправильный формат пароля")
    private String password;

    @NotBlank(message = "Заполните имя")
    @Size(min=4, max=32)
    @Pattern(regexp = "^[a-zA-Zа-яА-Я]{4,}(?: [a-zA-Zа-яА-Я]+){1,6}$",
            message = "Неправильный формат имени")
    private String name;

    @NotBlank(message = "Заполните электронную почту")
    @Size(max=50)
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
            message = "Неправильный формат почты")
    private String email;

    @Pattern(regexp = "(^\\+375\\d{9})$",
            message = "Неправильный формат номера телефона")
    private String phoneNumber;

    private String avatar;
    private Boolean enabled;
    private String role;

    public User() {
      this.username = null;
      this.password = null;
      this.name = null;
      this.email = null;
      this.phoneNumber = null;
      this.avatar = DEFAULT_AVATAR;
      this.enabled = true;
      this.role = DEFAULT_ROLE;
    }

    public boolean isEnabled() {
        return enabled;
    }
}