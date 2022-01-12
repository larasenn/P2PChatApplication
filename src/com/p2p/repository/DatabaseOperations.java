package com.p2p.repository;

import com.p2p.service.User;

import java.sql.*;

public class DatabaseOperations {
    private Statement statement;
    private ResultSet resultSet;
    private PreparedStatement preparedStatement;
    private Connection connection;

    public void changeStatusAsNotOnline(String userName) {//Changes user's online status as 0.
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

    public void changeStatusAsOnline(String userName) {//Changes user's online status as 1.
        String updateOnlineStatus = "UPDATE users SET isConnected = 1 WHERE name LIKE '" + userName + "'";
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

    public void changeStatusAsBusy(String userName) {//Changes user's busy status as 1.
        String updateBusyStatus = "UPDATE users SET isBusy = 1 WHERE name LIKE '" + userName + "'";
        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.createStatement();
            statement.execute(updateBusyStatus);
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void changeStatusAsNotBusy(String userName) {//Changes user's busy status as 0.
        String updateBusyStatus = "UPDATE users SET isBusy = 0 WHERE name LIKE '" + userName + "'";
        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.createStatement();
            statement.execute(updateBusyStatus);
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void searchOperation(String userName) {//Searches user according to given name.
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

    public boolean checkUsernameExistence(String userName) {//Checks whether a user exist or not.
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

    public void addNewUser(User user) {//Adds new user to database.
        String insertUser = "INSERT into users (name, password, isConnected, isBusy) values (?,?,?,?)";
        try {
            connection = DatabaseConnection.getConnection();
            preparedStatement = connection.prepareStatement(insertUser);
            preparedStatement.setString(1, user.getUserName());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setInt(3, user.getIsUserConnected());
            preparedStatement.setInt(4, user.getIsBusy());
            preparedStatement.executeUpdate();
            System.out.println("User " + user.getUserName() + " is inserted.");
            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getBusySituation(String userName) { //Gets busy situation(1 or 0) for a user.
        String authenticateUser = "select isBusy from users where name LIKE '" + userName + "'";
        String returnBusySituation = "";
        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(authenticateUser);
            while (resultSet.next()) {
                int getIsBusy = resultSet.getInt("isBusy");
                if (getIsBusy == 1) {
                    returnBusySituation = "BUSY";
                } else if (getIsBusy == 0) {
                    returnBusySituation = "NOT BUSY";
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return returnBusySituation;
    }

    public String getOnlineSituation(String userName) {//Gets online situation(1 or 0) for a user.
        String onlineStatus = "select isConnected from users where name LIKE '" + userName + "'";
        String returnOnlineSituation = "";
        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(onlineStatus);
            while (resultSet.next()) {
                int getIsConnected = resultSet.getInt("isConnected");
                if (getIsConnected == 1) {
                    returnOnlineSituation = "CONNECTED";
                } else if (getIsConnected == 0) {
                    returnOnlineSituation = "NOT CONNECTED";
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return returnOnlineSituation;
    }

    public boolean authenticationForSignIn(String userName, String password) { //When signIn method runs, this method checks whether that user's credentials is true.
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

    public String checkUsernameDuplication(String userName) {//Make sures one nickname can be taken only one time.
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
