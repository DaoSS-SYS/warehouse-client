package com.example.warehouse.warehouseclient;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {
    @Override
    public void start(Stage stage) throws Exception {

        // 1) логин-окно
        FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("login-view.fxml"));
        Scene loginScene = new Scene(loginLoader.load());
        Stage loginStage = new Stage();
        loginStage.setTitle("Авторизация");
        loginStage.setScene(loginScene);
        loginStage.initModality(Modality.APPLICATION_MODAL);
        loginStage.setResizable(false);
        loginStage.showAndWait();

        var loginController = (com.example.warehouse.warehouseclient.Auth.LoginController) loginLoader.getController();
        var role = loginController.getRole();
        if (role == null) {
            // пользователь закрыл окно — выходим
            Platform.exit();
            return;
        }

        // 2) основное окно
        FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
        Scene mainScene = new Scene(mainLoader.load());
        var mainController = (com.example.warehouse.warehouseclient.MainController) mainLoader.getController();
        mainController.setRole(role); // <-- важно

        stage.setTitle("Warehouse Client");
        stage.setScene(mainScene);
        stage.show();
    }

}
