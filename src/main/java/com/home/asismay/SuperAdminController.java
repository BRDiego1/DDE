package com.home.asismay;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

import java.io.IOException;

public class SuperAdminController {
    public void goRegisterAdmin(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("RegistroAdmins.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void goConfigAsis(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ConfigAsistencia.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private ComboBox<String> panelSelect;

    @FXML
    public void initialize() {
        // Agregar listener para detectar cambios en el ComboBox
        panelSelect.setOnAction(event -> cambiarPanel(event));
    }

    private void cambiarPanel(ActionEvent event) {
        String selectedPanel = panelSelect.getValue(); // Obtiene la opción seleccionada

        String fxmlFile = ""; // Variable para almacenar el archivo FXML a cargar

        switch (selectedPanel) {
            case "Panel Usuario":
                fxmlFile = "home-view.fxml"; // Ruta del FXML
                break;
            case "Panel Admin":
                fxmlFile = "HomeAdmin.fxml";
                break;
            default:
                return; // Si no hay selección válida, no hace nada
        }

        try {
            // Cargar la nueva vista
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            // Obtener la escena actual
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);

            // Cambiar la escena
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error al cargar el archivo FXML: " + fxmlFile);
        }
    }
}
