package com.p2p.service;

import com.p2p.repository.DatabaseOperations;

import java.net.*;
import java.util.logging.Logger;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientName;
    private DatabaseOperations databaseOperations = new DatabaseOperations();
    private Scanner scanner = new Scanner(System.in);
    private boolean isAccepted = false;
    public List<String> clientArrayList = new ArrayList<>();

    public void search(Logger logger) {//User searches another user.
        System.out.println("Please enter the nickname that you want to know online status: ");
        String onlineRequestMessage = scanner.nextLine();
        databaseOperations.searchOperation(onlineRequestMessage);
    }

    public void logOut(User user, Logger logger) throws IOException {//User logs out.
        databaseOperations.changeStatusAsNotBusy(user.getUserName());
        databaseOperations.changeStatusAsNotOnline(user.getUserName());
        System.exit(0);
    }

    public void chatRequest(User user, Logger logger) throws IOException {//User sends chat request to another user.
        System.out.println("Please enter the nickname that you want to send chat request: ");
        String messageFromUser = scanner.nextLine();
        logger.info("MESSAGE SENT: " + user.getUserName() + " > " + messageFromUser);
        if (databaseOperations.getOnlineSituation(messageFromUser).equals("CONNECTED")) {
            if (databaseOperations.getBusySituation(messageFromUser).equals("NOT BUSY")) {
                if (databaseOperations.checkUsernameExistence(messageFromUser)) {
                    bufferedWriter.write(messageFromUser);
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                } else {
                    System.out.println("There are no user registered with that nickname. Try again.");
                    messageFromUser = scanner.nextLine();
                    bufferedWriter.write(user.getUserName() + " > " + messageFromUser);
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                }
            } else {
                System.out.println("User " + messageFromUser + " is busy. Try again later.");
            }
        } else {
            System.out.println("User " + messageFromUser + " is offline. Try again later.");
        }

    }

    public void acceptRequest(User user, Logger logger) throws IOException { //Requested user accepts.
        isAccepted = true;
        databaseOperations.changeStatusAsBusy(user.getUserName());
        bufferedWriter.write("OK");
        logger.info("MESSAGE SENT: " + user.getUserName() + " > " + "OK");
        bufferedWriter.newLine();
        bufferedWriter.flush();
    }

    public void sendMessageToOtherUser(User user, Logger logger) { //Normal message send method.
        try {
            bufferedWriter.write(user.getUserName());
            bufferedWriter.newLine();
            bufferedWriter.flush();
            System.out.println("Some commands that you can use are listed below: ");
            System.out.println("SEARCH -> Enter the nickname that you want to monitor given user's online status.");
            System.out.println("CHAT REQUEST -> Create a request to chat.");
            System.out.println("LOGOUT -> Type it go offline.");
            while (socket.isConnected()) {
                String messageFromUser = scanner.nextLine();
                if ("SEARCH".equals(messageFromUser)) {
                    search(logger);
                } else if ("LOGOUT".equals(messageFromUser)) {
                    logOut(user, logger);
                } else if ("CHAT REQUEST".equals(messageFromUser)) {
                    chatRequest(user, logger);
                } else if ("OK".equals(messageFromUser)) {
                    acceptRequest(user, logger);
                } else if ("REJECT".equals(messageFromUser)) {
                    isAccepted = false;
                    System.out.println("You have rejected chat request.");
                } else if (clientName.equals(user.getUserName())) {
                    bufferedWriter.write(clientName + " > " + messageFromUser);
                    logger.info("MESSAGE SENT: " + clientName + " > " + messageFromUser);
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                }
            }
        } catch (IOException e) {
            databaseOperations.changeStatusAsNotBusy(user.getUserName());
            databaseOperations.changeStatusAsNotOnline(user.getUserName());
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public void listenForMessage(Client client, User user, Logger logger) { //Listens messages from other users.
        new Thread(() -> {
            String listenedMessage;
            String currentClient = "";
            isAccepted = false;
            while (socket.isConnected()) {
                try {
                    listenedMessage = bufferedReader.readLine();

                    if (listenedMessage.equals(clientName)) {
                        System.out.println("Type OK to accept request.");
                        currentClient = listenedMessage;
                    } else if (listenedMessage.equals("OK")) {
                        databaseOperations.changeStatusAsBusy(user.getUserName());
                        isAccepted = true;
                    } else if (listenedMessage.equals("REJECT")) {
                        System.out.println("Chat request is rejected.");
                        isAccepted = false;
                        client.clientArrayList.clear();
                    } else if (isAccepted) {
                        if (client.clientArrayList.isEmpty()) {
                            add(client, listenedMessage.substring(0, listenedMessage.indexOf(" ")));
                        }
                        if (listenedMessage.contains(" ")) {
                            if (client.clientArrayList.contains(listenedMessage.substring(0, listenedMessage.indexOf(" ")))) {
                                logger.info("MESSAGE RECEIVED: " + listenedMessage);
                                System.out.println(listenedMessage);
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void add(Client client, String name) { //Adds user to client array.
        if (client.clientArrayList.isEmpty() && databaseOperations.checkUsernameExistence(name)) {
            client.clientArrayList.add(name);
        }
    }

    public Client(Socket socket, String clientName) {//constructor.
        try {
            this.socket = socket;
            this.clientName = clientName;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
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
}
