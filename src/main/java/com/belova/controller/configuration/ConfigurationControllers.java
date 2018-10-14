package com.belova.controller.configuration;

import com.belova.controller.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class ConfigurationControllers {

    @Bean(name = "mainView")
    public View getMainView() throws IOException {
        return loadView("ui/main.fxml");
    }

    @Bean(name = "taskView")
    public View getTaskView() throws IOException {
        return loadView("ui/task_form.fxml");
    }

    @Bean(name = "adminView")
    public View getAdminView() throws IOException {
        return loadView("ui/admin.fxml");
    }

    @Bean(name = "managementOfRoleView")
    public View getRoleView() throws IOException {
        return loadView("ui/management_role_form.fxml");
    }

    @Bean(name = "managementOfUserView")
    public View getUserView() throws IOException {
        return loadView("ui/management_users_form.fxml");
    }

    @Bean(name = "managementOfPrivilege")
    public View getPrivilegeView() throws IOException {
        return loadView("ui/management_privileges.fxml");
    }

    @Bean(name = "passwordController")
    public View getChangePasswordView() throws IOException {
        return loadView("ui/change_password.fxml");
    }

    @Bean(name = "leadController")
    public View getLeadView() throws IOException {
        return loadView("ui/lead_form.fxml");
    }

    @Bean(name = "managementOfTasks")
    public View getTasksView() throws IOException {
        return loadView("ui/management_tasks_form.fxml");
    }

    @Bean
    public MainController getMainController() throws IOException {
        return (MainController) getMainView().getController();
    }

    @Bean
    public TaskController getTaskController() throws IOException {
        return (TaskController) getTaskView().getController();
    }

    @Bean
    public AdminController getAdminController() throws IOException {
        return (AdminController) getAdminView().getController();
    }

    @Bean
    public ManagementRoleController getManagementRoleController() throws IOException {
        return (ManagementRoleController) getRoleView().getController();
    }

    @Bean
    public ManagementUserController getManagementUserController() throws IOException {
        return (ManagementUserController) getUserView().getController();
    }

    @Bean
    public ManagementPrivilegeController getManagementPrivilegeController() throws IOException {
        return (ManagementPrivilegeController) getPrivilegeView().getController();
    }

    @Bean
    public ChangePasswordController getChangePasswordController() throws IOException {
        return (ChangePasswordController) getChangePasswordView().getController();
    }

    @Bean
    public LeadController getLeadController() throws IOException {
        return (LeadController) getLeadView().getController();
    }

    @Bean
    public ManagementTasksController getManagementTasksController() throws IOException {
        return (ManagementTasksController) getTasksView().getController();
    }

    protected View loadView(String url) throws IOException {
        InputStream fxmlStream = null;
        try {
            fxmlStream = getClass().getClassLoader().getResourceAsStream(url);
            FXMLLoader loader = new FXMLLoader();
            loader.load(fxmlStream);
            return new View(loader.getRoot(), loader.getController());
        } finally {
            if (fxmlStream != null) {
                fxmlStream.close();
            }
        }
    }

    public class View {
        private Parent view;
        private Object controller;

        public View(Parent view, Object controller) {
            this.view = view;
            this.controller = controller;
        }

        public Parent getView() {
            return view;
        }

        public void setView(Parent view) {
            this.view = view;
        }

        public Object getController() {
            return controller;
        }

        public void setController(Object controller) {
            this.controller = controller;
        }
    }

}
