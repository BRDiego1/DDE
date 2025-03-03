package com.home.asismay;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.net.URL;
import java.util.ResourceBundle;
import org.mindrot.jbcrypt.BCrypt;

public class RegistroAdminsController {

    @FXML
    private TextField usuarioTextField;

    @FXML
    private TextField emailTextField;

    @FXML
    private PasswordField contraseñaTextField;

    @FXML
    private MenuButton puestoMenuButton;

    private String puestoSeleccionado = "";

    @FXML
    public void initialize() {
        // Asignar acción a los items del menú desplegable
        for (MenuItem item : puestoMenuButton.getItems()) {
            item.setOnAction(event -> {
                puestoSeleccionado = item.getText();
                puestoMenuButton.setText(puestoSeleccionado); // Cambiar el texto del botón
            });
        }
    }

    @FXML
    private void registrobd() {
        // Obtener valores del formulario
        String usuario = usuarioTextField.getText();
        String email = emailTextField.getText();
        String contraseña = contraseñaTextField.getText();

        // Validar que todos los campos estén completos
        if (usuario.isEmpty() || email.isEmpty() || contraseña.isEmpty() || puestoSeleccionado.isEmpty()) {
            mostrarAlerta("Error", "Todos los campos son obligatorios.");
            return;
        }

        // Validar email
        if (!validarEmail(email)) {
            mostrarAlerta("Error", "El formato del correo electrónico es inválido.");
            return;
        }

        // Verificar si el usuario o email ya existe
        if (usuarioExiste(usuario) || emailExiste(email)) {
            mostrarAlerta("Error", "El usuario o el correo electrónico ya está registrado.");
            return;
        }

        // Encriptar la contraseña
        String contraseñaEncriptada = BCrypt.hashpw(contraseña, BCrypt.gensalt());

        // Insertar en la base de datos
        try {
            DatabaseConnection.insertUser(usuario, email, contraseñaEncriptada, puestoSeleccionado);
            mostrarAlerta("Éxito", "Usuario registrado correctamente.");

            // Limpiar los campos
            usuarioTextField.clear();
            emailTextField.clear();
            contraseñaTextField.clear();
            puestoMenuButton.setText("Seleccionar");
            puestoSeleccionado = "";
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo registrar el usuario. Error: " + e.getMessage());
        }
    }

    // Método para validar formato de email
    private boolean validarEmail(String email) {
        String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(regex);
    }

    // Método para verificar si el usuario ya existe en la base de datos
    private boolean usuarioExiste(String usuario) {
        // Aquí iría la lógica para verificar si el usuario ya existe en la base de datos
        // Por ejemplo, una consulta SQL:
        // "SELECT COUNT(*) FROM usuarios WHERE usuario = ?"
        return DatabaseConnection.existeUsuario(usuario);
    }

    private boolean emailExiste(String email) {
        // Similar al anterior, verificar si el correo ya está registrado
        return DatabaseConnection.existeEmail(email);
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
