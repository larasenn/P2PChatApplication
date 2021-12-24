package com.p2p;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

//We implement Runnable because we want that instances will be executed by different thread
public class UserHandler implements Runnable{

    private Socket socket;
    private String userName;
    public static List<UserHandler> userList = new ArrayList<>();
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    public UserHandler(Socket socket) throws IOException {
        this.socket = socket;
        this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.userName =  this.bufferedReader.readLine();
        userList.add(this);
        carryMessage(userName + " entered the chat.");
        System.out.println(userList);
    }


//    public void configureUser(UserHandler user) throws IOException {
//        try {
//            getUserList(user);
//            carryMessage(userName + " entered the chat.");
//        } catch (IOException e){
//            e.printStackTrace();
//            stopProgram(socket, bufferedReader, bufferedWriter);
//        }
//
//
//    }

    public void carryMessage(String msg) throws IOException {
        for (UserHandler userHandler: userList){
            if (userHandler.userName!=userName){
                userHandler.bufferedWriter.write(msg);
                userHandler.bufferedWriter.newLine();
                userHandler.bufferedWriter.flush();
            } else{
                System.out.println("userName==userHandler.userName");
            }
        }
    }

    public void removeUserFromChat() throws IOException {
        userList.remove(this);
        carryMessage(userName + " left the chat.");
    }

    @Override
    public void run() {
        String clientMsg;

        while(socket.isConnected()){
            try {
                clientMsg = bufferedReader.readLine();
                carryMessage(clientMsg);
            } catch (IOException e) {
                try {
                    stopProgram(socket, bufferedReader, bufferedWriter);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                break;
            }

        }
    }

    public void stopProgram(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) throws IOException {
        removeUserFromChat();

        if(socket!=null)
            socket.close();

        if (bufferedWriter!=null)
            bufferedWriter.close();

        if (bufferedReader!=null)
            bufferedReader.close();
    }
}
