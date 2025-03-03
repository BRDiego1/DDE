package com.home.asismay;

import java.sql.*;

public class DatabaseConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/ASISMAY"; // Se conecta directamente a ASISMAY
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection connect() {
        try {
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            return connection;
        } catch (SQLException e) {
            System.out.println("Error al conectar con la base de datos.");
            e.printStackTrace();
        }
        return null;
    }

    public static void createDatabase() {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/", USER, PASSWORD)) {
            if (connection != null) {
                Statement stmt = connection.createStatement();
                String sql = "CREATE DATABASE IF NOT EXISTS ASISMAY";
                stmt.executeUpdate(sql);
                System.out.println("Base de datos 'ASISMAY' creada (si no existía)");
            }
        } catch (SQLException e) {
            System.out.println("Error al crear la base de datos.");
            e.printStackTrace();
        }
    }

    // Crear tabla de usuarios
    public static void createTable() {
        try (Connection connection = connect()) {
            if (connection != null) {
                Statement stmt = connection.createStatement();
                String sql = "CREATE TABLE IF NOT EXISTS users (" +
                        "id INT PRIMARY KEY AUTO_INCREMENT," +
                        "usuario VARCHAR(255) NOT NULL," +
                        "email VARCHAR(255) NOT NULL," +
                        "contraseña VARCHAR(255) NOT NULL," +
                        "puesto ENUM('superadmin', 'admin') NOT NULL" +
                        ")";
                stmt.executeUpdate(sql);
                System.out.println("Tabla 'users' creada (si no existía)");
            }
        } catch (SQLException e) {
            System.out.println("Error al crear la tabla de usuarios.");
            e.printStackTrace();
        }
    }

    // Crear tabla para la configuración de retardos
    public static void createTableRetardo() {
        try (Connection connection = connect()) {
            if (connection != null) {
                Statement stmt = connection.createStatement();
                String sql = "CREATE TABLE IF NOT EXISTS configuracion_retardo (" +
                        "id INT PRIMARY KEY AUTO_INCREMENT," +
                        "tipo_retardo VARCHAR(255) NOT NULL," +  // Tipo de retardo, por ejemplo, "Tardanza leve"
                        "tiempo_retardo INT NOT NULL," +        // Tiempo en minutos para considerar retardo
                        "tiempo_no_asistencia INT NOT NULL," +  // Tiempo en minutos para considerar no asistencia
                        "configurado_por INT NOT NULL," +       // ID del superadmin que configuró
                        "FOREIGN KEY (configurado_por) REFERENCES users(id) ON DELETE CASCADE" +
                        ")";
                stmt.executeUpdate(sql);
                System.out.println("Tabla 'configuracion_retardo' creada (si no existía)");
            }
        } catch (SQLException e) {
            System.out.println("Error al crear la tabla de configuracion_retardo.");
            e.printStackTrace();
        }
    }

    // Método para verificar si el usuario ya existe
    public static boolean existeUsuario(String usuario) {
        String sql = "SELECT COUNT(*) FROM users WHERE usuario = ?";

        try (Connection connection = connect();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, usuario);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0; // Si el usuario existe, devuelve true
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Método para verificar si el correo electrónico ya existe
    public static boolean existeEmail(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";

        try (Connection connection = connect();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0; // Si el correo electrónico existe, devuelve true
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Si el correo no existe o hay un error
    }

    // Obtener el rol del usuario
    public static String getUserRole(String usuario) {
        String sql = "SELECT puesto FROM users WHERE usuario = ?";

        try (Connection connection = connect();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, usuario);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("puesto"); // Retorna 'admin' o 'superadmin'
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Si el usuario no existe o hay un error
    }

    // Insertar un usuario en la base de datos
    public static void insertUser(String usuario, String email, String contraseña, String puesto) {
        String sql = "INSERT INTO users (usuario, email, contraseña, puesto) VALUES (?, ?, ?, ?)";

        try (Connection connection = connect();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, usuario);
            stmt.setString(2, email);
            stmt.setString(3, contraseña);
            stmt.setString(4, puesto);

            stmt.executeUpdate();
            System.out.println("Usuario insertado correctamente");

        } catch (SQLException e) {
            System.out.println("Error al insertar usuario.");
            e.printStackTrace();
        }
    }

    // Insertar configuración de retardo
    public static void insertConfiguracionRetardo(String tipoRetardo, int tiempoRetardo, int tiempoNoAsistencia, int superadminId) {
        String sql = "INSERT INTO configuracion_retardo (tipo_retardo, tiempo_retardo, tiempo_no_asistencia, configurado_por) " +
                "VALUES (?, ?, ?, ?)";

        try (Connection connection = connect();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, tipoRetardo);
            stmt.setInt(2, tiempoRetardo);
            stmt.setInt(3, tiempoNoAsistencia);
            stmt.setInt(4, superadminId);

            stmt.executeUpdate();
            System.out.println("Configuración de retardo guardada correctamente");

        } catch (SQLException e) {
            System.out.println("Error al guardar configuración de retardo.");
            e.printStackTrace();
        }
    }

    // Actualizar configuración de retardo
    public static void updateConfiguracionRetardo(int id, String tipoRetardo, int tiempoRetardo, int tiempoNoAsistencia, int superadminId) {
        String sql = "UPDATE configuracion_retardo SET tipo_retardo = ?, tiempo_retardo = ?, tiempo_no_asistencia = ?, configurado_por = ? " +
                "WHERE id = ?";

        try (Connection connection = connect();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, tipoRetardo);
            stmt.setInt(2, tiempoRetardo);
            stmt.setInt(3, tiempoNoAsistencia);
            stmt.setInt(4, superadminId);  // El ID del superadmin que lo configuró
            stmt.setInt(5, id);            // El ID del tipo de retardo que se quiere modificar

            stmt.executeUpdate();
            System.out.println("Tipo de retardo actualizado correctamente");

        } catch (SQLException e) {
            System.out.println("Error al actualizar el tipo de retardo.");
            e.printStackTrace();
        }
    }

    // Eliminar configuración de retardo
    public static void deleteConfiguracionRetardo(int id) {
        String sql = "DELETE FROM configuracion_retardo WHERE id = ?";

        try (Connection connection = connect();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, id);  // El ID del tipo de retardo que se quiere eliminar

            stmt.executeUpdate();
            System.out.println("Tipo de retardo eliminado correctamente");

        } catch (SQLException e) {
            System.out.println("Error al eliminar el tipo de retardo.");
            e.printStackTrace();
        }
    }

    // Obtener todas las configuraciones de retardo
    public static void obtenerConfiguracionesRetardo() {
        String sql = "SELECT * FROM configuracion_retardo";

        try (Connection connection = connect();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String tipoRetardo = rs.getString("tipo_retardo");
                int tiempoRetardo = rs.getInt("tiempo_retardo");
                int tiempoNoAsistencia = rs.getInt("tiempo_no_asistencia");
                int configuradoPor = rs.getInt("configurado_por");

                System.out.println("ID: " + id + ", Tipo Retardo: " + tipoRetardo +
                        ", Tiempo Retardo: " + tiempoRetardo + " min" +
                        ", Tiempo No Asistencia: " + tiempoNoAsistencia + " min" +
                        ", Configurado por: " + configuradoPor);
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener las configuraciones de retardo.");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        createDatabase();
        createTable();
        createTableRetardo();
    }
}
