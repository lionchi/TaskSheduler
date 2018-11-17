package com.belova.controller;

import com.belova.common.Notification;
import com.belova.common.StatisticsHelper;
import com.belova.common.StorageOfTask;
import com.belova.common.UserSession;
import com.belova.controller.configuration.ConfigurationControllers;
import com.belova.entity.Task;
import com.belova.entity.User;
import com.belova.entity.UserRole;
import com.belova.entity.enums.Status;
import com.belova.service.RoleServiceImpl;
import com.belova.service.TasksServiceImpl;
import com.belova.service.UserServiceImpl;
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
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;

public class LeadController {
    public Label editTask;
    public Label deleteTask;
    public Label statistics;
    public Label changePass;
    public Label logOut;
    public Label addTask;
    public Label addUserInDepartmentLabel;
    public Label changeUserLabel;
    public Label deleteUserInDepartmentLabel;

    public TableView<Task> mainTable;
    public TableColumn<Task, String> fioColumn;
    public TableColumn<Task, String> nameColumn;
    public TableColumn<Task, String> deadlineColumn;
    public TableColumn<Task, String> complexityColumn;
    public TableColumn<Task, String> statusColumn;
    public TableColumn<Task, String> typeColumn;
    public TableColumn<Task, String> quicklyColumn;

    public ComboBox<String> fioBox;
    public ComboBox<String> statusBox;

    @Autowired
    private TasksServiceImpl tasksService;
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private RoleServiceImpl roleService;
    @Autowired
    private UserSession userSession;
    @Qualifier("passwordController")
    @Autowired
    private ConfigurationControllers.View viewChangePassword;
    @Qualifier("managementOfTasks")
    @Autowired
    private ConfigurationControllers.View viewTasks;
    @Qualifier("managementOfUserView")
    @Autowired
    private ConfigurationControllers.View viewUser;
    @Autowired
    private DataSource dataSource;
    @Autowired
    private ThreadPoolTaskScheduler taskScheduler;
    @Autowired
    private CronTrigger cronTriggerToNotification;
    @Autowired
    private Notification notification;
    @Autowired
    private StorageOfTask storageOfTask;

    @Value(value = "classpath:reports/Statistics.xls")
    private Resource resource;

    private Stage stage;
    private Stage primaryStage;

    private ObservableList<Task> observableListForTable = FXCollections.observableArrayList();
    private ObservableList<String> observableListForUser = FXCollections.observableArrayList();
    private ObservableList<String> observableListForStatus = FXCollections.observableArrayList();
    private FilteredList<Task> taskFilteredList;


    @FXML
    public void initialize() {

    }

    @PostConstruct
    public void init() {
        fioBox.setOnAction(event -> searchOfUser());
        statusBox.setOnAction(event -> searchOfStatus());
        addTask.setOnMouseClicked(event -> add());
        editTask.setOnMouseClicked(event -> edit());
        deleteTask.setOnMouseClicked(event -> delete());
        statistics.setOnMouseClicked(event -> generateStatistics());
        changePass.setOnMouseClicked(event -> changePassword());
        logOut.setOnMouseClicked(event -> logout());
        addUserInDepartmentLabel.setOnMouseClicked(event -> addUserInDepartment());
        changeUserLabel.setOnMouseClicked(event -> changeUser());
        deleteUserInDepartmentLabel.setOnMouseClicked(event -> deleteUserInDepartment());
    }

    private void add() {
        showManagementOfTasks(true);
    }

    private void edit() {
        if (mainTable.getSelectionModel().getSelectedIndex() > -1) {
            showManagementOfTasks(false);
        } else {
            new Alert(Alert.AlertType.ERROR, "Выберите задачу для изменения").showAndWait();
        }
    }


