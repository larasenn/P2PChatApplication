package com.p2p;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private String userNama;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    public Client(Socket socket, String userNama) throws IOException {
        try{
            this.socket = socket;
            this.userNama = userNama;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e){
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public void sendMsg()  {
        try {
            bufferedWriter.write(userNama);
            bufferedWriter.newLine();
            bufferedWriter.flush();
            Scanner sc = new Scanner(System.in);
            while (socket.isConnected()) {
                String msg = sc.nextLine();
                bufferedWriter.write(userNama + " > " + msg);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        }catch (IOException e){
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public void listenForMessage(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String msg ;
                while(socket.isConnected()) {
                    try {
                        msg = bufferedReader.readLine();
                        System.out.println(msg);
                    } catch (IOException e) {
                        closeEverything(socket, bufferedReader, bufferedWriter);
                    }
                }
            }
        }).start();
    }
    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {

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

    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.println("Username: ");
        String userName = sc.nextLine();
        System.out.println(userName);
        Socket socket = new Socket("localhost", 5555);
        Client client = new Client(socket, userName);
        client.listenForMessage();
        client.sendMsg();
    }
}
