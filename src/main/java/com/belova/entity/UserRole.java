package com.belova.entity;

import com.sun.istack.internal.NotNull;

import javax.persistence.*;
import javax.persistence.Entity;

@Entity
@Table(name = "user_role")
public class UserRole extends com.belova.entity.Entity {
    @NotNull
    @Column(name = "rolename", nullable = false)
    private String rolename;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public UserRole() {
    }

    public String getRolename() {
        return rolename;
    }

    public void setRolename(String rolename) {
        this.rolename = rolename;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
