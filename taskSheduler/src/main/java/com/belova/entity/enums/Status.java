package com.belova.entity.enums;

public enum Status {

    FAMILIARIZATION("Ознакомление"),
    ACTIVE("В Разработке"),
    TEST("Тестирование"),
    IMPLEMENTATION("Внедрение"),
    FIHISH("Решена");

    private String value;

    Status(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
