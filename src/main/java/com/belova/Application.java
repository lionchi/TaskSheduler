package com.belova;

import com.belova.common.ofSpring.AbstractJavaFxApplicationSupport;
import com.belova.common.ofSpring.ThreadPoolTaskSchedulerConfig;
import com.belova.common.ofSpring.ConfigurationControllers;
import com.belova.controller.MainController;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;

import java.net.URL;

@Lazy
@SpringBootApplication(scanBasePackages = "")
@Import({ConfigurationControllers.class, ThreadPoolTaskSchedulerConfig.class})
public class Application extends AbstractJavaFxApplicationSupport {

    @Value("Авторизация")
    private String windowTitle;

    @Qualifier("mainView")
    @Autowired
    private ConfigurationControllers.View view;

    @Override
    public void start(Stage stage) throws Exception {
        MainController mainController = (MainController) view.getController();
        AnchorPane view = (AnchorPane) this.view.getView();
        mainController.setStage(stage);
        URL url = Application.class.getClassLoader().getResource("img/organize.png");
        stage.getIcons().addAll(new Image(url.toString()));
        stage.setTitle(windowTitle);
        stage.setScene(new Scene(view));
        stage.setResizable(false);
        stage.centerOnScreen();
        stage.show();
    }

    public static void main(String[] args) {
        // Для работы Javafx с awt
        System.setProperty("javafx.macosx.embedded", "true");
        java.awt.Toolkit.getDefaultToolkit();

        launchApp(Application.class, args);
    }

}
