package com.belova.controller;

import com.belova.common.supporting.UserSession;
import com.belova.service.user.UserServiceImpl;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.springframework.beans.factory.annotation.Autowired;

public class TaskController {
    public Button testButton;
    @Autowired
    private UserSession userSession;
    @Autowired
    private UserServiceImpl userService;

    @FXML
    public void initialize() {

    }

}
