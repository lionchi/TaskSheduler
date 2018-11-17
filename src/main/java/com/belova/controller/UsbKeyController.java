package com.belova.controller;

import com.belova.entity.User;
import com.belova.service.UsbKeyServiceImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import javax.swing.*;
import javax.annotation.PostConstruct;
import java.io.File;
import java.util.List;

public class UsbKeyController {
    public TextField serialNumber;
    public TextField filePath;

    public ComboBox<User> checkUser;

    public Button okButton;
    public Button chooseButton;
    public Button deleteButton;

    @Autowired
    private UsbKeyServiceImpl usbKeyService;

    private Stage stage;
    private ObservableList<User> userObservableList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {

    }

    @PostConstruct
    public void init() {
        chooseButton.setOnAction(event -> showExplorerFile());
        okButton.setOnAction(event -> ok());
        deleteButton.setOnAction(event -> delete());
    }

    private void ok() {
        if (ObjectUtils.allNotNull(checkUser.getValue())) {
            usbKeyService.addUsbKey(filePath.getText(), checkUser.getValue());
            serialNumber.clear();
            filePath.clear();
            checkUser.getSelectionModel().clearSelection();
            checkUser.setValue(null);
        } else {
            new Alert(Alert.AlertType.ERROR, "Заполните все обязательные поля").showAndWait();
        }
    }

    private void delete() {
        if (ObjectUtils.allNotNull(checkUser.getValue())) {
            boolean result = usbKeyService.deleteKey(checkUser.getValue());
            if (!result) {
                new Alert(Alert.AlertType.ERROR, "Для выбранного пользователя ключ отсутсвует").showAndWait();
            }
            checkUser.getSelectionModel().clearSelection();
            checkUser.setValue(null);
        } else {
            new Alert(Alert.AlertType.ERROR, "Выберите пользователя").showAndWait();
        }
    }


    private void showExplorerFile()  {
        //FileChooser fileChooser = new FileChooser();
        //fileChooser.setTitle("Выберите usb-ключ");
        //fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("EXE", "*.exe"));
        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setTitle("Выберите usb-ключ");

        try {
            //File file = fileChooser.showOpenDialog(stage);
            File result = dirChooser.showDialog(stage);
            filePath.setText(result.getAbsolutePath());
        } catch (NullPointerException e) {
        }

    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setUserObservableList(List<User> userList) {
        userObservableList.clear();
        userObservableList.addAll(userList);
        checkUser.setItems(userObservableList);
    }
}
