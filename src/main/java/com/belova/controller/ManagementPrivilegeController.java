package com.belova.controller;

import com.belova.entity.Privilege;
import com.belova.entity.UserRole;
import com.belova.service.PrivilegeServiceImpl;
import com.belova.service.RoleServiceImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Set;

public class ManagementPrivilegeController {
    public TextField newPrivilegeName;
    public TextField editPrivilegeName;

    public Button showAllPrivilege;
    public Button add;
    public Button fasten;
    public Button edit;
    public Button delete;

    public ComboBox<Privilege> deletePrivilege;
    public ComboBox<UserRole> checkRole1;
    public ComboBox<Privilege> checkPrivilege;
    public ComboBox<UserRole> checkRole2;
    public ComboBox<Privilege> fastenPrivileges;

    @Autowired
    private PrivilegeServiceImpl privilegeService;
    @Autowired
    private RoleServiceImpl roleService;
    @Autowired
    private AdminController adminController;

    private ObservableList<UserRole> userRoleObservableList = FXCollections.observableArrayList();
    private ObservableList<Privilege> privilegeObservableList2 = FXCollections.observableArrayList();
    private ObservableList<Privilege> privilegeObservableList1 = FXCollections.observableArrayList();

    private List<UserRole> userRoleList;
    private List<Privilege> privilegeSet;
    private Stage stage;

    @FXML
    private void initialize() {

    }

    @PostConstruct
    private void init() {
        checkRole2.setOnAction(event -> initPrivilege());
        add.setOnAction(event -> addPrivilege());
        fasten.setOnAction(event -> fasPrivileges());
        edit.setOnAction(event -> editPrivilege());
        delete.setOnAction(event -> delPrivilege());
        showAllPrivilege.setOnAction(event -> showAll());
    }

    private void showAll() {
        checkRole2.getSelectionModel().clearSelection();
        checkRole2.setValue(null);
        List<Privilege> privileges = privilegeService.allPrivilege();
        privilegeObservableList2.clear();
        privilegeObservableList2.addAll(privileges);
        deletePrivilege.setItems(privilegeObservableList2);
    }

    private void addPrivilege() {
        if (newPrivilegeName.getText() != null && !org.springframework.util.ObjectUtils.isEmpty(newPrivilegeName.getText())) {
            privilegeService.addPrivilege(newPrivilegeName.getText());
            checkRole1.getSelectionModel().clearSelection();
            checkRole1.setValue(null);
            newPrivilegeName.setText(null);
            updateCheckBoxWithPrivilege(privilegeObservableList1, fastenPrivileges, checkPrivilege);
            updateCheckBoxWithPrivilege(privilegeObservableList2, deletePrivilege);
        } else {
            new Alert(Alert.AlertType.ERROR, "Заполните все поля").showAndWait();
        }
    }

    private void fasPrivileges() {
        if (checkRole1.getSelectionModel().getSelectedIndex() > -1 && fastenPrivileges.getSelectionModel().getSelectedIndex() > -1) {
            privilegeService.fastenPrivilege(checkRole1.getSelectionModel().getSelectedItem(), fastenPrivileges.getSelectionModel().getSelectedItem());
            checkRole1.getSelectionModel().clearSelection();
            checkRole1.setValue(null);
            fastenPrivileges.getSelectionModel().clearSelection();
            fastenPrivileges.setValue(null);
            updateCheckBoxWithRole(userRoleObservableList, checkRole1, checkRole2);
        } else {
            new Alert(Alert.AlertType.ERROR, "Заполните все поля").showAndWait();
        }
    }


    private void editPrivilege() {
        if (checkPrivilege.getSelectionModel().getSelectedIndex() > -1 && editPrivilegeName.getText() != null
                && !org.springframework.util.ObjectUtils.isEmpty(editPrivilegeName.getText())) {
            privilegeService.editPrivilege(checkPrivilege.getSelectionModel().getSelectedItem(), editPrivilegeName.getText());
            checkPrivilege.getSelectionModel().clearSelection();
            checkPrivilege.setValue(null);
            editPrivilegeName.setText(null);
            updateCheckBoxWithRole(userRoleObservableList, checkRole1, checkRole2);
            updateCheckBoxWithPrivilege(privilegeObservableList1, checkPrivilege);
            //updateCheckBoxWithPrivilege(privilegeObservableList2, deletePrivilege);
        } else {
            new Alert(Alert.AlertType.ERROR, "Заполните все поля").showAndWait();
        }
    }

