package com.p2p;

import java.io.FileReader;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
    private static Connection connection;
    private static String driverName = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

    public static String dbUrl() throws Exception {
        InputStream reader = DatabaseConnection.class.getResourceAsStream("/config/db.properties");
        Properties properties = new Properties();
        properties.load(reader);
        String url = "jdbc:sqlserver://localhost:1433;"
                + "databaseName=" + properties.getProperty("databaseName") + ";"
                + "user=" + properties.getProperty("user") + ";"
                + "password=" + properties.getProperty("password");
        return url;
    }

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName(driverName);
            connection = DriverManager.getConnection(dbUrl());
            if (connection != null) {
                System.out.println("Connection is ok.");
            } else {
                System.out.println("Connection sucks.");
            }
        } catch (Exception ex) {
            System.out.println("Driver not found.");
        }
        return connection;
    }
}
