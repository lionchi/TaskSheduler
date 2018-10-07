package com.belova.entity.enums;

public enum Type {

    NEW("Новый функционал"),
    BAG("Инцидент");

    private String value;

    Type(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
