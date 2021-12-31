package com.p2p;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
public class ClientHandler implements Runnable{
    private Socket socket;
    private String clientUserName;
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    public ClientHandler(Socket socket) throws IOException {
        try{
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.clientUserName =  this.bufferedReader.readLine();
            clientHandlers.add(this);
            broadcastMessage("SERVER: " + clientUserName + " has entered to the chat");

        } catch (IOException e){
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }
    public void broadcastMessage(String msg)  {
        for (ClientHandler clientHandler : clientHandlers) {
            try {
                User user = new User(clientHandler.clientUserName);
                if ( !(clientHandler.clientUserName.equals(clientUserName)) && ( clientHandler.clientUserName.equals(user.getChatClientName()) )) {
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
        clientHandlers.remove(this);
        broadcastMessage("SERVER: " + clientUserName + " left the chat.");
    }

    @Override
    public void run() {
        String clientMsg; //gelen mesaj
        while(socket.isConnected()){
            try {
                clientMsg = bufferedReader.readLine();
                broadcastMessage(clientMsg);
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        removeClientHandler();
        try{
            if(socket!=null)
                socket.close();

            if (bufferedWriter!=null)
                bufferedWriter.close();

            if (bufferedReader!=null)
                bufferedReader.close();
        } catch (IOException e){
            e.printStackTrace();
        }

    }
}
