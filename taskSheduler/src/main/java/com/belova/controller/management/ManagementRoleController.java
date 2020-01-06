package com.belova.controller.management;

import com.belova.entity.UserRole;
import com.belova.service.role.RoleServiceImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.List;

public class ManagementRoleController {
    public TextField newRole;
    public TextField nameEditRole;

    public Button edit;
    public Button delete;
    public Button add;

    public ComboBox<UserRole> roles;
    public ComboBox<UserRole> roleDelete;

    public AnchorPane anchorPane;

    @Autowired
    private RoleServiceImpl roleService;

    private ObservableList<UserRole> userRoleObservableList = FXCollections.observableArrayList();
    private ObservableList<UserRole> userRoleDeleteObservableList = FXCollections.observableArrayList();

    private List<UserRole> userRoles;
    private Stage stage;

    @FXML
    public void initialize() {

    }

    @PostConstruct
    public void init() {
        edit.setOnAction(event -> editRole());
        delete.setOnAction(event -> deleteRole());
        add.setOnAction(event -> addRole());
    }

    private void addRole() {
        if (newRole.getText() != null) {
            UserRole userRole = roleService.addRole(newRole.getText().toUpperCase());
            roles.getItems().addAll(userRole);
            roleDelete.getItems().addAll(userRole);
            newRole.setText(null);
        } else {
            new Alert(Alert.AlertType.ERROR, "Введите имя новой роли").showAndWait();
        }
    }

    private void deleteRole() {
        if (roleDelete.getSelectionModel().getSelectedIndex() > -1) {
            roleService.removeRole(roleDelete.getSelectionModel().getSelectedItem().getId());
            roles.getItems().remove(roleDelete.getSelectionModel().getSelectedItem());
            roleDelete.getItems().remove(roleDelete.getSelectionModel().getSelectedItem());
            roleDelete.getSelectionModel().clearSelection();
            roleDelete.setValue(null);
        } else {
            new Alert(Alert.AlertType.ERROR, "Выберите роль для удаления").showAndWait();
        }
    }

    private void editRole() {
        if (nameEditRole.getText() != null && roles.getSelectionModel().getSelectedIndex() > -1) {
            UserRole userRole = roleService.changeRole(roles.getSelectionModel().getSelectedItem(), nameEditRole.getText().toUpperCase());
            UserRole selectedItem = roles.getSelectionModel().getSelectedItem();
            roles.getItems().remove(selectedItem);
            roleDelete.getItems().remove(selectedItem);
            roles.getItems().addAll(userRole);
            roleDelete.getItems().addAll(userRole);
            nameEditRole.setText(null);
            roles.getSelectionModel().clearSelection();
            roles.setValue(null);
        } else {
            new Alert(Alert.AlertType.ERROR, "Введите новое имя роли и выберите изменяемую роль").showAndWait();
        }
    }

    private void initComboBox() {
        userRoleObservableList.clear();
        userRoleDeleteObservableList.clear();
        userRoleObservableList.addAll(userRoles);
        userRoleDeleteObservableList.addAll(userRoles);
        roles.setItems(userRoleObservableList);
        roleDelete.setItems(userRoleDeleteObservableList);
    }

    public void setUserRoles(List<UserRole> userRoles) {
        this.userRoles = userRoles;
        initComboBox();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setStylesheet() {
        if (!anchorPane.getStylesheets().contains("css/MyStyle.css"))
            anchorPane.getStylesheets().add("css/MyStyle.css");
    }
}
