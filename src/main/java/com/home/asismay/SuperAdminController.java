package com.home.asismay;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class SuperAdminController {

    @FXML
    private ComboBox<String> panelSelect;

    @FXML
    private Button loadFileButton; // Botón para cargar el archivo Excel

    // Cambiar de panel según la opción seleccionada en el ComboBox
    @FXML
    public void initialize() {
        panelSelect.setOnAction(event -> cambiarPanel(event));

        // Configurar el botón de carga de archivo
        loadFileButton.setOnAction(e -> cargarArchivoExcel());
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

    // Método para cargar el archivo Excel, crear tabla y cargar los datos en la base de datos
    private void cargarArchivoExcel() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos Excel", "*.xlsx"));
        File file = fileChooser.showOpenDialog(new Stage());

        if (file != null) {
            try (FileInputStream fis = new FileInputStream(file)) {
                Workbook workbook = new XSSFWorkbook(fis);
                Sheet sheet = workbook.getSheetAt(0);

                // Extraer los nombres de las columnas desde la primera fila (cabecera)
                Row headerRow = sheet.getRow(0); // Asegúrate de obtener la primera fila
                int numColumns = headerRow.getPhysicalNumberOfCells(); // Contar cuántas columnas hay
                String[] columns = new String[numColumns];

                for (int i = 0; i < numColumns; i++) {
                    columns[i] = sanitizeColumnName(headerRow.getCell(i).getStringCellValue());
                }

                // Crear la tabla en la base de datos
                DatabaseConnection.createTableFromExcel("excel_data", columns);

                // Preparar los datos para insertar en la tabla
                String[][] data = new String[sheet.getPhysicalNumberOfRows() - 1][numColumns];
                for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
                    Row row = sheet.getRow(i);
                    for (int j = 0; j < numColumns; j++) {
                        data[i - 1][j] = getCellValue(row, j);
                    }
                }

                // Insertar los datos en la base de datos
                DatabaseConnection.insertData("excel_data", columns, data);

                fis.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private String getCellValue(Row row, int columnIndex) {
        if (row == null) {
            return ""; // Si la fila es nula, devolver vacío
        }
        Cell cell = row.getCell(columnIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell == null) {
            return ""; // Si la celda es nula, devolver vacío
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString(); // Convertir fecha a String
                } else {
                    return String.valueOf(cell.getNumericCellValue()); // Convertir número a String
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return cell.getStringCellValue(); // Intentar como STRING
                } catch (IllegalStateException e) {
                    return String.valueOf(cell.getNumericCellValue()); // Intentar como NUMÉRICO
                }
            default:
                return ""; // Para celdas vacías o desconocidas
        }
    }


    // Método para sanear el nombre de la columna, eliminando o reemplazando caracteres especiales
    private String sanitizeColumnName(String columnName) {
        // Reemplazar caracteres problemáticos como "/", espacios y "." por guiones bajos
        return columnName.replaceAll("[^a-zA-Z0-9_]", "_");
    }

    // Método para ir al panel de administración
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

    // Método para ir a la configuración de asistencia
    public void goConfigAsis(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("RegistroAsistencia.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}