    private void delPrivilege() {
        if (checkRole2.getSelectionModel().getSelectedIndex() > -1 && deletePrivilege.getSelectionModel().getSelectedIndex() > -1) {
            Privilege selectedItem = deletePrivilege.getSelectionModel().getSelectedItem();
            privilegeService.deletePrivilege(checkRole2.getSelectionModel().getSelectedItem(), deletePrivilege.getSelectionModel().getSelectedItem());
            deletePrivilege.getItems().remove(selectedItem);
            deletePrivilege.getSelectionModel().clearSelection();
            deletePrivilege.setValue(null);
            updateCheckBoxWithRole(userRoleObservableList, checkRole1);
            updateCheckBoxWithPrivilege(privilegeObservableList1, deletePrivilege);
        } else if (checkRole2.getSelectionModel().getSelectedIndex() < 0 && deletePrivilege.getSelectionModel().getSelectedIndex() > -1) {
            try {
                Privilege selectedItem = deletePrivilege.getSelectionModel().getSelectedItem();
                privilegeService.deletePrivilege(deletePrivilege.getSelectionModel().getSelectedItem());
                deletePrivilege.getItems().remove(selectedItem);
                deletePrivilege.getSelectionModel().clearSelection();
                deletePrivilege.setValue(null);
                updateCheckBoxWithRole(userRoleObservableList, checkRole1);
                updateCheckBoxWithPrivilege(privilegeObservableList1, fastenPrivileges, checkPrivilege, deletePrivilege);
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, "Право доступа не отвязано от роли. Отвяжите данное право от всех ролей, с котороыми оно связано. " +
                        "Затем продолжите удаление").showAndWait();
            }
        } else {
            new Alert(Alert.AlertType.ERROR, "Выберите роль").showAndWait();
        }
    }

    private void initPrivilege() {
        if (checkRole2.getSelectionModel().getSelectedItem() != null) {
            Set<Privilege> privileges = checkRole2.getSelectionModel().getSelectedItem().getPrivileges();
            privilegeObservableList2.clear();
            privilegeObservableList2.addAll(privileges);
            deletePrivilege.setItems(privilegeObservableList2);
        }
    }

    @SafeVarargs
    private final void updateCheckBoxWithRole(ObservableList<UserRole> observableList, ComboBox<UserRole>... userRoleComboBoxs) {
        observableList.clear();
        List<UserRole> userRoleList = roleService.getAllRoles();
        observableList.addAll(userRoleList);
        for (ComboBox<UserRole> userRoleComboBox : userRoleComboBoxs) {
            userRoleComboBox.setItems(observableList);
        }
    }

    @SafeVarargs
    private final void updateCheckBoxWithPrivilege(ObservableList<Privilege> observableList, ComboBox<Privilege>... privilegeComboBoxs) {
        observableList.clear();
        List<Privilege> userRoleList = privilegeService.allPrivilege();
        observableList.addAll(userRoleList);
        for (ComboBox<Privilege> privilegeComboBox : privilegeComboBoxs) {
            privilegeComboBox.setItems(observableList);
        }
    }

    public void setUserRoleList(List<UserRole> userRoleList) {
        this.userRoleList = userRoleList;
        userRoleObservableList.clear();
        userRoleObservableList.addAll(userRoleList);
        checkRole1.setItems(userRoleObservableList);
        checkRole2.setItems(userRoleObservableList);
    }

    public void setPrivilegeSet(List<Privilege> privilegeSet) {
        this.privilegeSet = privilegeSet;
        privilegeObservableList1.clear();
        privilegeObservableList1.addAll(privilegeSet);
        checkPrivilege.setItems(privilegeObservableList1);
        fastenPrivileges.setItems(privilegeObservableList1);
        privilegeObservableList2.clear();
        privilegeObservableList2.addAll(privilegeSet);
        deletePrivilege.setItems(privilegeObservableList2);
    }

    public void setStage(Stage stage) {
        this.stage = stage;
        this.stage.setOnCloseRequest(event -> adminController.initMainTableAndPostBox());
    }
}
