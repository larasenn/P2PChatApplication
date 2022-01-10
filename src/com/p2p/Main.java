package com.p2p;

import com.p2p.repository.DatabaseOperations;
import com.p2p.service.Client;
import com.p2p.service.User;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Main {
    private static DatabaseOperations databaseOperations = new DatabaseOperations();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws IOException {
        System.out.println("Welcome to our chat application!");
        System.out.println("SIGN UP and SIGN IN -> Sign up or Sign in with a proper nickname and password to join chat.");
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

    public static void signIn() throws IOException {
        System.out.println("Enter your nickname: ");
        String userName = scanner.nextLine();
        System.out.println("Enter your password: ");
        String password = scanner.nextLine();
        if (databaseOperations.authenticationForSignIn(userName, password)) {
            User user = new User(userName, password, 1,  0);
            databaseOperations.changeStatusAsOnline(user.getUserName());
            Socket socket = new Socket("localhost", 5555);
            Client client = new Client(socket, userName);
            client.listenForMessage(client, user);
            client.sendMessageToOtherUser(user);
        } else {
            System.out.println("Incorrect nickname or password. Please try again.");
        }
    }

    public static void signUp() throws IOException {
        System.out.println("Enter your nickname: ");
        String userName = scanner.nextLine();
        while (userName.equals(databaseOperations.checkUsernameDuplication(userName))) {
            System.out.println("This username has already been taken, please enter another nickname: ");
            userName = scanner.nextLine();
        }
        System.out.println("Enter your password: ");
        String password = scanner.nextLine();
        Socket socket = new Socket("localhost", 5555);
        Client client = new Client(socket, userName);
        User user = new User(userName, password, 1,  0);
        databaseOperations.addNewUser(user);
        client.listenForMessage(client, user);
        client.sendMessageToOtherUser(user);
    }
}
