package com.p2p;

public class User {
    private int id;
    private String userName;
    private String password;
    private int isUserConnected;
    private int isBusy;
    private String chatClientName;

    public User(String userName, String password, int isUserConnected, String chatClientName, int isBusy) {
        this.userName = userName;
        this.password = password;
        this.isUserConnected = isUserConnected;
        this.chatClientName = chatClientName;
        this.isBusy = isBusy;
    }

    public User(String chatClientName) {
        this.chatClientName = chatClientName;
    }

    public String getChatClientName() {
        return chatClientName;
    }

    public void setChatClientName(String chatClientName) {
        this.chatClientName = chatClientName;
    }

    public int getIsBusy() {
        return isBusy;
    }

    public void setIsBusy(int isBusy) {
        this.isBusy = isBusy;
    }

    public int getIsUserConnected() {
        return isUserConnected;
    }

    public void setIsUserConnected(int isUserConnected) {
        this.isUserConnected = isUserConnected;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
