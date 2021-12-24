package com.p2p;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

//Responsible for listening the client, and then spawn a new thread to handle
public class Server {
    private ServerSocket serverSocket; // listening incoming connections/clients and creating a socket object to communicate with these connections/clients

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void runServer() throws IOException {
        while(!serverSocket.isClosed()){
               Socket socket = serverSocket.accept();
               System.out.println("A new user connected.");
               UserHandler userHandler = new UserHandler(socket);
               Thread thread = new Thread(userHandler);
               thread.start();
            System.out.println("debugdebug");
        }
    }

    public void shutDownServerSocket() throws IOException {
        if(serverSocket == null){
            IOException exception = new IOException();
            exception.printStackTrace();
        } else {
            serverSocket.close();
        }
    }

    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = new ServerSocket(5555);
        System.out.println("debug: serverSocket");
        Server server = new Server(serverSocket);
        System.out.println("debug: server");
        server.runServer();
    }
}
