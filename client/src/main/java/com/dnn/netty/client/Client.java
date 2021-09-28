package com.dnn.netty.client;

import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws InterruptedException {
        ServerConnection sc = new ServerConnection();
        if(sc.isConnectionReady())
            System.out.println("!!!");
            while (true){
                System.out.println("Message to Server:");
                Scanner scanner = new Scanner(System.in);
                sc.sendMsg(scanner.nextLine());
        }
    }
}
