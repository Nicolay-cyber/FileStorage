package com.dnn.clientapp;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Scanner;

public class RequestWorker {
    Request request;
    ChannelHandlerContext future;

    String storagePath = "C:\\Users\\Николай\\IdeaProjects\\FileStorage\\clientApp\\src\\storage\\";

    public RequestWorker(ChannelHandlerContext future) {
            this.future = future;

            System.out.println("? - help");
            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.print("> ");
                String cmd = scanner.nextLine();
                String[] s = cmd.split(" ");
                try {
                    work(s);
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.out.println("The command isn't complete");
                }
            }
    }


    private void work(String[] s) {
        switch (s[0]) {
            case"?":{
                System.out.println(
                        ""
                );
                break;
            }
            case "quit": {
                System.exit(1);
                break;
            }
            case "hi": {
                request = new Request("Hi");
                future.channel().writeAndFlush(request);
                break;
            }
            case "getFile": {
                request = new Request("Get file", s[1]);
                future.channel().writeAndFlush(request);
                break;
            }
            case "sendFile": {
                byte[] buffer = new byte[1024 * 512];
                try (RandomAccessFile accessFile = new RandomAccessFile(storagePath + s[1], "r")) {
                    while (true) {
                        request = new Request("Receive file", s[1]);
                        request.setPosition(accessFile.getFilePointer());
                        int read = accessFile.read(buffer);
                        if (read < buffer.length - 1) {
                            byte[] tempBuffer = new byte[read];
                            System.arraycopy(buffer, 0, tempBuffer, 0, read);
                            request.setFile(tempBuffer);
                            future.channel().writeAndFlush(request);
                            break;
                        } else {
                            request.setFile(buffer);
                            future.channel().writeAndFlush(request);
                        }
                        buffer = new byte[1024 * 512];
                    }
                } catch (FileNotFoundException e) {
                    System.out.println("File isn't found");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
            default:
                System.out.println("Unknown command");
        }
    }
}

