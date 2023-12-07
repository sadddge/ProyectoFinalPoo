package com.example.proyectofinalpoov2;

import java.sql.*;

public class GestorDB {
    private final static String host =  "jdbc:mysql://localhost/proyecto";
    private final static String user = "root";
    private final static String pass = "";

    private final Connection connection;

    public GestorDB () {
        try {
            connection = DriverManager.getConnection(host,user,pass);
            System.out.println("Conectado con exito");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void addUser(String user, String pass) {
        try {
            String consult = "INSERT INTO jugadores (usuario, contraseña, nivel) VALUES (?, ?, 0)";

            try (PreparedStatement statement = connection.prepareStatement(consult)) {
                statement.setString(1, user);
                statement.setString(2, pass);
                statement.executeUpdate();

                System.out.println("Agregado con éxito");
            }
        } catch (SQLException e) {
            System.out.println("Ha ocurrido un error inesperado con el usuario ingresado");
        }
    }

    public User findUser (String user){

        try {
            String consult = "SELECT usuario, contraseña, nivel FROM jugadores WHERE usuario = ?";

            try (PreparedStatement statement = connection.prepareStatement(consult)) {
                statement.setString(1, user);

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {

                        String name = resultSet.getString("usuario");
                        String password = resultSet.getString("contraseña");
                        int level = resultSet.getInt("nivel");

                        return new User(name, password, level);

                    } else {
                        System.out.println("Usuario no encontrado");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean isUserExists(String username) {
        try {
            String consult = "SELECT * FROM jugadores WHERE usuario = ?";

            try (PreparedStatement statement = connection.prepareStatement(consult)) {
                statement.setString(1, username);

                try (ResultSet resultSet = statement.executeQuery()) {
                    return resultSet.next();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public void updateUserLevel (String user, int newLevel){
        try {
            String consult = "UPDATE jugadores SET nivel = ? WHERE usuario = ?";

            try (PreparedStatement statement = connection.prepareStatement(consult)) {
                statement.setInt(1, newLevel);
                statement.setString(2, user);
                statement.executeUpdate();
                System.out.println("Nivel actualizado con éxito");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTableJugadores() {

        try (Statement statement = connection.createStatement()) {

            String consult = "CREATE TABLE IF NOT EXISTS jugadores (" +
                                    "usuario PRIMARY KEY," +
                                    "contraseña VARCHAR(50) NOT NULL," +
                                    "nivel INT DEFAULT 0," + ")";

            statement.executeUpdate(consult);

        } catch (SQLException e) {

            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }

    }
}