    private void showManagementOfTasks(boolean isAdd) {
        Window window = null;
        if (viewTasks.getView().getScene() != null) {
            window = viewTasks.getView().getScene().getWindow();
        }
        Stage newStage = new Stage(StageStyle.UTILITY);
        ManagementTasksController managementTasksController = (ManagementTasksController) viewTasks.getController();
        AnchorPane view = (AnchorPane) this.viewTasks.getView();
        managementTasksController.setStage(newStage);
        managementTasksController.setAdd(isAdd);
        if (!isAdd) managementTasksController.setEditTask(mainTable.getSelectionModel().getSelectedItem());
        newStage.setScene(window == null ? new Scene(view) : window.getScene());
        newStage.initModality(Modality.WINDOW_MODAL);
        newStage.initOwner(mainTable.getScene().getWindow());
        newStage.centerOnScreen();
        newStage.show();
    }

    private void changeUser() {
        if (fioBox.getSelectionModel().getSelectedItem() != null) {
            if (fioBox.getSelectionModel().getSelectedItem().equals(userSession.getFio())) {
                new Alert(Alert.AlertType.ERROR, "Вы не можете изменять самого себя")
                        .showAndWait();
            } else if (fioBox.getSelectionModel().getSelectedItem() != null) {
                showManagementOfUserView(false);
            }
        } else {
            new Alert(Alert.AlertType.ERROR, "Выберите пользователя в поле 'ФИО сотрудника' для изменения")
                    .showAndWait();
        }
    }

    private void addUserInDepartment() {
        showManagementOfUserView(true);
    }

    private void showManagementOfUserView(boolean isAdd) {
        List<UserRole> rolesWithoutRoleAdmin = roleService.getAllRoles()
                .stream()
                .filter(userRole -> !userRole.getRolename().toLowerCase().equals("admin") &&
                        !userRole.getRolename().toLowerCase().equals("lead"))
                .collect(Collectors.toList());
        Window window = null;
        if (viewUser.getView().getScene() != null) {
            window = viewUser.getView().getScene().getWindow();
        }
        Stage newStage = new Stage(StageStyle.UTILITY);
        ManagementUserController managementUserController = (ManagementUserController) viewUser.getController();
        AnchorPane view = (AnchorPane) this.viewUser.getView();
        managementUserController.setStage(newStage);
        managementUserController.setUserRoleList(rolesWithoutRoleAdmin);
        managementUserController.setAdd(isAdd);
        if (isAdd) {
            managementUserController.setLead(true, userService.getDepartmentCurrentUser(userSession.getId()));
        } else {
            User selectUser = userService.findUserByFio(fioBox.getSelectionModel().getSelectedItem());
            managementUserController.setLead(true, null);
            managementUserController.setEditUser(selectUser);
        }
        newStage.setScene(window == null ? new Scene(view) : window.getScene());
        newStage.initModality(Modality.WINDOW_MODAL);
        newStage.initOwner(mainTable.getScene().getWindow());
        newStage.centerOnScreen();
        newStage.show();
    }

    private void deleteUserInDepartment() {
        Alert alertApproval = new Alert(Alert.AlertType.WARNING, "Вы точно хотите удалить пользователя?");
        alertApproval.setTitle("WARNING!");
        alertApproval.setHeaderText(null);
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        alertApproval.getButtonTypes().addAll(buttonTypeCancel);
        Optional<ButtonType> result = alertApproval.showAndWait();
        if (result.get() == ButtonType.OK) {
            if (fioBox.getSelectionModel().getSelectedItem() == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Выберите пользователя для удаления");
                alert.showAndWait();
                return;
            }
            userService.deleteUser(fioBox.getSelectionModel().getSelectedItem());
            mainTable.getItems().remove(mainTable.getSelectionModel().getSelectedItem());
        }
        initMainTable(true, false);
    }

