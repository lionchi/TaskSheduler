package com.belova.service;

import com.belova.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

@Component
public class UserServiceImpl implements UserService {

    @Autowired
    private EntityManager entityManager;

    @Transactional
    @Override
    public User findUserByLogin(String login) {
        User user = entityManager.createQuery("select u from User as u where u.login = :login", User.class)
                .setParameter("login", login)
                .getSingleResult();
        return  user;
    }
}
