package com.p2p;

import java.sql.*;

public class DatabaseOperations {
    private Statement statement;
    private ResultSet resultSet;
    private PreparedStatement preparedStatement;
    private Connection connection;

    public void changeOnlineStatus(String userName) {
        String updateOnlineStatus = "UPDATE users SET isConnected = 0 WHERE name LIKE '" + userName + "'";
        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.createStatement();
            statement.execute(updateOnlineStatus);
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void searchOperation(String userName) {
        String searchOnlineUser = "select isConnected from users WHERE name LIKE '" + userName + "'";
        try {
            if (checkUsernameExistence(userName)) {
                connection = DatabaseConnection.getConnection();
                statement = connection.createStatement();
                resultSet = statement.executeQuery(searchOnlineUser);
                while (resultSet.next()) {
                    int connectionStatus = resultSet.getInt("isConnected");
                    if (connectionStatus == 1) {
                        System.out.println(userName + " is online.");
                    } else if (connectionStatus == 0) {
                        System.out.println(userName + " is offline.");
                    }
                }
            } else {
                System.out.println(userName + " is NOT FOUND.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean checkUsernameExistence(String userName) {
        String getUsernameExistence = "select name from users";
        boolean isExist = false;
        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(getUsernameExistence);
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                if (userName.equals(name)) {
                    isExist = true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isExist;
    }

    public void addNewUser(User user) {
        String insertUser = "INSERT into users (name,password, isConnected) values (?,?,?)";
        try {
            connection = DatabaseConnection.getConnection();
            preparedStatement = connection.prepareStatement(insertUser);
            preparedStatement.setString(1, user.getUserName());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setInt(3, user.getIsUserConnected());
            preparedStatement.executeUpdate();
            System.out.println("User " + user.getUserName() + " is inserted.");
            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean authenticationForSignIn(String userName, String password) {
        boolean isAuthenticated = false;
        String authenticateUser = "select name, password from users where name LIKE '" + userName + "'";
        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(authenticateUser);
            while (resultSet.next()) {
                String fetchedName = resultSet.getString("name");
                String fetchedPassword = resultSet.getString("password");
                if (fetchedName.equals(userName) && fetchedPassword.equals(password)) {
                    isAuthenticated = true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isAuthenticated;
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
