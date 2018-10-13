package com.belova.service;

import com.belova.entity.Privilege;
import com.belova.entity.UserRole;
import javafx.scene.control.Alert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class PrivilegeServiceImpl implements PrivilegeService {
    @Autowired
    private EntityManager entityManager;

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<Privilege> allPrivilege() {
        return entityManager.createQuery("select p from Privilege as p", Privilege.class).getResultList();
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void addPrivilege(String name) {
        try {
            Privilege searchPrivilege = entityManager.createQuery("select p from Privilege as p where p.name = :name", Privilege.class)
                    .setParameter("name", name)
                    .getSingleResult();
            if (searchPrivilege != null) {
                new Alert(Alert.AlertType.ERROR, "Такое право доступа уже существует").showAndWait();
                return;
            }
        } catch (NoResultException e) {
            Privilege privilege = new Privilege();
            privilege.setName(name);
            entityManager.persist(privilege);
        }
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void fastenPrivilege(UserRole role, Privilege privilege) {
        UserRole merge = entityManager.merge(role);
        Privilege mergePrivilege = entityManager.merge(privilege);
        if (merge.getPrivileges().contains(privilege)) {
            new Alert(Alert.AlertType.ERROR, "Такое право доступа уже добавлено к этой роли").showAndWait();
            return;
        }
        merge.getPrivileges().add(mergePrivilege);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void editPrivilege(Privilege privilege, String newName) {
        try {
            Privilege searchPrivilege = entityManager.createQuery("select p from Privilege as p where p.name = :name", Privilege.class)
                    .setParameter("name", newName)
                    .getSingleResult();
            if (searchPrivilege != null) {
                new Alert(Alert.AlertType.ERROR, "Такое право доступа уже существует").showAndWait();
                return;
            }
        } catch (NoResultException e) {
            Privilege merge = entityManager.merge(privilege);
            merge.setName(newName);
        }
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void deletePrivilege(UserRole userRole, Privilege privilege) {
        UserRole merge = entityManager.merge(userRole);
        Privilege mergePrivilege = entityManager.merge(privilege);
        merge.getPrivileges().remove(mergePrivilege);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void deletePrivilege(Privilege privilege) {
        Privilege merge = entityManager.merge(privilege);
        entityManager.remove(merge);
    }
}
