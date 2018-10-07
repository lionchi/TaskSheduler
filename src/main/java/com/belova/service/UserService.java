package com.belova.service;

import com.belova.entity.User;


public interface UserService {

    User findUserByLogin (String login);
}
