package com.dnn.clientapp;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import javafx.application.Application;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.io.RandomAccessFile;

public class ResponseWorker extends SimpleChannelInboundHandler<Response> {
    String storagePath = "C:\\Users\\Николай\\IdeaProjects\\FileStorage\\client\\src\\storage\\";
    Response res;
    ChannelHandlerContext ctx;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Response res) throws Exception {
        this.ctx = ctx;
        System.out.println();
        this.res = res;
        System.out.println("Server's response: " + res.getResponse());
        try{
            work(res.getResponse());
        }catch (NullPointerException ignored){
        }
        System.out.print("> ");
    }
    private void work(String response) throws Exception {
        switch (response){
            case "Unknown user":
            case "User has existed":{
                new LoginWindow(ctx);
                break;
            }

            case "User is exist" :
            case "User was registered successfully" : {
                new Thread(() -> new RequestWorker(ctx)).start();
                System.out.println("All right!");
                break;
            }
            case "Receive file":{
                try(RandomAccessFile accessFile = new RandomAccessFile(storagePath + "" + res.getResComment(), "rw")) {
                    accessFile.seek(res.getPosition());
                    accessFile.write(res.getFile());
                    System.out.printf("File %s is received%n", res.getResComment());
                }
                catch (Exception e){
                    System.out.println("Exception while receiving of the file " + res.getResComment());
                }
                break;
            }
            case "Hi":{
                System.out.println("The server greeted you");
                break;
            }
        }
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        new LoginWindow(ctx);
    }

/*
    public void setFuture(ChannelFuture future) {
        this.future = future;
    }
*/
}
