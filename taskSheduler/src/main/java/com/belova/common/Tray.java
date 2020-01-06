package com.belova.common;

import javafx.application.Platform;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.net.URL;

public class Tray {
    private static boolean flag = false;
    private static TrayIcon trayIcon;

    public static void addAppToTray(Stage stage) {
        try {
            if (!SystemTray.isSupported()) {
                System.out.println("Система не поддерживает системный tray :(");
                return;
            }
            if (!flag) {
                SystemTray tray = SystemTray.getSystemTray();
                URL imageLoc = Tray.class.getClassLoader().getResource("img/organize_min.png");
                Image image = ImageIO.read(imageLoc);
                trayIcon = new TrayIcon(image);
                trayIcon.addActionListener(event -> Platform.runLater(() -> {
                    stage.show();
                    stage.toFront();
                }));

                MenuItem openItem = new MenuItem("Раскрыть приложение");
                openItem.addActionListener(event -> Platform.runLater(() -> {
                    stage.show();
                    stage.toFront();
                }));

                Font defaultFont = Font.decode(null);
                Font boldFont = defaultFont.deriveFont(Font.BOLD);
                openItem.setFont(boldFont);

                MenuItem exitItem = new MenuItem("Выход");
                exitItem.addActionListener(event -> {
                    Platform.exit();
                    tray.remove(trayIcon);
                });

                final PopupMenu popup = new PopupMenu();
                popup.add(openItem);
                popup.addSeparator();
                popup.add(exitItem);
                trayIcon.setPopupMenu(popup);

                tray.add(trayIcon);
                flag = true;
            }
        } catch (java.awt.AWTException | IOException e) {
            System.out.println("Невозможно запустить системный  tray");
            e.printStackTrace();
        }
    }

    public static void deleteTrayIcon() {
        if (!SystemTray.isSupported()) {
            System.out.println("Система не поддерживает системный tray");
            return;
        }
        SystemTray tray = SystemTray.getSystemTray();
        tray.remove(trayIcon);
        flag = false;
    }
}
