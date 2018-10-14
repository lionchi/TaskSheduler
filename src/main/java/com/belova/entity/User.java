package com.belova.entity;

import com.sun.istack.internal.NotNull;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import javax.persistence.*;
import javax.persistence.Entity;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "user")
public class User extends com.belova.entity.Entity {
    @NotNull
    @Column(name = "login", nullable = false)
    private String login;

    @NotNull
    @Column(name = "password", nullable = false)
    private String password;

    @NotNull
    @Column(name = "fio", nullable = false)
    private String fio;

    @NotNull
    @Column(name = "department", nullable = false)
    private String department;

    @NotNull
    @Column(name = "post", nullable = false)
    private String post;

    @NotNull
    @Column(name = "enabled", nullable = false)
    private Integer enabled;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Task> tasks;

    @ManyToOne
    @JoinColumn(name = "user_role_id")
    private UserRole userRole;

    @Transient
    private StringProperty loginP = new SimpleStringProperty();
    @Transient
    private StringProperty passwordP = new SimpleStringProperty();
    @Transient
    private StringProperty fioP = new SimpleStringProperty();
    @Transient
    private StringProperty departmentP = new SimpleStringProperty();
    @Transient
    private StringProperty postP = new SimpleStringProperty();
    @Transient
    private StringProperty enabledP = new SimpleStringProperty();
    @Transient
    private StringProperty userRoleP = new SimpleStringProperty();
    @Transient
    private StringProperty privilegeP = new SimpleStringProperty();

    public User() {
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
        setLoginP(login);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFio() {
        return fio;
    }

    public void setFio(String fio) {
        this.fio = fio;
        setFioP(fio);
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
        setDepartmentP(department);
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
        setPostP(post);
    }

    public Integer getEnabled() {
        return enabled;
    }

    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
        setEnabledP(enabled);
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
        setUserRoleP(userRoleP.toString());
    }

    public String getLoginP() {
        return loginP.get();
    }

    public StringProperty LoginProperty() {
        return loginP;
    }

    public void setLoginP(String loginP) {
        this.loginP.set(loginP);
    }

    public String getPasswordP() {
        return passwordP.get();
    }

    public StringProperty PasswordProperty() {
        return passwordP;
    }

    public void setPasswordP(String passwordP) {
        this.passwordP.set(passwordP);
    }

    public String getFioP() {
        return fioP.get();
    }

    public StringProperty FioProperty() {
        return fioP;
    }

    public void setFioP(String fioP) {
        this.fioP.set(fioP);
    }

    public String getDepartmentP() {
        return departmentP.get();
    }

    public StringProperty DepartmentProperty() {
        return departmentP;
    }

    public void setDepartmentP(String departmentP) {
        this.departmentP.set(departmentP);
    }

    public String getPostP() {
        return postP.get();
    }

    public StringProperty PostProperty() {
        return postP;
    }

    public void setPostP(String postP) {
        this.postP.set(postP);
    }

    public String getEnabledP() {
        return enabledP.get();
    }

    public StringProperty EnabledProperty() {
        return enabledP;
    }

    public void setEnabledP(Integer enabledP) {
        if (enabledP == 1) {
            this.enabledP.set("Активен");
        } else {
            this.enabledP.set("Заблокирован");
        }
    }

    public String getUserRoleP() {
        return userRoleP.get();
    }

    public StringProperty UserRoleProperty() {
        return userRoleP;
    }

    public void setUserRoleP(String userRoleP) {
        this.userRoleP.set(userRoleP);
    }

    public String getPrivilegeP() {
        return privilegeP.get();
    }

    public StringProperty PriviligeProperty() {
        return privilegeP;
    }

    public void setPrivilegeP(Set<Privilege> privileges) {
        String string = privileges.stream()
                .collect(StringBuilder::new,
                        (stringBuilder, privilege) -> stringBuilder.append(privilege).append(","),
                        StringBuilder::append).toString();
        this.privilegeP.set(string);
    }

    public void initProperty () {
        this.setLoginP(this.getLogin());
        this.setFioP(this.getFio());
        this.setDepartmentP(this.getDepartment());
        this.setPostP(this.getPost());
        this.setUserRoleP(this.getUserRole().toString());
        this.setEnabledP(this.getEnabled());
        this.setPrivilegeP(this.getUserRole().getPrivileges());
    }

    @Override
    public String toString() {
        return fio;
    }
}
