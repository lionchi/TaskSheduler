package com.belova;

import com.belova.common.AbstractJavaFxApplicationSupport;
import com.belova.common.ThreadPoolTaskSchedulerConfig;
import com.belova.controller.configuration.ConfigurationControllers;
import com.belova.controller.MainController;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;

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
        stage.setTitle(windowTitle);
        stage.setScene(new Scene(view));
        stage.setResizable(true);
        stage.centerOnScreen();
        stage.show();
    }

    public static void main(String[] args) {
        launchApp(Application.class, args);
    }

}
