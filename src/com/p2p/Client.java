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
    private DatabaseOperations databaseOperations = new DatabaseOperations();
    private Scanner sc = new Scanner(System.in);
    private boolean isAccepted = false;
    public ArrayList<String> clientArrayList = new ArrayList<>();

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

    public void search() {
        System.out.println("Please enter the nickname that you want to know online status: ");
        String onlineRequestMessage = sc.nextLine();
        databaseOperations.searchOperation(onlineRequestMessage);
    }

    public void logOut(User user) {
        databaseOperations.changeStatusAsNotBusy(user.getUserName());
        databaseOperations.changeStatusAsNotOnline(user.getUserName());
        System.exit(0);
    }

    public void chatRequest(User user) throws IOException {
        System.out.println("Please enter the nickname that you want to send chat request: ");
        String messageFromUser = sc.nextLine();
        if (databaseOperations.getBusySituation(messageFromUser).equals("NOT BUSY")) {
            if (databaseOperations.checkUsernameExistence(messageFromUser)) {
                bufferedWriter.write(messageFromUser);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            } else {
                System.out.println("There are no user registered with that nickname. Try again.");
                messageFromUser = sc.nextLine();
                bufferedWriter.write(user.getUserName() + " > " + messageFromUser);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        } else {
            System.out.println("User " + messageFromUser + " is busy. Try again later.");
        }
    }

    public void acceptRequest(User user) throws IOException {
        databaseOperations.changeStatusAsBusy(user.getUserName());
        bufferedWriter.write("OK");
        bufferedWriter.newLine();
        bufferedWriter.flush();
    }

    public void sendMessageToOtherUser(User user, Client client) {
        try {
            bufferedWriter.write(user.getUserName());
            bufferedWriter.newLine();
            bufferedWriter.flush();
            System.out.println("Some commands that you can use are listed below: ");
            System.out.println("SEARCH -> Enter the nickname that you want to monitor given user's online status.");
            System.out.println("CHAT REQUEST -> Create a request to chat.");
            System.out.println("LOGOUT -> Type it go offline.");
            while (socket.isConnected()) {
                String messageFromUser = sc.nextLine();
                if ("SEARCH".equals(messageFromUser)) {
                    search();
                } else if ("LOGOUT".equals(messageFromUser)) {
                    logOut(user);
                } else if ("CHAT REQUEST".equals(messageFromUser)) {
                    chatRequest(user);
                } else if ("OK".equals(messageFromUser)) {
                    acceptRequest(user);
                } else if ("REJECT".equals(messageFromUser)) {
                    System.out.println("You have rejected chat request.");
                } else if (clientName.equals(user.getUserName())) {
                    bufferedWriter.write(clientName + " > " + messageFromUser);
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

    public void listenForMessage(Client client, User user) {
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
                        isAccepted = true;
                    } else if (listenedMessage.equals("OK")) {
                        System.out.println("Chat connection has established. You can chat now.");//t
                        databaseOperations.changeStatusAsBusy(user.getUserName());
                        isAccepted = true;
                    } else if (listenedMessage.equals("REJECT")) {
                        System.out.println("Chat request is rejected.");
                        isAccepted = false;
                        client.clientArrayList.clear();
                    } else if (isAccepted) {
                        if (client.clientArrayList.size() == 0) {
                            add(client, listenedMessage.substring(0, 1));
                            System.out.println(clientArrayList);
                        }
                        if (client.clientArrayList.contains(listenedMessage.substring(0, 1))) {
                            System.out.println("x");
                            System.out.println(client.clientArrayList);
                            System.out.println(listenedMessage);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void add(Client client, String name) {
        DatabaseOperations db = new DatabaseOperations();
        if (client.clientArrayList.size() == 0 && db.checkUsernameExistence(name)) {
            client.clientArrayList.add(name);
            System.out.println(client.clientArrayList);
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

    public static void main(String[] args) throws IOException {
        DatabaseOperations databaseOperations = new DatabaseOperations();
        Scanner sc = new Scanner(System.in);
        System.out.println("Welcome to our chat application!");
        System.out.println("SIGN UP and SIGN IN -> Sign up or Sign in with a proper nickname and password to join chat.\t");
        String listenCommand = sc.nextLine();
        while (true) {
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
                databaseOperations.addNewUser(user);
                client.listenForMessage(client, user);
                client.sendMessageToOtherUser(user, client);
            } else if (listenCommand.equals("SIGN IN")) {
                System.out.println("Enter your nickname: ");
                String userName = sc.nextLine();
                System.out.println("Enter your password: ");
                String password = sc.nextLine();
                if (databaseOperations.authenticationForSignIn(userName, password)) {
                    User user = new User(userName, password, 1, "empty", 0);
                    databaseOperations.changeStatusAsOnline(user.getUserName());
                    Socket socket = new Socket("localhost", 5555);
                    Client client = new Client(socket, userName);
                    client.listenForMessage(client, user);
                    client.sendMessageToOtherUser(user, client);
                } else {
                    System.out.println("Incorrect nickname or password. Please try again.");
                }
            } else {
                System.out.println("Please SIGN IN our SIGN OUT to use our chat application.");
                listenCommand = sc.nextLine();
            }
        }
    }
}
