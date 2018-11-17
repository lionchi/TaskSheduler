package com.belova.controller;

import com.belova.common.UserSession;
import com.belova.entity.User;
import com.belova.entity.UserRole;
import com.belova.service.UserServiceImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.authentication.UserServiceBeanDefinitionParser;

import javax.annotation.PostConstruct;
import java.util.List;

public class ManagementUserController {
    public TextField fioField;
    public TextField loginField;
    public TextField postField;
    public TextField departmentField;

    public ComboBox<UserRole> chooseRole;
    public CheckBox isActive;

    public Button add;

    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private AdminController adminController;
    @Autowired
    private LeadController leadController;
    @Autowired
    private UserSession userSession;

    private Stage stage;
    private boolean isLead;
    private User editUser;
    private List<UserRole> userRoleList;
    private ObservableList<UserRole> observableList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {

    }

    @PostConstruct
    private void init() {

    }

    private void initComboBox() {
        observableList.clear();
        observableList.addAll(userRoleList);
        chooseRole.setItems(observableList);
    }

    private void initField() {
        fioField.setText(editUser.getFio());
        postField.setText(editUser.getPost());
        departmentField.setText(editUser.getDepartment());
        loginField.setText(editUser.getLogin());
        isActive.setSelected(editUser.getEnabled() == 1);
        chooseRole.setValue(editUser.getUserRole());
    }

    public void setAdd(boolean add) {
        if (add) {
            this.add.setText("Добавить");
            this.add.setOnAction(event -> addUser());
            isActive.setVisible(false);
        } else {
            this.add.setText("Изменить");
            this.add.setOnAction(event -> editUser());
            isActive.setVisible(true);
        }
    }

    private void addUser() {
        if (ObjectUtils.allNotNull(fioField.getText(), loginField.getText(), postField.getText(), departmentField.getText(), chooseRole.getSelectionModel().getSelectedItem())
                && !org.springframework.util.ObjectUtils.isEmpty(new String[]{fioField.getText(), loginField.getText(), postField.getText(), departmentField.getText()})) {
            String password = userService.addUser(fioField.getText(), loginField.getText(), postField.getText(),
                    departmentField.getText(), chooseRole.getSelectionModel().getSelectedItem());
            new Alert(Alert.AlertType.INFORMATION, String.format("Пользователь %s успешно добавлен. Сгенерированный пароль %s", fioField.getText(), password)).showAndWait();
            clearAllField(fioField, loginField, postField, departmentField);
            chooseRole.getSelectionModel().clearSelection();
            chooseRole.setValue(null);
            if (!isLead) adminController.initMainTableAndPostBox();
            else leadController.initMainTable(true, false);
        } else {
            new Alert(Alert.AlertType.ERROR, "Заполните все поля").showAndWait();
        }
    }

    private void editUser() {
        if (ObjectUtils.allNotNull(fioField.getText(), loginField.getText(), postField.getText(), departmentField.getText(), chooseRole.getSelectionModel().getSelectedItem())
                && !org.springframework.util.ObjectUtils.isEmpty(new String[]{fioField.getText(), loginField.getText(), postField.getText(), departmentField.getText()})) {
            userService.editUser(editUser, fioField.getText(), loginField.getText(), postField.getText(),
                    departmentField.getText(), isActive.isSelected() ? 1 : 0, chooseRole.getSelectionModel().getSelectedItem());
            clearAllField(fioField, loginField, postField, departmentField);
            if (!isLead) adminController.initMainTableAndPostBox();
            else leadController.initMainTable(true, false);
            stage.close();
        } else {
            new Alert(Alert.AlertType.ERROR, "Заполните все поля").showAndWait();
        }
    }

    private void clearAllField(TextField... textFields) {
        for (TextField textField : textFields) {
            textField.setText(null);
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setUserRoleList(List<UserRole> userRoleList) {
        this.userRoleList = userRoleList;
        initComboBox();
    }

    public void setLead(boolean lead, String department) {
        isLead = lead;
        if (department != null) {
            departmentField.setText(department);
        }
        departmentField.setEditable(false);
    }

    public void setEditUser(User editUser) {
        this.editUser = editUser;
        initField();
    }
}
