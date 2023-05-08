package com.example.courseworkbyzayats.repositories;

import com.example.courseworkbyzayats.models.User;
import com.example.courseworkbyzayats.models.dto.UserUpdateDTO;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    @Query(
            value = "SELECT u.username FROM user u WHERE u.username = :username ", nativeQuery = true)
    String getUsername(@Param("username") String username);

    @Query(
           value = "SELECT * FROM user u WHERE u.username = :username ", nativeQuery = true)
    User getUserByUsername(@Param("username") String username);

    @Modifying
    @Transactional
    @Query(
            value = "INSERT INTO user(role, username, password, name, email, phone_number, avatar, enabled) VALUES " +
                    "(:#{#newUser.role}, :#{#newUser.username}, :#{#newUser.password}, :#{#newUser.name}, " +
                    ":#{#newUser.email}, :#{#newUser.phoneNumber}, :#{#newUser.avatar}, :#{#newUser.enabled})",
            nativeQuery = true)
    void saveUser(@Param("newUser") User newUser);

    @Query(
            value = "SELECT u.email FROM user u WHERE u.email = :email ", nativeQuery = true
    )
    String getEmail(@Param("email") String email);

    @Query(
            value = "SELECT u.phone_number FROM user u WHERE u.phone_number = :phoneNumber ", nativeQuery = true
    )
    String getPhoneNumber(@Param("phoneNumber") String phoneNumber);

    @Query(
            value = "SELECT u.phone_number FROM user u WHERE u.id=:userId",
            nativeQuery = true
    )
    String getPhoneNumberByUserId(@Param("userId") Integer userId);

    @Transactional
    @Modifying
    @Query(
            value = "UPDATE `user` "+
                    "SET email = :#{#user.email}, " +
                    "password = :#{#user.password}, " +
                    "username = :#{#user.username}, " +
                    "`name` = :#{#user.name}, " +
                    "phone_number = :#{#user.phoneNumber} " +
                    "WHERE id = :#{#user.id}",
            nativeQuery = true
    )
    void updateUser(@Param("user") UserUpdateDTO user);

    @Query(
            value = "SELECT * FROM `user` " +
                    "WHERE `role` = :role AND enabled=1",
            countQuery = "SELECT count(*) FROM `user`\n" +
                    "WHERE `role` = :role AND enabled=1",
            nativeQuery = true
    )
    Page<User> getAllUsersByRole(@Param("role") String role, Pageable pageable);

    @Query(
            value = "SELECT * FROM `user`\n" +
                    "WHERE `role` = :role AND `name` = :name",
            nativeQuery = true
    )
    List<User> findUsersByName(@Param("name") String name, @Param("role") String role);

    @Query(
            value = "SELECT * FROM `user`\n" +
                    "WHERE `role` = :role AND username = :username",
            nativeQuery = true
    )
    List<User> findUsersByUserName(@Param("username") String username,@Param("role") String role);

    @Transactional
    @Modifying
    @Query(
            value = "UPDATE `user` SET avatar = :avatar WHERE id = :userId",
            nativeQuery = true
    )
    void updateUserAvatar(@Param("userId") Integer userId,@Param("avatar") String avatar);
}
