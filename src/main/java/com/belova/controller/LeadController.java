package com.belova.controller;

import com.belova.common.UserSession;
import com.belova.controller.configuration.ConfigurationControllers;
import com.belova.entity.Task;
import com.belova.entity.User;
import com.belova.entity.enums.Status;
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

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class LeadController {
    public Label editTask;
    public Label deleteTask;
    public Label statistics;
    public Label changePass;
    public Label logOut;
    public Label addTask;

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
    private UserSession userSession;
    @Qualifier("passwordController")
    @Autowired
    private ConfigurationControllers.View viewChangePassword;
    @Qualifier("managementOfTasks")
    @Autowired
    private ConfigurationControllers.View viewTasks;

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
        changePass.setOnMouseClicked(event -> changePassword());
        logOut.setOnMouseClicked(event -> logout());
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

    public void initMainTable(boolean isInitComboBox) {
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
            tasksService.deleteTask(mainTable.getSelectionModel().getSelectedItem());
            mainTable.getItems().remove(mainTable.getSelectionModel().getSelectedItem());
        }
        initMainTable(false);
    }

    private void searchOfUser() {
        if (fioBox.getValue().equals(" ")) {
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

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    public void setStage(Stage newStage) {
        this.stage = newStage;
    }
}
