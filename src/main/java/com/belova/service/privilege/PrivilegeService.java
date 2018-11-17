package com.belova.service.privilege;

import com.belova.entity.Privilege;
import com.belova.entity.UserRole;

import java.util.List;

public interface PrivilegeService {
    List<Privilege> allPrivilege ();

    void addPrivilege(String name);

    void fastenPrivilege (UserRole role, Privilege privilege);

    void editPrivilege(Privilege privilege, String newName);

    void deletePrivilege(UserRole userRole, Privilege privilege);

    void deletePrivilege(Privilege privilege);
}
