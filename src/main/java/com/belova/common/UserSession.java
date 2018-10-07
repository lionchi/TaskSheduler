package com.belova.common;

import org.springframework.stereotype.Component;

@Component
public class UserSession {
    private Long id;
    private String fio;
    private String login;
    private String roles;

    public UserSession() {
    }

    public UserSession(Long id, String fio, String login, String roles) {
        this.id = id;
        this.fio = fio;
        this.login = login;
        this.roles = roles;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFio() {
        return fio;
    }

    public void setFio(String fio) {
        this.fio = fio;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }
}
