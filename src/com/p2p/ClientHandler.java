package com.p2p;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler implements Runnable {
    private Socket socket;
    private String clientUserName;
    public static ArrayList<ClientHandler> clientHandlerArrayList = new ArrayList<>();
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String messageSentUser;

    public String getMessageSentUser() {
        return messageSentUser;
    }

    public void setMessageSentUser(String messageSentUser) {
        this.messageSentUser = messageSentUser;
    }

    public ClientHandler(Socket socket) throws IOException {
        try {
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.clientUserName = this.bufferedReader.readLine();
            clientHandlerArrayList.add(this);
            infoMessageFromOtherUsers("SERVER: " + clientUserName + " has entered to the chat");

        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public void infoMessageFromOtherUsers(String msg) {
        for (ClientHandler clientHandler : clientHandlerArrayList) {
            try {
                String otherUser = "";
                String otherUser2 = "";
              //  Client client = new Client(otherUser, otherUser2);
                if (!(clientHandler.clientUserName.equals(clientUserName)) /*&& ( (clientHandler.clientUserName.equals(client.getOtherUser())) || (clientHandler.clientUserName.equals(client.getOtherUser2())) ) */  ) {
                    clientHandler.bufferedWriter.write(msg);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    public void removeClientHandler() {
        clientHandlerArrayList.remove(this);
        infoMessageFromOtherUsers("SERVER: " + clientUserName + " left the chat.");
    }

    @Override
    public void run() {
        String messageFromUser;
        while (socket.isConnected()) {
            try {
                messageFromUser = bufferedReader.readLine();
                infoMessageFromOtherUsers(messageFromUser);
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        removeClientHandler();
        try {
            if (socket != null)
                socket.close();

            if (bufferedWriter != null)
                bufferedWriter.close();

            if (bufferedReader != null)
                bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
