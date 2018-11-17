package com.belova.service;

import com.belova.common.MD5;
import com.belova.common.UserSession;
import com.belova.entity.User;
import com.belova.entity.UserRole;
import org.apache.commons.text.CharacterPredicates;
import org.apache.commons.text.RandomStringGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private EntityManager entityManager;
    @Autowired
    private UserSession userSession;

    @Override
    public User findUserByLogin(String login) {
        User user = entityManager.createQuery("select u from User as u where u.login = :login", User.class)
                .setParameter("login", login)
                .getSingleResult();
        return user;
    }

    @Override
    @PreAuthorize("hasRole('ROLE_LEAD')")
    public User findUserByFio(String fio) {
        User user = entityManager.createQuery("select u from User as u where u.fio = :fio", User.class)
                .setParameter("fio", fio)
                .getSingleResult();
        return user;
    }

    @Override
    //@PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<User> getAllUsers() {
        List<User> users = entityManager.createQuery("select u from User as u", User.class)
                .getResultList();
        users.forEach(User::initProperty);
        return users;
    }

    @Override
    @PreAuthorize("hasRole('ROLE_LEAD')")
    public List<User> getAllDepartmentUsers(Long id) {
        User user = entityManager.find(User.class, id);
        return entityManager.createQuery("select u from User as u where u.department = :department and u.userRole.rolename <> :rolename", User.class)
                .setParameter("department", user.getDepartment())
                .setParameter("rolename", "ADMIN")
                .getResultList();
    }

    @Override
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_LEAD')")
    public String addUser(String fio, String login, String post, String department, UserRole role) {
        String password = generatedPassword();
        User newUser = new User();
        newUser.setFio(fio);
        newUser.setLogin(login);
        newUser.setPassword(MD5.crypt(password));
        newUser.setDepartment(department);
        newUser.setUserRole(role);
        newUser.setEnabled(1);
        newUser.setPost(post);
        entityManager.persist(newUser);
        return password;
    }

    @Override
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_LEAD')")
    public void editUser(User editUser, String fio, String login, String post, String department, Integer enabled, UserRole role) {
        User merge = entityManager.merge(editUser);
        merge.setUserRole(role);
        merge.setFio(fio);
        merge.setDepartment(department);
        merge.setEnabled(enabled);
        merge.setPost(post);
        merge.setLogin(login);
    }

    @Override
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_LEAD')")
    public void deleteUser(String fio) {
        User user = entityManager.createQuery("select u from User as u where u.fio = :fio", User.class)
                .setParameter("fio", fio)
                .getSingleResult();
        entityManager.remove(user);
    }

    @Override
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_LEAD','ROLE_USER')")
    public void changePassword(String oldPass, String newPass) {
        User user = entityManager.find(User.class, userSession.getId());
        if (user.getPassword().equals(oldPass)) {
            user.setPassword(MD5.crypt(newPass));
        }
    }

    @Override
    public String getDepartmentCurrentUser(Long userId) {
        User user = entityManager.find(User.class, userId);
        return user.getDepartment();
    }

    private String generatedPassword() {
        RandomStringGenerator randomStringGenerator = new RandomStringGenerator.Builder()
                .withinRange('0', 'z')
                .filteredBy(CharacterPredicates.LETTERS, CharacterPredicates.DIGITS)
                .build();
        return randomStringGenerator.generate(10);
    }
}
