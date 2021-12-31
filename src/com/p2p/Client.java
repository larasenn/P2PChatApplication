package com.p2p;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientName;
    private String chatClientName;

    public Client(Socket socket, String clientName) {
        try {
            this.socket = socket;
            this.clientName = clientName;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public Client(String chatClientName) {
        this.chatClientName = chatClientName;
    }

    public String getChatClientName() {
        return chatClientName;
    }

    public void setChatClientName(String chatClientName) {
        this.chatClientName = chatClientName;
    }

    public void sendMsg(User user) {
        try {
            DatabaseOperations databaseOperations = new DatabaseOperations();
            bufferedWriter.write(user.getUserName());
            bufferedWriter.newLine();
            bufferedWriter.flush();
            Scanner sc = new Scanner(System.in);
            System.out.println("Some commands that you can use are listed below: ");
            System.out.println("SEARCH -> Enter the nickname that you want to monitor given user's online status.");
            System.out.println("CHAT REQUEST -> Create a request to chat.");
            System.out.println("LOGOUT -> Type it go offline.");
            boolean isAccepted = false;
            boolean isThatUser = false;
            while (socket.isConnected()) {
                String msg = sc.nextLine();
                if ("SEARCH".equals(msg)) {
                    System.out.println("Please enter the nickname that you want to know online status: ");
                    msg = sc.nextLine();
                    databaseOperations.searchOperation(msg);
                } else if ("LOGOUT".equals(msg)) {
                    user.setIsUserConnected(0);
                    user.setIsBusy(0);
                    databaseOperations.changeOnlineStatus(user.getUserName());
                    System.exit(0);
                } else if ("CHAT REQUEST".equals(msg)) {
                    isAccepted = true;
                    databaseOperations.changeBusyStatus(user.getUserName());
                    System.out.println("Please enter the nickname that you want to send chat request: ");
                    msg = sc.nextLine();
                    if (databaseOperations.getBusySituation(msg).equals("NOT BUSY")) {
                        //      user.setChatClientName(msg);
                        if (databaseOperations.checkUsernameExistence(msg)) {
                            bufferedWriter.write(msg);
                            bufferedWriter.newLine();
                            bufferedWriter.flush();
                            System.out.println("heyyo");
                        } else {
                            System.out.println("There are no user registered with that nickname. Try again.");
                            msg = sc.nextLine();
                            bufferedWriter.write(user.getUserName() + " > " + msg);
                            bufferedWriter.newLine();
                            bufferedWriter.flush();
                        }
                    }else{
                        System.out.println("User " + msg + " is busy. Try again later.");
                        System.exit(0);
                    }

                } else if ("OK".equals(msg)) {
                    isAccepted = true;
                    databaseOperations.changeBusyStatus(user.getUserName());
                    bufferedWriter.write("OK");
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                } else if ("REJECT".equals(msg)) {
                    System.out.println("You have rejected chat request.");
                } else if (isAccepted) {
                    bufferedWriter.write(clientName + " > " + msg);
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                }
            }
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }


    public void listenForMessage(User user) {
        new Thread(() -> {
            String msg;
            boolean isAccepted = false;
            boolean isThatUser = false;
            while (socket.isConnected()) {
                try {
                    msg = bufferedReader.readLine();
                    if (msg.equals(clientName)) {
                        System.out.println("Type OK to accept request.");
                        isAccepted = true;
                       // isThatUser = true;
                    } else if (msg.equals("OK")) {
                        System.out.println("Chat connection has established. You can chat now.");
                       // isThatUser = true;
                        isAccepted = true;
                    } else if (msg.equals("REJECT")) {
                        System.out.println("Chat request is rejected.");
                        isAccepted = false;
                    } else if (isAccepted) {
                        System.out.println(msg);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
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
            User user = new User(userName, password, 1, "empty", 0);
            //   arrayList.add(user);
            databaseOperations.addNewUser(user);
            client.listenForMessage(user);
            client.sendMsg(user);
        } else if (listenCommand.equals("SIGN IN")) {
            System.out.println("Enter your nickname: ");
            String userName = sc.nextLine();
            System.out.println("Enter your password: ");
            String password = sc.nextLine();
            if (databaseOperations.authenticationForSignIn(userName, password)) {
                User user = new User(userName, password, 1, "empty", 0);
                //  arrayList.add(user);
                Socket socket = new Socket("localhost", 5555);
                Client client = new Client(socket, userName);
                client.listenForMessage(user);
                client.sendMsg(user);
            } else {
                System.out.println("Incorrect nickname or password. Please try again.");
            }
        }
    }
}
