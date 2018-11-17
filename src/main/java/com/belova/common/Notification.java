package com.belova.common;

import com.belova.Application;
import com.belova.entity.Task;
import com.belova.service.TasksServiceImpl;
import javafx.application.Platform;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tray.notification.NotificationType;
import tray.notification.TrayNotification;

@Component
public class Notification implements Runnable {
    @Autowired
    private UserSession userSession;
    @Autowired
    private TasksServiceImpl tasksService;

    @Override
    public void run() {
        Task newTask = tasksService.findNewTask(userSession.getId());
        if (newTask != null) {
            Platform.runLater(() -> {
                TrayNotification trayNotification = new TrayNotification();
                trayNotification.setTitle("Новая задача: " + newTask.getName());
                trayNotification.setMessage(String.format("Статус: %s. Срочно: %s",
                        newTask.getStatus().getValue(), newTask.getQuickly() ? "Да" : "Нет"));
                trayNotification.setNotificationType(NotificationType.INFORMATION);
                trayNotification.setOnShown(event -> {
                    Media hit = new Media(Application.class.getResource("/sounds/notification_sound.mp3").toString());
                    MediaPlayer mediaPlayer = new MediaPlayer(hit);
                    mediaPlayer.play();
                });
                trayNotification.showAndWait();
            });
        }
    }
}
