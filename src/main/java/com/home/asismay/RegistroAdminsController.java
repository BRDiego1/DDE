package com.home.asismay;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RegistroAdminsController {

    @FXML
    private TextField usuarioTextField;

    @FXML
    private TextField emailTextField;

    @FXML
    private PasswordField contraseñaTextField;

    @FXML
    private MenuButton puestoMenuButton;

    @FXML
    private TableView<Usuario> tablaUsuarios;

    @FXML
    private TableColumn<Usuario, String> columnaUsuario;

    @FXML
    private TableColumn<Usuario, String> columnaEmail;

    @FXML
    private TableColumn<Usuario, String> columnaPuesto;

    private String puestoSeleccionado = "";

    private ObservableList<Usuario> listaUsuarios = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Configuración de las columnas de la tabla
        columnaUsuario.setCellValueFactory(cellData -> cellData.getValue().usuarioProperty());
        columnaEmail.setCellValueFactory(cellData -> cellData.getValue().emailProperty());
        columnaPuesto.setCellValueFactory(cellData -> cellData.getValue().puestoProperty());

        cargarUsuarios();

        // Manejo de la selección del puesto
        for (MenuItem item : puestoMenuButton.getItems()) {
            item.setOnAction(event -> {
                puestoSeleccionado = item.getText();
                puestoMenuButton.setText(puestoSeleccionado);
            });
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

    @FXML
    private void registrobd() {
        String usuario = usuarioTextField.getText();
        String email = emailTextField.getText();
        String contraseña = contraseñaTextField.getText();

        if (usuario.isEmpty() || email.isEmpty() || contraseña.isEmpty() || puestoSeleccionado.isEmpty()) {
            mostrarAlerta("Error", "Todos los campos son obligatorios.");
            return;
        }

        if (!validarEmail(email)) {
            mostrarAlerta("Error", "El formato del correo electrónico es inválido.");
            return;
        }

        if (DatabaseConnection.existeUsuario(usuario) || DatabaseConnection.existeEmail(email)) {
            mostrarAlerta("Error", "El usuario o el correo electrónico ya está registrado.");
            return;
        }

        String contraseñaEncriptada = BCrypt.hashpw(contraseña, BCrypt.gensalt());

        DatabaseConnection.insertUser(usuario, email, contraseñaEncriptada, puestoSeleccionado);
        mostrarAlerta("Éxito", "Usuario registrado correctamente.");

        limpiarCampos();
        cargarUsuarios();  // Recargar usuarios después de agregar uno nuevo
    }

    @FXML
    private void editardb() {
        Usuario usuarioSeleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();
        if (usuarioSeleccionado == null) {
            mostrarAlerta("Error", "Selecciona un usuario para modificar.");
            return;
        }

        String usuario = usuarioTextField.getText();
        String email = emailTextField.getText();
        String contraseña = contraseñaTextField.getText();

        if (usuario.isEmpty() || email.isEmpty() || contraseña.isEmpty() || puestoSeleccionado.isEmpty()) {
            mostrarAlerta("Error", "Todos los campos son obligatorios.");
            return;
        }

        String contraseñaEncriptada = BCrypt.hashpw(contraseña, BCrypt.gensalt());

        DatabaseConnection.updateUser(usuarioSeleccionado.getId(), usuario, email, contraseñaEncriptada, puestoSeleccionado);
        mostrarAlerta("Éxito", "Usuario modificado correctamente.");

        limpiarCampos();
        cargarUsuarios();  // Recargar usuarios después de modificar uno
    }

    @FXML
    private void eliminardb() {
        Usuario usuarioSeleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();
        if (usuarioSeleccionado == null) {
            mostrarAlerta("Error", "Selecciona un usuario para eliminar.");
            return;
        }

        DatabaseConnection.deleteUser(usuarioSeleccionado.getId());
        mostrarAlerta("Éxito", "Usuario eliminado correctamente.");

        limpiarCampos();
        cargarUsuarios();  // Recargar usuarios después de eliminar uno
    }

    @FXML
    private void deletedb() {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION, "¿Estás seguro de eliminar todos los usuarios?", ButtonType.YES, ButtonType.NO);
        confirmacion.showAndWait();

        if (confirmacion.getResult() == ButtonType.YES) {
            DatabaseConnection.deleteAllUsers();
            mostrarAlerta("Éxito", "Todos los usuarios han sido eliminados.");
            cargarUsuarios();  // Recargar usuarios después de eliminar todos
        }
    }

    private void cargarUsuarios() {
        listaUsuarios.clear();  // Limpiar la lista antes de recargar
        ResultSet rs = DatabaseConnection.obtenerUsuarios();

        try {
            while (rs != null && rs.next()) {
                listaUsuarios.add(new Usuario(
                        rs.getInt("id"),
                        rs.getString("usuario"),
                        rs.getString("email"),
                        rs.getString("puesto")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error al cargar los usuarios.");
            e.printStackTrace();
        }

        tablaUsuarios.setItems(listaUsuarios);
    }

    private boolean validarEmail(String email) {
        String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(regex);
    }

    private void limpiarCampos() {
        usuarioTextField.clear();
        emailTextField.clear();
        contraseñaTextField.clear();
        puestoMenuButton.setText("Seleccionar");
        puestoSeleccionado = "";
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
