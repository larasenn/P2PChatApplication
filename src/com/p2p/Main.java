package com.p2p;

import com.p2p.repository.DatabaseOperations;
import com.p2p.service.Client;
import com.p2p.service.User;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Main {
    private static DatabaseOperations databaseOperations = new DatabaseOperations();
    private static Scanner scanner = new Scanner(System.in);
    private static Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) throws IOException {//Main method responsible when program first runs.
        System.out.println("Welcome to our chat application!");
        System.out.println("SIGN UP and SIGN IN -> Sign up or Sign in with a proper nickname and password to join chat.");
        FileHandler fileHandler = new FileHandler("C:\\Users\\rojen\\eclipse-workspace\\P2PChatApplication\\src\\com\\p2p\\output.log"); //Logging to file.
        SimpleFormatter simpleFormatter = new SimpleFormatter();
        fileHandler.setFormatter(simpleFormatter);
        logger.addHandler(fileHandler);
        logger.info("This is logger info.");
        String listenCommand = scanner.nextLine();
        while (true) {
            if (listenCommand.equals("SIGN UP")) {
                signUp();
            } else if (listenCommand.equals("SIGN IN")) {
                signIn();
            } else if (listenCommand.equals("EXIT")) {
                break;
            } else {
                System.out.println("Please SIGN IN our SIGN OUT to use our chat application.");
                listenCommand = scanner.nextLine();
            }
        }
    }

    public static void signIn() throws IOException {//Sign in method to authorize users.
        System.out.println("Enter your nickname: ");
        String userName = scanner.nextLine();
        System.out.println("Enter your password: ");
        String password = scanner.nextLine();
        if (databaseOperations.authenticationForSignIn(userName, password)) {
            User user = new User(userName, password, 1, 0);
            databaseOperations.changeStatusAsOnline(user.getUserName());
            Socket socket = new Socket("localhost", 7777);
            Client client = new Client(socket, userName);
            client.listenForMessage(client, user, logger);
            client.sendMessageToOtherUser(user, logger);
        } else {
            System.out.println("Incorrect nickname or password. Please try again.");
        }
    }

    public static void signUp() throws IOException {//Sign up method for unregistered users.
        System.out.println("Enter your nickname: ");
        String userName = scanner.nextLine();
        while (userName.equals(databaseOperations.checkUsernameDuplication(userName))) {
            System.out.println("This username has already been taken, please enter another nickname: ");
            userName = scanner.nextLine();
        }
        System.out.println("Enter your password: ");
        String password = scanner.nextLine();
        Socket socket = new Socket("localhost", 7777);
        Client client = new Client(socket, userName);
        User user = new User(userName, password, 1, 0);
        databaseOperations.addNewUser(user);
        client.listenForMessage(client, user, logger);
        client.sendMessageToOtherUser(user, logger);
    }
}
