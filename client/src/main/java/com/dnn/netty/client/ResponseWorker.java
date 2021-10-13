package com.dnn.netty.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.RandomAccessFile;

public class ResponseWorker extends SimpleChannelInboundHandler<Response> {
    String storagePath = "C:\\Users\\Николай\\IdeaProjects\\FileStorage\\client\\src\\storage\\";
    Response res;
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Response res) throws Exception {
        System.out.println();
        this.res = res;
        System.out.println("Server's response: " + res.getResponse());
        work(res.getResponse());
        System.out.print("> ");
    }
    private void work(String response){
        switch (response){
            case "Receive file":{
                try(RandomAccessFile accessFile = new RandomAccessFile(storagePath + "" + res.getFilename(), "rw")) {
                    accessFile.seek(res.getPosition());
                    accessFile.write(res.getFile());
                    System.out.println("File " + res.getFilename() + " is received");
                }
                catch (Exception e){
                    System.out.println("Exception while receiving of the file " + res.getFilename());
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
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }
}
