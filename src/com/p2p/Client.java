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
        this.socket = socket;
        this.userNama = userNama;
        this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    public void sendMsg() throws IOException {
        bufferedWriter.write(userNama);
        bufferedWriter.newLine();
        bufferedWriter.flush();

        Scanner sc = new Scanner(System.in);
        while(socket.isConnected()){
            String msg = sc.nextLine();
            bufferedWriter.write(userNama + " > ");
            bufferedWriter.newLine();
            bufferedWriter.flush();
        }
    }

    public void getMsg(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String msg = null;
                try {
                    msg = bufferedReader.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println(msg);
            }
        }).start();
    }

    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.println("Username: ");
        String userName = sc.nextLine();
        System.out.println(userName);
        Socket socket = new Socket("localhost", 5555);
        System.out.println("debug-client: socket");
        Client client = new Client(socket, userName);
        System.out.println("debug-client: client");
        client.getMsg();
        System.out.println("debug-client: getMsg");
        client.sendMsg();
        System.out.println("debug-client: sendMsg");
    }
}
