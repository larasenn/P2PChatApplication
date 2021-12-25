package com.p2p;

import java.io.*;
import java.net.Socket;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private String userName;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    public Client(Socket socket, String userName) {
        try {
            this.socket = socket;
            this.userName = userName;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public void sendMsg(User user) {
        try {
            DatabaseOperations databaseOperations = new DatabaseOperations();
            bufferedWriter.write(userName);
            bufferedWriter.newLine();
            bufferedWriter.flush();
            Scanner sc = new Scanner(System.in);
            while (socket.isConnected()) {
                String msg = sc.nextLine();
                if ("search".equals(msg)) {
                    System.out.println("Please enter the nickname that you want to know online status: ");
                    msg = sc.nextLine();
                    databaseOperations.searchOperation(msg);
                }
                if ("exit".equals(msg)) {
                    user.setIsUserConnected(0);
                    databaseOperations.changeStatus(user.getUserName());
                    System.exit(0);
                }
                bufferedWriter.write(userName + " > " + msg);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public void listenForMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String msg;
                while (socket.isConnected()) {
                    try {
                        msg = bufferedReader.readLine();
                        System.out.println(msg);
                    } catch (IOException e) {
                        closeEverything(socket, bufferedReader, bufferedWriter);
                    }
                }

            }
        }).start();
    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        try {
            if (socket != null) {
                socket.close();
            }
            if (bufferedWriter != null){
                bufferedWriter.close();
            }
            if (bufferedReader != null) {
                bufferedReader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        DatabaseOperations databaseOperations = new DatabaseOperations();
        Scanner sc = new Scanner(System.in);
        System.out.println("Username: ");
        String userName = sc.nextLine();
        while(userName.equals(databaseOperations.checkUsernameDuplication(userName))){
            System.out.println("saaaa: ");
            System.out.println("This username has alredy been taken, please enter another nickname: ");
            userName = sc.nextLine();
        }
        System.out.println("Enter your password: ");
        String password = sc.nextLine();
        Socket socket = new Socket("localhost", 5555);
        Client client = new Client(socket, userName);
        User user = new User(userName, password, 1);
        databaseOperations.addClient(user);
        client.listenForMessage();
        client.sendMsg(user);
    }
}
