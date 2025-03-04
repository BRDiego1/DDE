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

    public static void updateUser(int id, String usuario, String email, String contraseña, String puesto) {
        String sql = "UPDATE users SET usuario = ?, email = ?, contraseña = ?, puesto = ? WHERE id = ?";

        try (Connection connection = connect();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, usuario);
            stmt.setString(2, email);
            stmt.setString(3, contraseña);
            stmt.setString(4, puesto);
            stmt.setInt(5, id);

            stmt.executeUpdate();
            System.out.println("Usuario actualizado correctamente");

        } catch (SQLException e) {
            System.out.println("Error al actualizar usuario.");
            e.printStackTrace();
        }
    }

    public static void deleteUser(int id) {
        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection connection = connect();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
            System.out.println("Usuario eliminado correctamente");

        } catch (SQLException e) {
            System.out.println("Error al eliminar usuario.");
            e.printStackTrace();
        }
    }

    public static void deleteAllUsers() {
        String sql = "DELETE FROM users";

        try (Connection connection = connect();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.executeUpdate();
            System.out.println("Todos los usuarios han sido eliminados");

        } catch (SQLException e) {
            System.out.println("Error al eliminar todos los usuarios.");
            e.printStackTrace();
        }
    }

    public static ResultSet obtenerUsuarios() {
        String sql = "SELECT id, usuario, email, puesto FROM users";

        try {
            Connection connection = connect();
            if (connection != null) {
                PreparedStatement stmt = connection.prepareStatement(sql);
                return stmt.executeQuery();
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener usuarios.");
            e.printStackTrace();
        }
        return null;
    }

    // Crear tabla dinámica a partir de los encabezados del Excel
    public static void createTableFromExcel(String tableName, String[] columns) {
        try (Connection connection = connect()) {
            if (connection != null) {
                Statement stmt = connection.createStatement();

                // Crear la sentencia SQL para crear la tabla
                StringBuilder sql = new StringBuilder("CREATE TABLE IF NOT EXISTS " + tableName + " (");
                for (String column : columns) {
                    sql.append(column).append(" VARCHAR(255), ");
                }
                sql.delete(sql.length() - 2, sql.length());  // Eliminar la última coma y espacio
                sql.append(");");

                stmt.executeUpdate(sql.toString());
                System.out.println("Tabla '" + tableName + "' creada exitosamente.");
            }
        } catch (SQLException e) {
            System.out.println("Error al crear la tabla.");
            e.printStackTrace();
        }
    }

    // Insertar datos desde Excel a la base de datos
    public static void insertData(String tableName, String[] columns, String[][] data) {
        try (Connection connection = connect()) {
            if (connection != null) {
                StringBuilder sql = new StringBuilder("INSERT INTO " + tableName + " (");
                for (String column : columns) {
                    sql.append(column).append(", ");
                }
                sql.delete(sql.length() - 2, sql.length()); // Eliminar la última coma y espacio
                sql.append(") VALUES ");

                // Agregar los valores a insertar
                for (String[] row : data) {
                    sql.append("(");
                    for (String value : row) {
                        sql.append("'").append(value).append("', ");
                    }
                    sql.delete(sql.length() - 2, sql.length()); // Eliminar la última coma y espacio
                    sql.append("), ");
                }
                sql.delete(sql.length() - 2, sql.length()); // Eliminar la última coma y espacio

                Statement stmt = connection.createStatement();
                stmt.executeUpdate(sql.toString());
                System.out.println("Datos insertados exitosamente en la tabla '" + tableName + "'");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public static void main(String[] args) {
        createDatabase();
        createTable();
    }
}
