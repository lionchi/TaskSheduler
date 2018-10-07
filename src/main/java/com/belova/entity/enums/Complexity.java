package com.belova.entity.enums;

public enum  Complexity {

    EASY("Легкая"),
    NORMAL("Средняя"),
    HARD("Сложная");

    private String value;

    Complexity(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
