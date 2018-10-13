package com.belova.service;

import com.belova.entity.UserRole;

import java.util.List;

public interface RoleService {
    List<UserRole> getAllRoles();

    void removeRole(String roleName);

    UserRole changeRole (UserRole userRole, String newRoleName);

    UserRole addRole (String roleName);
}
