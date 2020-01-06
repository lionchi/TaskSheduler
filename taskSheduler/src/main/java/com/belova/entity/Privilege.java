package com.belova.entity;

import javax.persistence.Column;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@javax.persistence.Entity
@Table(name = "privilege")
public class Privilege extends Entity {

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToMany(mappedBy = "privileges")
    private List<UserRole> userRoles = new ArrayList<>();

    public Privilege() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<UserRole> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(List<UserRole> userRoles) {
        this.userRoles = userRoles;
    }

    @Override
    public String toString() {
        return name;
    }
}
