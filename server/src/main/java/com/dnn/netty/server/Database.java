package com.dnn.netty.server;

import java.sql.*;

public class Database {
    private static Connection connection;
    private static Statement statement;

    public static void connect() {
        try {
            String url = "jdbc:postgresql://ec2-34-251-245-108.eu-west-1.compute.amazonaws.com:5432/d5gmno0qk5qjah?sslmode=require";
            String user = "bmdubxlelfebxw";
            String pass = "34730ab8628e7fbc84bf42f311641e81a54d591042e56b1d70dabd61cdeaae74";
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(url, user, pass);
            statement = connection.createStatement();
            System.out.println("Database is connected");
        } catch (Exception e) {
            System.out.println("Database connection error");
            e.printStackTrace();
        }
    }

    public static String getNickname(String clientID) {
        try {
            ResultSet rs = statement.executeQuery(String.format(
                    "SELECT nickname FROM users WHERE id ='%s'", clientID
            ));
            if (rs.next()) {
                return rs.getString("nickname");
            }
        } catch (SQLException e) {
            System.out.println("There was error while server tried to get nickname by ID");
            e.printStackTrace();
        }
        return null;
    }

    public static String registerUser(String login, String password) {
        try {
            String nickname = "User " + ((int) (Math.random() * 1000000));
            statement.executeUpdate(String.format(
                    "INSERT INTO users (login, password, nickname) VALUES ('%s','%s','%s')", login, password, nickname
            ));
            return getId(login,password);
        } catch (SQLException e) {
            return null;
        }
    }

    public static void disconnect() {
        try {
            System.out.println("Database is disconnected");
            connection.close();
        } catch (SQLException e) {
            System.out.println("Database disconnection error");
            e.printStackTrace();
        }
    }

    public static boolean changePassword(String clientID, String oldPassword, String newPassword) {
        try {
            ResultSet rs = statement.executeQuery(String.format(
                    "SELECT password FROM users WHERE id = '%s'", clientID
            ));
            if (rs.next()) {
                if ((rs.getString("password")).equals(oldPassword)) {
                    statement.executeUpdate(String.format(
                            "UPDATE users SET password = '%s' WHERE id = '%s'", newPassword, clientID
                    ));
                    return true;
                }
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getId(String login, String password) {
        try {
            ResultSet rs = statement.executeQuery(String.format(
                    "SELECT id FROM users WHERE login ='%s' AND password = '%s'", login, password
            ));
            if (rs.next()) {
                return rs.getString("id");
            }
        } catch (SQLException e) {
            System.out.println("There was error while server tried to get id by login and password");
            e.printStackTrace();
        }
        return null;
    }

    public static boolean changeNickname(String clientId, String newNickname) {
        try{
            statement.executeUpdate(String.format(
                    "UPDATE users SET nickname = '%s' WHERE id = '%s'", newNickname, clientId
            ));
            return true;
        }catch (SQLException e){
            System.out.println("There was error while server tried to get nickname");
            return false;
        }
    }

    public static boolean changeLogin(String clientId, String newLogin) {

        try{
            statement.executeUpdate(String.format(
                    "UPDATE users SET login = '%s' WHERE id = '%s'", newLogin, clientId
            ));
            return true;
        }catch (SQLException e){
            System.out.println("There was error while server tried to get login");
            System.out.println(e.getMessage());
            return false;
        }

    }

    public static String checkLogin(String login) {
        try{
            ResultSet rs = statement.executeQuery(String.format(
                    "SELECT id FROM users WHERE login ='%s'", login
            ));
            if (rs.next()) {
                return rs.getString("id");
            }
        }catch (SQLException e){
            return  null;
        }
        return null;
    }
}
