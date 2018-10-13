package com.belova.controller;

import com.belova.common.UserSession;
import com.belova.entity.Privilege;
import com.belova.entity.User;
import com.belova.entity.UserRole;
import com.belova.service.PrivilegeServiceImpl;
import com.belova.service.RoleServiceImpl;
import com.belova.service.UserServiceImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class AdminController {

    public AnchorPane anchorPane;

    public TableView<User> mainTable;
    public TableColumn<User, String> fioColumn;
    public TableColumn<User, String> loginColumn;
    public TableColumn<User, String> postColumn;
    public TableColumn<User, String> departmentColumn;
    public TableColumn<User, String> enabledColumn;
    public TableColumn<User, String> roleColumn;
    public TableColumn<User, String> privilegesColumn;


    public Label editUser;
    public Label addUser;
    public Label deleteUser;
    public Label managementOfRoles;
    public Label managementOfAccess;
    public Label statistics;
    public Label changePass;
    public Label logOut;

    public ComboBox<String> departmentBox;
    public ComboBox<String> roleBox;

    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private RoleServiceImpl roleService;
    @Autowired
    private PrivilegeServiceImpl privilegeService;
    @Autowired
    private UserSession userSession;
    @Qualifier("managementOfRoleView")
    @Autowired
    private ConfigurationControllers.View viewRole;
    @Qualifier("managementOfUserView")
    @Autowired
    private ConfigurationControllers.View viewUser;
    @Qualifier("managementOfPrivilege")
    @Autowired
    private ConfigurationControllers.View viewPrivilege;
    @Qualifier("passwordController")
    @Autowired
    private ConfigurationControllers.View viewChangePassword;

    private ObservableList<User> userObservableList = FXCollections.observableArrayList();
    private ObservableList<String> observableListForDepartmentBox = FXCollections.observableArrayList();
    private ObservableList<String> observableListForRoleBox = FXCollections.observableArrayList();
    private FilteredList<User> userFilteredList;

    private Stage stage;
    private Stage primaryStage;

    @FXML
    public void initialize() {
        //anchorPane.getStyleClass().addAll(Objects.requireNonNull(getClass().getClassLoader().getResource("css/MyStyle.css")).toExternalForm());
    }

    @PostConstruct
    public void init() {
        editUser.setOnMouseClicked(event -> edit());
        addUser.setOnMouseClicked(event -> add());
        deleteUser.setOnMouseClicked(event -> delete());
        changePass.setOnMouseClicked(event -> changePassword());
        logOut.setOnMouseClicked(event -> logout());
        managementOfRoles.setOnMouseClicked(event -> showManagementOfRoleView());
        managementOfAccess.setOnMouseClicked(event -> showManagementOfPrivilegeView());
        departmentBox.setOnAction(event -> searchOfDepartment());
        roleBox.setOnAction(event -> searchOfRole());
        initMainTableAndPostBox();
    }


    public void initMainTableAndPostBox() {
        userObservableList.clear();
        observableListForRoleBox.clear();
        observableListForDepartmentBox.clear();
        List<User> allUsers = userService.getAllUsers();
        Set<String> posts = new HashSet<>();
        posts.add(" ");
        posts.addAll(allUsers.stream().map(User::getDepartment).collect(Collectors.toList()));
        Set<String> roles = new HashSet<>();
        roles.add(" ");
        roles.addAll(allUsers.stream().map(user -> user.getUserRole().toString()).collect(Collectors.toList()));
        observableListForDepartmentBox.addAll(posts);
        observableListForRoleBox.addAll(roles);
        userObservableList.addAll(allUsers);
        fioColumn.setCellValueFactory(param -> param.getValue().FioProperty());
        loginColumn.setCellValueFactory(param -> param.getValue().LoginProperty());
        postColumn.setCellValueFactory(param -> param.getValue().PostProperty());
        departmentColumn.setCellValueFactory(param -> param.getValue().DepartmentProperty());
        enabledColumn.setCellValueFactory(param -> param.getValue().EnabledProperty());
        roleColumn.setCellValueFactory(param -> param.getValue().UserRoleProperty());
        privilegesColumn.setCellValueFactory(param -> param.getValue().PriviligeProperty());
        userFilteredList = new FilteredList<>(userObservableList, e -> true);
        mainTable.setItems(userObservableList);
        departmentBox.setItems(observableListForDepartmentBox);
        roleBox.setItems(observableListForRoleBox);
    }

    private void showManagementOfRoleView() {
        List<UserRole> allRoles = roleService.getAllRoles();
        Window window = null;
        if (viewRole.getView().getScene() != null) {
            window = viewRole.getView().getScene().getWindow();
        }
        Stage newStage = new Stage(StageStyle.UTILITY);
        ManagementRoleController managementRoleController = (ManagementRoleController) viewRole.getController();
        AnchorPane view = (AnchorPane) this.viewRole.getView();
        managementRoleController.setStage(newStage);
        managementRoleController.setUserRoles(allRoles);
        newStage.setScene(window == null ? new Scene(view) : window.getScene());
        newStage.initModality(Modality.WINDOW_MODAL);
        newStage.initOwner(mainTable.getScene().getWindow());
        newStage.centerOnScreen();
        newStage.show();
    }

    private void changePassword() {
        Window window = null;
        if (viewChangePassword.getView().getScene() != null) {
            window = viewChangePassword.getView().getScene().getWindow();
        }
        Stage newStage = new Stage(StageStyle.UTILITY);
        ChangePasswordController changePasswordController = (ChangePasswordController) viewChangePassword.getController();
        AnchorPane view = (AnchorPane) this.viewChangePassword.getView();
        changePasswordController.setStage(newStage);
        newStage.setScene(window == null ? new Scene(view) : window.getScene());
        newStage.initModality(Modality.WINDOW_MODAL);
        newStage.initOwner(mainTable.getScene().getWindow());
        newStage.centerOnScreen();
        newStage.show();
    }

    private void logout() {
        stage.close();
        primaryStage.show();
    }

    private void add() {
        showManagementOfUserView(true);
    }

    private void edit() {
        if (mainTable.getSelectionModel().getSelectedIndex() > -1) {
            showManagementOfUserView(false);
        } else {
            new Alert(Alert.AlertType.ERROR, "Выберите пользователя для изменения").showAndWait();
        }
    }

    private void showManagementOfUserView(boolean isAdd) {
        List<UserRole> allRoles = roleService.getAllRoles();
        Window window = null;
        if (viewUser.getView().getScene() != null) {
            window = viewUser.getView().getScene().getWindow();
        }
        Stage newStage = new Stage(StageStyle.UTILITY);
        ManagementUserController managementUserController = (ManagementUserController) viewUser.getController();
        AnchorPane view = (AnchorPane) this.viewUser.getView();
        managementUserController.setStage(newStage);
        managementUserController.setUserRoleList(allRoles);
        managementUserController.setAdd(isAdd);
        if (!isAdd) managementUserController.setEditUser(mainTable.getSelectionModel().getSelectedItem());
        newStage.setScene(window == null ? new Scene(view) : window.getScene());
        newStage.initModality(Modality.WINDOW_MODAL);
        newStage.initOwner(mainTable.getScene().getWindow());
        newStage.centerOnScreen();
        newStage.show();
    }

    private void delete() {
        Alert alertApproval = new Alert(Alert.AlertType.WARNING, "Вы точно хотите удалить пользователя?");
        alertApproval.setTitle("WARNING!");
        alertApproval.setHeaderText(null);
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        alertApproval.getButtonTypes().addAll(buttonTypeCancel);
        Optional<ButtonType> result = alertApproval.showAndWait();
        if (result.get() == ButtonType.OK) {
            int selectedIndex = mainTable.getSelectionModel().getSelectedIndex();
            if (selectedIndex < 0) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Выберите пользователя для удаления");
                alert.showAndWait();
                return;
            }
            userService.deleteUser(mainTable.getSelectionModel().getSelectedItem().getFio());
            mainTable.getItems().remove(mainTable.getSelectionModel().getSelectedItem());
        }
        initMainTableAndPostBox();
    }

    private void showManagementOfPrivilegeView() {
        List<UserRole> allRoles = roleService.getAllRoles();
        List<Privilege> allPrivileges = privilegeService.allPrivilege();
        Window window = null;
        if (viewPrivilege.getView().getScene() != null) {
            window = viewPrivilege.getView().getScene().getWindow();
        }
        Stage newStage = new Stage(StageStyle.UTILITY);
        ManagementPrivilegeController managementPrivilegeController = (ManagementPrivilegeController) viewPrivilege.getController();
        AnchorPane view = (AnchorPane) this.viewPrivilege.getView();
        managementPrivilegeController.setStage(newStage);
        managementPrivilegeController.setUserRoleList(allRoles);
        managementPrivilegeController.setPrivilegeSet(allPrivileges);
        newStage.setScene(window == null ? new Scene(view) : window.getScene());
        newStage.initModality(Modality.WINDOW_MODAL);
        newStage.initOwner(mainTable.getScene().getWindow());
        newStage.centerOnScreen();
        newStage.show();
    }

    private void searchOfDepartment() {
        if (departmentBox.getValue().equals(" ")) {
            mainTable.setItems(userObservableList);
        } else if (departmentBox.getSelectionModel().getSelectedIndex() > -1) {
            userFilteredList.setPredicate(user -> user.getDepartment().equals(departmentBox.getValue()));
            mainTable.setItems(userFilteredList);
        }
    }


    private void searchOfRole() {
        if (roleBox.getValue().equals(" ")) {
            mainTable.setItems(userObservableList);
        } else if (roleBox.getSelectionModel().getSelectedIndex() > -1) {
            userFilteredList.setPredicate(user -> user.getUserRole().toString().equals(roleBox.getValue()));
            mainTable.setItems(userFilteredList);
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }
}
