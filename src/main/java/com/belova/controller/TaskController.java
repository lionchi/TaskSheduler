package com.belova.controller;

import com.belova.common.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.springframework.beans.factory.annotation.Autowired;

public class TaskController {
    public Button testButton;
    @Autowired
    private UserSession userSession;

    @FXML
    public void initialize() {
        testButton.setOnAction(event -> System.out.println(userSession.getFio()));
    }
}
