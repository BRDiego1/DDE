package com.home.asismay;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Node;

import java.io.IOException;
import java.sql.*;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Obtener el rol del usuario si las credenciales son correctas
        String userRole = getUserRole(username, password);

        if (userRole == null) {
            // Si las credenciales son incorrectas, mostrar un mensaje de error
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Acceso Denegado");
            alert.setHeaderText(null);
            alert.setContentText("Usuario o contraseña incorrectos.");
            alert.showAndWait();
        } else {
            // Redirigir según el rol del usuario
            if (userRole.equals("admin")) {
                goToPanel(event, "HomeAdmin.fxml"); // Redirigir a Admin
            } else if (userRole.equals("superadmin")) {
                goToPanel(event, "SuperAdmin.fxml"); // Redirigir a SuperAdmin
            }
        }
    }

    private String getUserRole(String username, String password) {
        String sql = "SELECT puesto FROM users WHERE usuario = ? AND contraseña = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("puesto"); // Devuelve "admin" o "superadmin"
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Si el usuario no existe o la contraseña es incorrecta
    }

    private void goToPanel(ActionEvent event, String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    @FXML
    private void goHome(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("home-view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
