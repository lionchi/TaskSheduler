package com.belova.service;

import com.belova.common.MD5;
import com.belova.common.UserSession;
import com.belova.entity.User;
import com.belova.entity.UserRole;
import org.apache.commons.text.CharacterPredicates;
import org.apache.commons.text.RandomStringGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.encoding.MessageDigestPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    public List<User> getAllUsers() {
        List<User> users = entityManager.createQuery("select u from User as u", User.class)
                .getResultList();
        users.forEach(User::initProperty);
        return users;
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String addUser(String fio, String login, String post, String department, UserRole role) {
        String password = generatedPassword();
/*        String query = String.format("insert into User (department, fio, login, password, post, enabled, user_role_id) values('%s','%s','%s',md5('%s'),'%s',%s,%s)",
                department, fio, login, password, post, 1, role.getId());
        entityManager.createNativeQuery(query).executeUpdate();*/
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
    @PreAuthorize("hasRole('ROLE_ADMIN')")
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
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void deleteUser(String fio) {
        User user = entityManager.createQuery("select u from User as u where u.fio = :fio", User.class)
                .setParameter("fio", fio)
                .getSingleResult();
        entityManager.remove(user);
    }

    @Override
    public void changePassword(String oldPass, String newPass) {
        User user = entityManager.find(User.class, userSession.getId());
        if (user.getPassword().equals(oldPass)) {
            user.setPassword(MD5.crypt(newPass));
        }
    }

    private String generatedPassword() {
        RandomStringGenerator randomStringGenerator = new RandomStringGenerator.Builder()
                .withinRange('0', 'z')
                .filteredBy(CharacterPredicates.LETTERS, CharacterPredicates.DIGITS)
                .build();
        return randomStringGenerator.generate(10);
    }
}
