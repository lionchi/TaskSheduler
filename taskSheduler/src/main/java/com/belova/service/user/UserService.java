package com.belova.service.user;

import com.belova.entity.User;
import com.belova.entity.UserRole;

import java.util.List;


public interface UserService {

    User findUserByLogin (String login);

    User findUserByFio (String fio);

    List<User> getAllUsers ();

    List<User> getAllDepartmentUsers (Long id);

    String addUser (String fio, String login, String post, String department, UserRole role);

    void editUser (User editUser, String fio, String login, String post, String department, Integer enabled, UserRole role);

    void deleteUser (String fio);

    void changePassword(String oldPass, String newPass);

    String getDepartmentCurrentUser (Long userId);
}
