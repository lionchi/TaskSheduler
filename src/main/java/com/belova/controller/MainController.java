package com.belova.controller;

import com.belova.common.UserSession;
import com.belova.controller.configuration.ConfigurationControllers;
import com.belova.entity.User;
import com.belova.service.UsbKeyServiceImpl;
import com.belova.service.UserServiceImpl;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import java.io.File;
import java.net.URL;

public class MainController {

    @Autowired
    private EntityManager entityManager;
    @Qualifier("adminView")
    @Autowired
    private ConfigurationControllers.View viewAdmin;
    @Qualifier("leadController")
    @Autowired
    private ConfigurationControllers.View viewLead;
    @Qualifier("userView")
    @Autowired
    private ConfigurationControllers.View viewUser;
    @Autowired
    private AuthenticationManager authManager;
    @Autowired
    private UserSession userSession;
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private UsbKeyServiceImpl usbKeyService;

    private Stage stage;

    public TextField loginField;
    public PasswordField passwordField;
    public ImageView imageView;
    public Button logIn;

    @FXML
    public void initialize() {

    }

    @PostConstruct
    public void init() {
        setImage();
        logIn.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ENTER) {
                login();
                event.consume();
            }
        });
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

            showWindowsOfUserOrAdmin();
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

    private void showWindowsOfUserOrAdmin() {
        if (userSession.getRoles().contains("ROLE_ADMIN")) {
            if (!checkUsbKey()) {
                new Alert(Alert.AlertType.ERROR, "Вы указали неверный usb ключ").showAndWait();
                return;
            }
            Window window = null;
            if (viewAdmin.getView().getScene() != null) {
                window = viewAdmin.getView().getScene().getWindow();
            }
            Stage newStage = new Stage();
            AdminController adminController = (AdminController) viewAdmin.getController();
            AnchorPane view = (AnchorPane) this.viewAdmin.getView();
            adminController.setPrimaryStage(stage);
            adminController.setStage(newStage);
            newStage.setTitle("Администрирование");
            newStage.setScene(window == null ? new Scene(view) : window.getScene());
            newStage.setResizable(true);
            newStage.centerOnScreen();
            newStage.show();
            stage.hide();
        } else if (userSession.getRoles().contains("ROLE_LEAD")) {
            Window window = null;
            if (viewLead.getView().getScene() != null) {
                window = viewLead.getView().getScene().getWindow();
            }
            Stage newStage = new Stage();
            LeadController leadController = (LeadController) viewLead.getController();
            AnchorPane view = (AnchorPane) this.viewLead.getView();
            leadController.setPrimaryStage(stage);
            leadController.setStage(newStage);
            leadController.initMainTable(true, true);
            newStage.setTitle("Руководитель");
            newStage.setScene(window == null ? new Scene(view) : window.getScene());
            newStage.setResizable(true);
            newStage.centerOnScreen();
            newStage.show();
            stage.hide();
        } else if (userSession.getRoles().contains("ROLE_USER")) {
            Window window = null;
            if (viewUser.getView().getScene() != null) {
                window = viewUser.getView().getScene().getWindow();
            }
            Stage newStage = new Stage();
            UserController userController = (UserController) viewUser.getController();
            AnchorPane view = (AnchorPane) this.viewUser.getView();
            userController.setPrimaryStage(stage);
            userController.setStage(newStage);
            userController.initMainTable(true, true);
            newStage.setTitle("Руководитель");
            newStage.setScene(window == null ? new Scene(view) : window.getScene());
            newStage.setResizable(true);
            newStage.centerOnScreen();
            newStage.show();
            stage.hide();
        }
        loginField.clear();
        passwordField.clear();
    }

    private boolean checkUsbKey() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Выберите UsbKey");
        try {
            File dir = directoryChooser.showDialog(stage);
            return usbKeyService.checkKey(dir, userSession.getLogin());
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
