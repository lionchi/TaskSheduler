package com.belova.controller;

import com.belova.entity.Task;
import com.belova.entity.enums.Status;
import com.belova.service.TasksService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.Arrays;

public class ChangeStatusController {
    public Button changeButton;
    public ComboBox<Status> statusBox;

    @Autowired
    private TasksService tasksService;
    @Autowired
    private UserController userController;

    private Stage stage;
    private Task currentTask;

    private ObservableList<Status> observableListForStatus = FXCollections.observableArrayList();

    @FXML
    public void initialize() {

    }

    @PostConstruct
    public void init() {
        changeButton.setOnAction(event -> changeStatus());
    }

    private void changeStatus() {
        tasksService.changeStatus(currentTask.getId(), statusBox.getValue());
        new Alert(Alert.AlertType.INFORMATION, "Статус успешно изменен").showAndWait();
        userController.initMainTable(false);
        stage.close();
    }


    private void initComboBox() {
        observableListForStatus.clear();
        observableListForStatus.addAll(Arrays.asList(Status.values()));
        statusBox.setItems(observableListForStatus);
        statusBox.setValue(currentTask.getStatus());
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setCurrentTask(Task currentTask) {
        this.currentTask = currentTask;
        initComboBox();
    }
}
