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

    public static String getNickByLoginAndPass(String login, String password) {
        try {
            ResultSet rs = statement.executeQuery(String.format(
                    "SELECT nickname FROM users WHERE login ='%s' AND password = '%s'", login, password
            ));
            if (rs.next()) {
                return rs.getString("nickname");
            }
        } catch (SQLException e) {
            System.out.println("There was error while server tried to get nickname by login and password");
            e.printStackTrace();
        }
        return null;
    }

    public static String registerUser(String login, String password) {
        try {
            statement.executeUpdate(String.format(
                    "INSERT INTO users (login, password, nickname) VALUES ('%s','%s','%s')", login, password, login
            ));
            return getNickByLoginAndPass(login,password);
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

    public static boolean changePassword(String nickname, String oldPassword, String newPassword) {
        try {
            ResultSet rs = statement.executeQuery(String.format(
                    "SELECT password FROM users WHERE nickname = '%s'", nickname
            ));
            if (rs.getString("password").equals(oldPassword)) {
                statement.executeUpdate(String.format(
                        "UPDATE users SET password = '%s' WHERE nickname = '%s'", newPassword, nickname
                ));
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean changeNickAndLogin(String oldNickname, String newNickname, String newLogin) {
        try {
            statement.executeUpdate(String.format(
                    "UPDATE users SET nickname = '%s', login = '%s' WHERE nickname = '%s'", newNickname, newLogin, oldNickname
            ));
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


}
