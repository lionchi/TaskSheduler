package com.belova.entity;

import com.sun.istack.internal.NotNull;

import javax.persistence.*;
import javax.persistence.Entity;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "user_role")
public class UserRole extends com.belova.entity.Entity {
    @NotNull
    @Column(name = "rolename", nullable = false)
    private String rolename;

    @ManyToMany(mappedBy = "userRoles")
    private List<User> users;

    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinTable(name = "UserRole_Privilege",
            joinColumns = {@JoinColumn(name = "userRole_id")},
            inverseJoinColumns = {@JoinColumn(name = "privilege_id")})
    private Set<Privilege> privileges = new HashSet<>();

    public UserRole() {
    }

    public String getRolename() {
        return rolename;
    }

    public void setRolename(String rolename) {
        this.rolename = rolename;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public Set<Privilege> getPrivileges() {
        return privileges;
    }

    public void setPrivileges(Set<Privilege> privileges) {
        this.privileges = privileges;
    }
}
