package com.belova.service;

import com.belova.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private EntityManager entityManager;

    @Override
    public User findUserByLogin(String login) {
        User user = entityManager.createQuery("select u from User as u where u.login = :login", User.class)
                .setParameter("login", login)
                .getSingleResult();
        return  user;
    }

    @PreAuthorize("hasPermission(#user, 'read')")
    //@PreAuthorize("hasRole('ROLE_TESTER')")
    public User test (User user) {
        return user;
    }
}
