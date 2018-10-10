package com.belova.controller;

import com.belova.common.MyUserDetailsService;
import com.belova.common.UserSession;
import com.belova.entity.User;
import com.belova.service.UserServiceImpl;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import java.net.URL;

public class MainController {

    @Autowired
    private EntityManager entityManager;
    @Qualifier("taskView")
    @Autowired
    private ConfigurationControllers.View view;
    @Autowired
    private AuthenticationManager authManager;
    @Autowired
    private UserSession userSession;
    @Autowired
    private UserServiceImpl userService;

    private Stage stage;

    public TextField loginField;
    public TextField passwordField;
    public Label changePassword;
    public ImageView imageView;
    public Button logIn;

    @FXML
    public void initialize() {

    }

    @PostConstruct
    public void init() {
        setImage();
        logIn.setOnAction(event -> login());
    }

    void login() {
        try {
            final String username = loginField.getText().trim();
            final String password = passwordField.getText().trim();

            Authentication request = new UsernamePasswordAuthenticationToken(username, password);
            Authentication result = authManager.authenticate(request);
            SecurityContextHolder.getContext().setAuthentication(result);

            String name = result.getName();
            String roles = result.getAuthorities().toString();

            User currentUser = userService.findUserByLogin(username);

            userSession.setFio(currentUser.getFio());
            userSession.setLogin(username);
            userSession.setRoles(roles);
            userSession.setId(currentUser.getId());

            TaskController taskController = (TaskController) view.getController();
            AnchorPane view = (AnchorPane) this.view.getView();
            stage.setTitle("Lol");
            stage.setScene(new Scene(view));
            stage.setResizable(true);
            stage.centerOnScreen();
            stage.show();
        } catch (AuthenticationException e) {
            Alert alertApproval = new Alert(Alert.AlertType.ERROR, "Отказано в доступе. Проверьте свои данные");
            alertApproval.setTitle("Error!");
            alertApproval.setHeaderText(null);
            alertApproval.showAndWait();
            System.out.println(e.getMessage());
        }
    }

    private void setImage() {
        URL url = getClass().getClassLoader().getResource("img/organize.png");
        imageView.setImage(new Image(url.toString()));
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
