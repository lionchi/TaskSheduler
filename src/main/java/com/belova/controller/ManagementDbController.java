package com.belova.controller;

import com.belova.common.BackupData;
import com.belova.common.StorageOfTask;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.concurrent.ScheduledFuture;

public class ManagementDbController {
    public TextField pathDumpField;
    public TextField pathSaveField;

    public Button explorerFile;
    public Button explorerDir;

    public MenuItem startItem;
    public MenuItem stopItem;

    @Value("${spring.datasource.username}")
    private String user;
    @Value("${spring.datasource.password}")
    private String password;
    @Value("${db.host}")
    private String host;
    @Value("${db.port}")
    private String port;
    @Value("${db.name}")
    private String database;

    @Autowired
    private ThreadPoolTaskScheduler taskScheduler;
    @Autowired
    private CronTrigger cronTrigger;
    @Autowired
    private StorageOfTask storageOfTask;

    private Stage stage;

    @FXML
    public void initialize() {

    }

    @PostConstruct
    public void init() {
        explorerDir.setOnAction(event -> showExplorerDir());
        explorerFile.setOnAction(event -> showExplorerFile());
        startItem.setOnAction(event -> start());
        stopItem.setOnAction(event -> stop());
    }

    private void start() {
        File file = new File("configuration.txt");
        String config = pathDumpField.getText() + "," + pathSaveField.getText();
        try (PrintWriter printWriter = new PrintWriter(file)) {
            printWriter.write(config);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ScheduledFuture<?> schedule = taskScheduler.schedule(new BackupData(pathDumpField.getText(), pathSaveField.getText() + "\\",
                user, password, host, port, database), cronTrigger);
        storageOfTask.put(BackupData.class, schedule);
        startItem.setDisable(true);
        stopItem.setDisable(false);
        new Alert(Alert.AlertType.INFORMATION, "Задача резервирования успешна запущена").showAndWait();
    }

    private void stop() {
        ScheduledFuture<?> value = storageOfTask.getValue(BackupData.class);
        if (!value.isCancelled()) {
            value.cancel(true);
        }
        storageOfTask.remove(BackupData.class);
        startItem.setDisable(false);
        stopItem.setDisable(true);
        new Alert(Alert.AlertType.INFORMATION, "Задача резервирования успешна остановлена").showAndWait();
    }

    private void showExplorerDir() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        try {
            File dir = directoryChooser.showDialog(stage);
            pathSaveField.setText(dir.getAbsolutePath());
        } catch (NullPointerException e) {
            new Alert(Alert.AlertType.ERROR, "Выберите директорию для сохранения резервных копий базы данных").showAndWait();
        }
    }

    private void showExplorerFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выберите mysqldump.exe");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("EXE", "*.exe"));
        try {
            File file = fileChooser.showOpenDialog(stage);
            pathDumpField.setText(file.getAbsolutePath());
        } catch (NullPointerException e) {
            new Alert(Alert.AlertType.ERROR, "Выберите mysqldump.exe").showAndWait();
        }

    }

    public void setStage(Stage stage) {
        this.stage = stage;
        if (storageOfTask.contains(BackupData.class)) {
            startItem.setDisable(true);
            stopItem.setDisable(false);
        } else {
            startItem.setDisable(false);
            stopItem.setDisable(true);
        }
    }

    public void setPathDumpField(String pathDump) {
        this.pathDumpField.setText(pathDump);
    }

    public void setPathSaveField(String pathSave) {
        this.pathSaveField.setText(pathSave);
    }
}
