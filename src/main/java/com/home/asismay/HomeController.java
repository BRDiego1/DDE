package com.home.asismay;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class HomeController {

    @FXML
    private TableView<Asistencia> table;

    @FXML
    private TableColumn<Asistencia, String> nombreColumn;

    @FXML
    private TableColumn<Asistencia, String> asistenciaColumn;

    @FXML
    private Button loadFileButton;

    private ObservableList<Asistencia> data = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        // Inicializa las columnas de la tabla
        nombreColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNombre()));
        asistenciaColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAsistencia()));

        // Configura el botón de carga de archivo
        loadFileButton.setOnAction(e -> cargarArchivoExcel());
    }

    private void cargarArchivoExcel() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos Excel", "*.xlsx"));
        File file = fileChooser.showOpenDialog(new Stage());

        if (file != null) {
            try {
                FileInputStream fis = new FileInputStream(file);
                Workbook workbook = new XSSFWorkbook(fis);
                Sheet sheet = workbook.getSheetAt(0);

                data.clear();

                for (Row row : sheet) {
                    if (row.getRowNum() == 0) continue;

                    String nombre = getCellValue(row, 0);
                    String asistencia = getCellValue(row, 1);
                    Asistencia asistenciaEmpleado = new Asistencia(nombre, asistencia);

                    for (int i = 2; i < row.getPhysicalNumberOfCells(); i++) {
                        String estadoDia = getCellValue(row, i);
                        asistenciaEmpleado.setAsistenciaDia(i - 1, estadoDia);
                    }

                    data.add(asistenciaEmpleado);
                }

                table.setItems(data);
                generarColumnasDias(sheet);

                fis.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private String getCellValue(Row row, int columnIndex) {
        Cell cell = row.getCell(columnIndex);
        if (cell != null) {
            switch (cell.getCellType()) {
                case STRING:
                    return cell.getStringCellValue();
                case NUMERIC:
                    return String.valueOf(cell.getNumericCellValue());
                case BOOLEAN:
                    return String.valueOf(cell.getBooleanCellValue());
                default:
                    return "";
            }
        }
        return "";
    }

    private void generarColumnasDias(Sheet sheet) {
        int diasDelMes = java.time.LocalDate.now().lengthOfMonth();

        for (int i = 1; i <= diasDelMes; i++) {
            int dia = i;
            TableColumn<Asistencia, String> dayColumn = new TableColumn<>(String.valueOf(i));

            dayColumn.setCellValueFactory(cellData -> {
                Asistencia asistencia = cellData.getValue();
                return new SimpleStringProperty(asistencia.getAsistenciaDia(dia));
            });

            dayColumn.setCellFactory(cell -> new TableCell<Asistencia, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : item);
                }
            });

            table.getColumns().add(dayColumn);
        }
    }

    // Método para cambiar de escena sin restricción de usuario administrador
    public void goAdmin(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("HomeAdmin.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
