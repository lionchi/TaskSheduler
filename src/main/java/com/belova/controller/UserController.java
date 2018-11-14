package com.belova.controller;

import com.belova.common.UserSession;
import com.belova.controller.configuration.ConfigurationControllers;
import com.belova.entity.Task;
import com.belova.entity.enums.Status;
import com.belova.service.TasksServiceImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
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
import java.util.stream.Collectors;

public class UserController {
    public Label changePass;
    public Label logOut;
    public Label editStatus;

    public TableView<Task> mainTable;
    public TableColumn<Task, String> nameColumn;
    public TableColumn<Task, String> deadlineColumn;
    public TableColumn<Task, String> complexityColumn;
    public TableColumn<Task, String> statusColumn;
    public TableColumn<Task, String> typeColumn;
    public TableColumn<Task, String> quicklyColumn;

    public ComboBox<String> statusBox;

    @Autowired
    private TasksServiceImpl tasksService;
    @Autowired
    private UserSession userSession;
    @Qualifier("passwordController")
    @Autowired
    private ConfigurationControllers.View viewChangePassword;
    @Qualifier("statusView")
    @Autowired
    private ConfigurationControllers.View viewChangeStatus;

    private Stage stage;
    private Stage primaryStage;

    private ObservableList<Task> observableListForTable = FXCollections.observableArrayList();
    private ObservableList<String> observableListForStatus = FXCollections.observableArrayList();
    private FilteredList<Task> taskFilteredList;

    @FXML
    public void initialize() {

    }

    @PostConstruct
    public void init() {
        statusBox.setOnAction(event -> searchOfStatus());
        editStatus.setOnMouseClicked(event -> edit());
        logOut.setOnMouseClicked(event -> logout());
        changePass.setOnMouseClicked(event -> changePassword());
    }

    private void edit() {
        if (mainTable.getSelectionModel().getSelectedIndex() > -1) {
            Window window = null;
            if (viewChangeStatus.getView().getScene() != null) {
                window = viewChangeStatus.getView().getScene().getWindow();
            }
            Stage newStage = new Stage(StageStyle.UTILITY);
            ChangeStatusController changeStatusController = (ChangeStatusController) viewChangeStatus.getController();
            AnchorPane view = (AnchorPane) this.viewChangeStatus.getView();
            changeStatusController.setStage(newStage);
            changeStatusController.setCurrentTask(mainTable.getSelectionModel().getSelectedItem());
            newStage.setScene(window == null ? new Scene(view) : window.getScene());
            newStage.initModality(Modality.WINDOW_MODAL);
            newStage.initOwner(mainTable.getScene().getWindow());
            newStage.centerOnScreen();
            newStage.show();
        } else {
            new Alert(Alert.AlertType.ERROR, "Выберите задачу для изменения у нее статуса").showAndWait();
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
        userSession.closeSession();
        stage.close();
        primaryStage.show();
    }

    public void initMainTable(boolean isInitComboBox) {
        mainTable.setRowFactory(param -> {
            TableRow<Task> row = new TableRow<>();
            row.itemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null && !newValue.isRead()) {
                    row.setStyle("-fx-background-color: #ffd919;");
                }
            });
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getButton() == MouseButton.PRIMARY
                        && event.getClickCount() == 2) {
                    tasksService.changeFlagRead(row.getItem().getId());
                    new Alert(Alert.AlertType.INFORMATION, "Задача помечена, как прочитанная").showAndWait();
                }
            });
            return row;
        });


        List<Task> allDepartmentTasks = tasksService.getAllUserTasks(userSession.getId());
        observableListForTable.clear();
        observableListForTable.addAll(allDepartmentTasks);
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
        observableListForStatus.clear();
        List<String> statuses = new ArrayList<>();
        statuses.add(" ");
        List<Status> statusesString = Arrays.asList(Status.values());
        statuses.addAll(statusesString.stream().map(Status::toString).collect(Collectors.toList()));
        observableListForStatus.addAll(statuses);
        statusBox.setItems(observableListForStatus);
    }

    private void searchOfStatus() {
        if (statusBox.getValue() != null) {
            if (statusBox.getValue().equals(" ")) {
                mainTable.setItems(observableListForTable);
            } else if (statusBox.getSelectionModel().getSelectedIndex() > -1) {
                taskFilteredList.setPredicate(task -> task.getStatus().toString().equals(statusBox.getValue()));
                mainTable.setItems(taskFilteredList);
            }
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
}
