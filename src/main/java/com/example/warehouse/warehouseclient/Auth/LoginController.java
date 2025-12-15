package com.example.warehouse.warehouseclient.Auth;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField tfLogin;
    @FXML private PasswordField pfPassword;
    @FXML private Label lblError;

    private Role role;

    @FXML
    public void onLogin() {
        String u = tfLogin.getText() == null ? "" : tfLogin.getText().trim();
        String p = pfPassword.getText() == null ? "" : pfPassword.getText();

        Role r = AuthService.login(u, p);
        if (r == null) {
            lblError.setText("Неверный логин или пароль.");
            return;
        }

        role = r;
        ((Stage) tfLogin.getScene().getWindow()).close();
    }

    public Role getRole() {
        return role;
    }
}