    public void initMainTable(boolean isInitComboBox, boolean startThreadToNotification) {
        List<Task> allDepartmentTasks = tasksService.getAllDepartmentTasks(userSession.getId());
        observableListForTable.clear();
        observableListForTable.addAll(allDepartmentTasks);
        fioColumn.setCellValueFactory(param -> param.getValue().UserProperty());
        nameColumn.setCellValueFactory(param -> param.getValue().NameProperty());
        deadlineColumn.setCellValueFactory(param -> param.getValue().DeadlineProperty());
        complexityColumn.setCellValueFactory(param -> param.getValue().ComplexityProperty());
        statusColumn.setCellValueFactory(param -> param.getValue().StatusPProperty());
        typeColumn.setCellValueFactory(param -> param.getValue().TypeProperty());
        quicklyColumn.setCellValueFactory(param -> param.getValue().QuicklyProperty());
        taskFilteredList = new FilteredList<>(observableListForTable, e -> true);
        mainTable.setItems(observableListForTable);
        if (isInitComboBox) initComboBox();
        if (startThreadToNotification) {
            ScheduledFuture<?> schedule = taskScheduler.schedule(notification, cronTriggerToNotification);
            storageOfTask.put(Notification.class, schedule);
        }
    }

    private void initComboBox() {
        observableListForUser.clear();
        observableListForStatus.clear();
        List<String> statuses = new ArrayList<>();
        statuses.add(" ");
        List<Status> statusesString = Arrays.asList(Status.values());
        statuses.addAll(statusesString.stream().map(Status::toString).collect(Collectors.toList()));
        observableListForStatus.addAll(statuses);
        statusBox.setItems(observableListForStatus);
        List<String> allDepartmentUsers = new ArrayList<>();
        allDepartmentUsers.add(" ");
        List<String> stringsFio = userService.getAllDepartmentUsers(userSession.getId()).stream().map(User::toString).collect(Collectors.toList());
        allDepartmentUsers.addAll(stringsFio);
        observableListForUser.addAll(allDepartmentUsers);
        fioBox.setItems(observableListForUser);
    }

    private void delete() {
        Alert alertApproval = new Alert(Alert.AlertType.WARNING, "Вы точно хотите удалить задачу?");
        alertApproval.setTitle("WARNING!");
        alertApproval.setHeaderText(null);
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        alertApproval.getButtonTypes().addAll(buttonTypeCancel);
        Optional<ButtonType> result = alertApproval.showAndWait();
        if (result.get() == ButtonType.OK) {
            int selectedIndex = mainTable.getSelectionModel().getSelectedIndex();
            if (selectedIndex < 0) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Выберите задачу для удаления");
                alert.showAndWait();
                return;
            }
            User removingOfUser = userService.findUserByLogin(userSession.getLogin());
            try {
                tasksService.deleteTask(mainTable.getSelectionModel().getSelectedItem(), removingOfUser);
            } catch (org.springframework.security.access.AccessDeniedException accessDeniedException) {
                new Alert(Alert.AlertType.ERROR, "У вас недостаточно прав доступа").showAndWait();
            }
            mainTable.getItems().remove(mainTable.getSelectionModel().getSelectedItem());
        }
        initMainTable(false, false);
    }

    private void searchOfUser() {
        if (fioBox.getValue() == null || fioBox.getValue().equals(" ")) {
            mainTable.setItems(observableListForTable);
        } else if (fioBox.getSelectionModel().getSelectedIndex() > -1) {
            taskFilteredList.setPredicate(task -> task.getUser().toString().equals(fioBox.getValue()));
            mainTable.setItems(taskFilteredList);
        }
    }

    private void searchOfStatus() {
        if (statusBox.getValue().equals(" ")) {
            mainTable.setItems(observableListForTable);
        } else if (statusBox.getSelectionModel().getSelectedIndex() > -1) {
            taskFilteredList.setPredicate(task -> task.getStatus().toString().equals(statusBox.getValue()));
            mainTable.setItems(taskFilteredList);
        }
    }

    private void generateStatistics() {
        try {
            User userByLogin = userService.findUserByLogin(userSession.getLogin());
            String templateUrl = resource.getURL().toString().replace("file:/", "");
            StatisticsHelper.generateStatisticForLeader(dataSource, userByLogin.getDepartment(), templateUrl);
            new Alert(Alert.AlertType.INFORMATION, "Статистика сохранена в документы").showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        storageOfTask.clearAllOfClass(Notification.class);
        userSession.closeSession();
        stage.close();
        primaryStage.show();
    }

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    public void setStage(Stage newStage) {
        this.stage = newStage;
    }
}
