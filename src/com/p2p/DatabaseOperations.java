package com.p2p;

import java.sql.*;

public class DatabaseOperations {
    private Statement statement;
    private ResultSet resultSet;
    private PreparedStatement preparedStatement;
    private Connection connection;

    public void changeStatus(String userName) {
        String update = "UPDATE users SET isConnected = 0 WHERE name LIKE '" + userName + "'";
        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.createStatement();
            statement.execute(update);
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void searchOperation(String userName) {
        String update = "select isConnected from users WHERE name LIKE '" + userName + "'";
        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(update);
            while (resultSet.next()) {
                int connectionStatus = resultSet.getInt("isConnected");
                System.out.println(connectionStatus);
                if (connectionStatus == 1) {
                    System.out.println(userName + " is online.");
                } else if (connectionStatus == 0) {
                    System.out.println(userName + " is offline.");
                }
            }
            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addClient(User user) {
        String insertClient = "INSERT into users (name,password, isConnected) values (?,?,?)";
        try {
            connection = DatabaseConnection.getConnection();
            preparedStatement = connection.prepareStatement(insertClient);
            preparedStatement.setString(1, user.getUserName());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setInt(3, user.getIsUserConnected());
            System.out.println("Client inserted: " + preparedStatement.executeUpdate());
            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String checkUsernameDuplication(String userName) {
        String checkUserId = "select id from users where name LIKE '" + userName + "'";
        String fetchedName = "";
        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(checkUserId);
            while (resultSet.next()) {
                Integer userId = resultSet.getInt("id");
                String checkUserName = "select name from users where id = " + userId + "";
                resultSet.close();
                if (userId != null) {
                    resultSet = statement.executeQuery(checkUserName);
                    while (resultSet.next()) {
                        fetchedName = resultSet.getString("name");
                    }
                }
            }
            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return fetchedName;
    }
}
