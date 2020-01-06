package com.belova.controller;

import com.belova.common.supporting.MD5;
import com.belova.service.user.UserServiceImpl;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;

public class ChangePasswordController {
    public PasswordField newPass;
    public PasswordField oldPass;
    public PasswordField checkNewPass;

    public Button edit;

    public AnchorPane anchorPane;

    @Autowired
    private UserServiceImpl userService;

    private Stage stage;

    @FXML
    private void initialize() {

    }

    @PostConstruct
    private void init() {
        edit.setOnAction(event -> editPass());
    }

    private void editPass() {
        if (org.apache.commons.lang3.ObjectUtils.allNotNull(oldPass.getText(), newPass.getText(), checkNewPass.getText())
                && !StringUtils.isEmpty(oldPass.getText()) && !StringUtils.isEmpty(newPass.getText())
                && !StringUtils.isEmpty(checkNewPass.getText()) && newPass.getText().equals(checkNewPass.getText())) {
            userService.changePassword(MD5.crypt(oldPass.getText()), newPass.getText());
            oldPass.clear();
            newPass.clear();
            checkNewPass.clear();
            stage.close();
            new Alert(Alert.AlertType.INFORMATION, "Пароль успешно изменен").showAndWait();
        } else {
            new Alert(Alert.AlertType.ERROR, "Заполните корректно все поля").showAndWait();
        }
    }

    public void setStylesheet() {
        if (!anchorPane.getStylesheets().contains("css/MyStyle.css"))
            anchorPane.getStylesheets().add("css/MyStyle.css");
    }

    public void setStage(Stage newStage) {
        this.stage = newStage;
    }

}
