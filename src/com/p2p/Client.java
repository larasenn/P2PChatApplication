package com.p2p;

import java.io.*;
import java.net.Socket;
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
            System.out.println("Some commands that you can use is shown below: ");
            System.out.println("SEARCH -> Enter the nickname that you want to monitor given user's online status.\t");
            System.out.println("CHAT -> Enter the nickname that you want to chat with.\t");
            while (socket.isConnected()) {
                String msg = sc.nextLine();
                if ("SEARCH".equals(msg)) {
                    System.out.println("Please enter the nickname that you want to know online status: ");
                    msg = sc.nextLine();
                    databaseOperations.searchOperation(msg);
                }
                if ("LOGOUT".equals(msg)) {
                    user.setIsUserConnected(0);
                    databaseOperations.changeOnlineStatus(user.getUserName());
                    System.exit(0);
                }
                if ("CHAT REQUEST".equals(msg)) {
                    System.out.println("Please enter the nickname that you want to send chat request: ");
                    msg = sc.nextLine();
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
        new Thread(() -> {
            String msg;
            while (socket.isConnected()) {
                try {
                    msg = bufferedReader.readLine();
                    System.out.println(msg);
                } catch (IOException e) {
                    closeEverything(socket, bufferedReader, bufferedWriter);
                }
            }
        }).start();
    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        try {
            if (socket != null) {
                socket.close();
            }
            if (bufferedWriter != null) {
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
        System.out.println("Welcome to our chat application!");
        //  System.out.println("Type COMMANDS to see available commands and what they stands for");
        //  String commands = sc.nextLine().toUpperCase();
        //  if(commands.equals("COMMANDS")){
        System.out.println("SIGN UP and SIGN IN -> Sign up or Sign in with a proper nickname and password to join chat.\t");
        String listenCommand = sc.nextLine();

        if (listenCommand.equals("SIGN UP")) {
            System.out.println("Enter your nickname: ");
            String userName = sc.nextLine();
            while (userName.equals(databaseOperations.checkUsernameDuplication(userName))) {
                System.out.println("This username has already been taken, please enter another nickname: ");
                userName = sc.nextLine();
            }
            System.out.println("Enter your password: ");
            String password = sc.nextLine();
            Socket socket = new Socket("localhost", 5555);
            Client client = new Client(socket, userName);
            User user = new User(userName, password, 1);
            databaseOperations.addNewUser(user);
            client.listenForMessage();
            client.sendMsg(user);
        } else if (listenCommand.equals("SIGN IN")) {
            System.out.println("Enter your nickname: ");
            String userName = sc.nextLine();
            System.out.println("Enter your password: ");
            String password = sc.nextLine();
            if (databaseOperations.authenticationForSignIn(userName, password)) {
                Socket socket = new Socket("localhost", 5555);
                Client client = new Client(socket, userName);
                User user = new User(userName, password, 1);
                client.listenForMessage();
                client.sendMsg(user);
            } else {
                System.out.println("Incorrect nickname or password. Please try again.");
            }
        }
    }
}
