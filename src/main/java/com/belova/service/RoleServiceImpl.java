package com.belova.service;

import com.belova.entity.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class RoleServiceImpl implements RoleService {

    @Autowired
    private EntityManager entityManager;

    @Override
    @PreAuthorize(("hasRole('ROLE_ADMIN')"))
    public List<UserRole> getAllRoles() {
        List<UserRole> userRoles = entityManager.createQuery("select r from UserRole as r", UserRole.class).getResultList();
        return  userRoles;
    }

    @Override
    @PreAuthorize(("hasRole('ROLE_ADMIN')"))
    public void removeRole(Long id) {
        UserRole userRole = entityManager.find(UserRole.class, id);
        entityManager.remove(userRole);
    }

    @Override
    @PreAuthorize(("hasRole('ROLE_ADMIN')"))
    public UserRole changeRole(UserRole userRole, String newRoleName) {
        UserRole merge = entityManager.merge(userRole);
        merge.setRolename(newRoleName);
        return merge;
    }

    @Override
    @PreAuthorize(("hasRole('ROLE_ADMIN')"))
    public UserRole addRole(String roleName) {
        UserRole userRole = new UserRole();
        userRole.setRolename(roleName);
        entityManager.persist(userRole);
        return  userRole;
    }
}
