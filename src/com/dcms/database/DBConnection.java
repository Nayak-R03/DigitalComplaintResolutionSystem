package com.dcms.database;

import java.sql.*;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/DCMS";
    private static final String USER = "root"; 
    private static final String PASSWORD = "nayak"; 

    public static Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Database Connected!");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        return conn;
    }
    public static void main(String[] args) {
        DBConnection.getConnection();
    }

}
