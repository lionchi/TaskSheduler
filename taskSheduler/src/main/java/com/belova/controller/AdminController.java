package com.belova.controller;

import com.belova.common.Tray;
import com.belova.common.statistics.StatisticsHelper;
import com.belova.common.supporting.UserSession;
import com.belova.common.ofSpring.ConfigurationControllers;
import com.belova.controller.management.ManagementDbController;
import com.belova.controller.management.ManagementPrivilegeController;
import com.belova.controller.management.ManagementRoleController;
import com.belova.controller.management.ManagementUserController;
import com.belova.entity.Privilege;
import com.belova.entity.User;
import com.belova.entity.UserRole;
import com.belova.service.privilege.PrivilegeServiceImpl;
import com.belova.service.role.RoleServiceImpl;
import com.belova.service.user.UserServiceImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
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
    public Label managementUsbKeys;
    public Label changePass;
    public Label logOut;
    public Label managementDumpBD;

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
    @Autowired
    private DataSource dataSource;
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
    @Qualifier("managementDb")
    @Autowired
    private ConfigurationControllers.View viewDb;
    @Qualifier("usbKeyView")
    @Autowired
    private ConfigurationControllers.View viewUsbKey;


    @Value(value = "classpath:reports/Statistics.xls")
    private Resource resource;

    private ObservableList<User> userObservableList = FXCollections.observableArrayList();
    private ObservableList<String> observableListForDepartmentBox = FXCollections.observableArrayList();
    private ObservableList<String> observableListForRoleBox = FXCollections.observableArrayList();
    private FilteredList<User> userFilteredList;

    private Stage stage;
    private Stage primaryStage;

    @FXML
    public void initialize() {

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
        managementUsbKeys.setOnMouseClicked(event -> showManagementOfUsbView());
        managementDumpBD.setOnMouseClicked(event -> showManagementDumpDbView());
        statistics.setOnMouseClicked(event -> generateStatistics());
        //initMainTableAndPostBox();
    }


    public void initMainTableAndPostBox() {
        if (!anchorPane.getStylesheets().contains("css/MyStyle.css"))
            anchorPane.getStylesheets().add("css/MyStyle.css");
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
        managementRoleController.setStylesheet();
        newStage.setScene(window == null ? new Scene(view) : window.getScene());
        newStage.initModality(Modality.WINDOW_MODAL);
        newStage.initOwner(mainTable.getScene().getWindow());
        newStage.centerOnScreen();
        newStage.show();
    }

    private void showManagementDumpDbView() {
        String pathFile = "", pathDir = "";
        try (Scanner scanner = new Scanner(new File("configuration.txt"))) {
            String[] strings = scanner.nextLine().split(",");
            pathFile = strings[0];
            pathDir = strings[1];
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }

        Window window = null;
        if (viewDb.getView().getScene() != null) {
            window = viewDb.getView().getScene().getWindow();
        }
        Stage newStage = new Stage(StageStyle.UTILITY);
        ManagementDbController managementDbController = (ManagementDbController) viewDb.getController();
        managementDbController.setStylesheet();
        AnchorPane view = (AnchorPane) this.viewDb.getView();
        managementDbController.setStage(newStage);
        managementDbController.setPathDumpField(pathFile);
        managementDbController.setPathSaveField(pathDir);
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
        changePasswordController.setStylesheet();
        AnchorPane view = (AnchorPane) this.viewChangePassword.getView();
        changePasswordController.setStage(newStage);
        newStage.setScene(window == null ? new Scene(view) : window.getScene());
        newStage.initModality(Modality.WINDOW_MODAL);
        newStage.initOwner(mainTable.getScene().getWindow());
        newStage.centerOnScreen();
        newStage.show();
    }

    private void logout() {
        userSession.closeSession();
        Tray.deleteTrayIcon();
        stage.close();
        primaryStage.show();
        primaryStage.toFront();
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
        managementUserController.setStylesheet();
        if (!isAdd) managementUserController.setEditUser(mainTable.getSelectionModel().getSelectedItem());
        else managementUserController.clearField();
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
        managementPrivilegeController.setStylesheet();
        newStage.setScene(window == null ? new Scene(view) : window.getScene());
        newStage.initModality(Modality.WINDOW_MODAL);
        newStage.initOwner(mainTable.getScene().getWindow());
        newStage.centerOnScreen();
        newStage.show();
    }

    private void generateStatistics() {
        try {
            String templateUrl = resource.getURL().toString().replace("file:/", "");
            StatisticsHelper.generateStatisticForAdmin(dataSource, templateUrl);
            new Alert(Alert.AlertType.INFORMATION, "Статистика сохранена в документы").showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    private void showManagementOfUsbView() {
        List<User> userAdminList = userService.getAllUsers()
                .stream()
                .filter(user -> user.getUserRole().getRolename().equals("ADMIN"))
                .collect(Collectors.toList());
        Window window = null;
        if (viewUsbKey.getView().getScene() != null) {
            window = viewUsbKey.getView().getScene().getWindow();
        }
        Stage newStage = new Stage(StageStyle.UTILITY);
        UsbKeyController usbKeyController = (UsbKeyController) viewUsbKey.getController();
        usbKeyController.setStylesheet();
        AnchorPane view = (AnchorPane) this.viewUsbKey.getView();
        usbKeyController.setStage(newStage);
        usbKeyController.setUserObservableList(userAdminList);
        newStage.setScene(window == null ? new Scene(view) : window.getScene());
        newStage.initModality(Modality.WINDOW_MODAL);
        newStage.initOwner(mainTable.getScene().getWindow());
        newStage.centerOnScreen();
        newStage.show();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }
}
