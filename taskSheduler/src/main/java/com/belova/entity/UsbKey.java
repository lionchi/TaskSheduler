package com.belova.entity;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "usb_key")
public class UsbKey extends com.belova.entity.Entity {
    @NotNull
    @Column(name = "sequence_key", nullable = false)
    private String sequence_key;

    @Lob
    @Column(name="bytes")
    private byte[] bytes;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    public UsbKey() {
    }

    public String getKey() {
        return sequence_key;
    }

    public void setKey(String sequence_key) {
        this.sequence_key = sequence_key;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
