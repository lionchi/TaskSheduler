package com.belova.entity;

import com.belova.entity.enums.Complexity;
import com.belova.entity.enums.Status;
import com.belova.entity.enums.Type;
import com.sun.istack.internal.NotNull;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import javax.persistence.*;
import javax.persistence.Entity;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
@Table(name = "task")
public class Task extends com.belova.entity.Entity {

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "isQuickly")
    private Boolean isQuickly;

    @NotNull
    @Column(name = "deadline", nullable = false)
    private Date deadline;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "complexity", nullable = false)
    private Complexity complexity;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private Type type;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @Transient
    private StringProperty nameP = new SimpleStringProperty();
    @Transient
    private StringProperty quicklyP = new SimpleStringProperty();
    @Transient
    private StringProperty deadlineP = new SimpleStringProperty();
    @Transient
    private StringProperty statusP = new SimpleStringProperty();
    @Transient
    private StringProperty complexityP = new SimpleStringProperty();
    @Transient
    private StringProperty typeP = new SimpleStringProperty();
    @Transient
    private StringProperty userP = new SimpleStringProperty();

    public Task() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        setNameP(name);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getQuickly() {
        return isQuickly;
    }

    public void setQuickly(Boolean quickly) {
        isQuickly = quickly;
        setQuicklyP(quickly);
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
        setDeadlineP(deadline);
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
        setStatusP(status);
    }

    public Complexity getComplexity() {
        return complexity;
    }

    public void setComplexity(Complexity complexity) {
        this.complexity = complexity;
        setComplexityP(complexity);
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
        setTypeP(type);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        setUserP(user);
    }


    public String getNameP() {
        return nameP.get();
    }

    public StringProperty NameProperty() {
        return nameP;
    }

    public void setNameP(String nameP) {
        this.nameP.set(nameP);
    }

    public String getQuicklyPP() {
        return quicklyP.get();
    }

    public StringProperty QuicklyProperty() {
        return quicklyP;
    }

    public void setQuicklyP(Boolean quickly) {
        if (quickly.equals(Boolean.TRUE)) {
            this.quicklyP.set("Да");
        } else {
            this.quicklyP.set("Нет");
        }
    }

    public String getDeadlineP() {
        return deadlineP.get();
    }

    public StringProperty DeadlineProperty() {
        return deadlineP;
    }

    public void setDeadlineP(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.mm.yyyy");
        this.deadlineP.set(simpleDateFormat.format(date));
    }

    public String getStatusP() {
        return statusP.get();
    }

    public StringProperty StatusPProperty() {
        return statusP;
    }

    public void setStatusP(Status status) {
        this.statusP.set(status.toString());
    }

    public String getComplexityP() {
        return complexityP.get();
    }

    public StringProperty ComplexityProperty() {
        return complexityP;
    }

    public void setComplexityP(Complexity complexity) {
        this.complexityP.set(complexity.toString());
    }

    public String getTypeP() {
        return typeP.get();
    }

    public StringProperty TypeProperty() {
        return typeP;
    }

    public void setTypeP(Type type) {
        this.typeP.set(type.toString());
    }

    public String getUserP() {
        return userP.get();
    }

    public StringProperty UserProperty() {
        return userP;
    }

    public void setUserP(User user) {
        this.userP.set(user.toString());
    }

    public void initProperty() {
        this.setNameP(this.getName());
        this.setComplexityP(this.getComplexity());
        this.setDeadlineP(this.getDeadline());
        this.setQuicklyP(this.getQuickly());
        this.setStatusP(this.getStatus());
        this.setTypeP(this.getType());
        this.setUserP(this.getUser());
    }
}
