package com.belova.controller;

import com.belova.common.UserSession;
import com.belova.entity.User;
import com.belova.service.UserServiceImpl;
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
