package com.belova.controller;

import com.belova.common.UserSession;
import com.belova.entity.Task;
import com.belova.entity.User;
import com.belova.entity.enums.Complexity;
import com.belova.entity.enums.Status;
import com.belova.entity.enums.Type;
import com.belova.service.TasksServiceImpl;
import com.belova.service.UserServiceImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;

import javax.annotation.PostConstruct;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

public class ManagementTasksController {
    public TextField nameField;
    public TextArea description;

    public ComboBox<Status> chooseStatus;
    public ComboBox<Complexity> chooseComplexity;
    public ComboBox<Type> chooseType;
    public ComboBox<User> userComboBox;

    public DatePicker deadline;
    public CheckBox isQuickly;
    public Button add;

    private ObservableList<Status> observableListFoStatus = FXCollections.observableArrayList();
    private ObservableList<Complexity> observableListForComplexity = FXCollections.observableArrayList();
    private ObservableList<Type> observableListForType = FXCollections.observableArrayList();
    private ObservableList<User> observableListForUser = FXCollections.observableArrayList();

    @Autowired
    private TasksServiceImpl tasksService;
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private UserSession userSession;
    @Autowired
    private LeadController leadController;

    private Stage stage;
    private boolean isAdd;
    private Task editTask;

    @FXML
    public void initialize() {
    }

    @PostConstruct
    public void init() {
    }

    private void addTask() {
        if (ObjectUtils.allNotNull(nameField.getText(), description.getText(), deadline.getValue())
                && !org.springframework.util.ObjectUtils.isEmpty(new String[]{nameField.getText(), description.getText()})
                && chooseStatus.getSelectionModel().getSelectedIndex() > -1
                && chooseComplexity.getSelectionModel().getSelectedIndex() > -1
                && chooseType.getSelectionModel().getSelectedIndex() > -1
                && userComboBox.getSelectionModel().getSelectedIndex() > -1) {
            User userByLogin = userService.findUserByLogin(userSession.getLogin());
            try {
                tasksService.addTask(nameField.getText(), description.getText(), chooseStatus.getValue(), chooseComplexity.getValue(),
                        chooseType.getValue(), userComboBox.getValue(), deadline.getValue(), isQuickly.isSelected(), userByLogin);
            } catch (AccessDeniedException accessDeniedException) {
                new Alert(Alert.AlertType.ERROR, "У вас недостаточно прав доступа").showAndWait();
            }
            clearComboBox(chooseStatus, chooseComplexity, chooseType, userComboBox);
            nameField.clear();
            description.clear();
            deadline.setValue(null);
            leadController.initMainTable(false);
        } else {
            new Alert(Alert.AlertType.ERROR, "Заполните все поля").showAndWait();
        }
    }


    private void editT() {
        if (ObjectUtils.allNotNull(nameField.getText(), description.getText(), deadline.getValue(), userComboBox.getValue(),
                chooseStatus.getValue(), chooseComplexity.getValue(), chooseType.getValue())
                && !org.springframework.util.ObjectUtils.isEmpty(new String[]{nameField.getText(), description.getText()})) {
            User userByLogin = userService.findUserByLogin(userSession.getLogin());
            try {
                tasksService.editTask( editTask.getId(), nameField.getText(), description.getText(), chooseStatus.getValue(), chooseComplexity.getValue(),
                        chooseType.getValue(), userComboBox.getValue(), deadline.getValue(), isQuickly.isSelected(), userByLogin);
            } catch (AccessDeniedException accessDeniedException) {
                new Alert(Alert.AlertType.ERROR, "У вас недостаточно прав доступа").showAndWait();
            }
            leadController.initMainTable(false);
            stage.close();
        } else {
            new Alert(Alert.AlertType.ERROR, "Заполните все поля").showAndWait();
        }
    }


    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setAdd(boolean add) {
        isAdd = add;
        if (isAdd) {
            this.add.setText("ДОБАВИТЬ");
            this.add.setOnAction(event -> addTask());
        }
        else {
            this.add.setText("ИЗМЕНИТЬ");
            this.add.setOnAction(event -> editT());
        }
        initComboBoxs();
    }
    public void setEditTask(Task editTask) {
        this.editTask = editTask;
        initAllControl();
    }

    private void initComboBoxs() {
        observableListFoStatus.clear();
        observableListFoStatus.addAll(Arrays.asList(Status.values()));
        chooseStatus.setItems(observableListFoStatus);
        observableListForComplexity.clear();
        observableListForComplexity.addAll(Arrays.asList(Complexity.values()));
        chooseComplexity.setItems(observableListForComplexity);
        observableListForType.clear();
        observableListForType.addAll(Arrays.asList(Type.values()));
        chooseType.setItems(observableListForType);
        List<User> allDepartmentUsers = userService.getAllDepartmentUsers(userSession.getId());
        observableListForUser.clear();
        observableListForUser.addAll(allDepartmentUsers);
        userComboBox.setItems(observableListForUser);
    }

    private void initAllControl() {
        nameField.setText(editTask.getName());
        description.setText(editTask.getDescription());
        deadline.setValue(editTask.getDeadline().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        isQuickly.setSelected(editTask.getQuickly());
        chooseStatus.setValue(editTask.getStatus());
        chooseType.setValue(editTask.getType());
        chooseComplexity.setValue(editTask.getComplexity());
        userComboBox.setValue(editTask.getUser());
    }

    private void clearComboBox(ComboBox<?>... comboBoxes) {
        for (ComboBox<?> comboBox : comboBoxes) {
            comboBox.getSelectionModel().clearSelection();
            comboBox.setValue(null);
        }
    }
}
