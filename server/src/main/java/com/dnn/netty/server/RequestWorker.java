package com.dnn.netty.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.FileNotFoundException;
import java.io.RandomAccessFile;

public class RequestWorker extends SimpleChannelInboundHandler<Request> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Request req) throws Exception {
        Response response = new Response();
        String reqCommand = req.getCommand();
        System.out.println("Clint sent message: " + reqCommand);
        switch (reqCommand){
            case "Get file":{
                String filename = req.getFilename();
                String path = "C:\\Users\\Николай\\IdeaProjects\\FileStorage\\server\\src\\storage\\";
                byte[] buffer = new byte[1024 * 512];
                try (RandomAccessFile accessFile = new RandomAccessFile(path + filename, "r")) {
                    while (true) {
                        response.setResponse("Receive file");
                        response.setFilename(filename);
                        response.setPosition(accessFile.getFilePointer());
                        int read = accessFile.read(buffer);
                        if (read < buffer.length - 1) {
                            byte[] tempBuffer = new byte[read];
                            System.arraycopy(buffer, 0, tempBuffer, 0, read);
                            response.setFile(tempBuffer);
                            //ctx.writeAndFlush(response);
                            break;
                        } else {
                            response.setFile(buffer);
                            //ctx.writeAndFlush(response);
                        }
                        buffer = new byte[1024 * 512];
                    }
                    System.out.println("File " + filename + " is sent");
                }catch (FileNotFoundException e){
                    System.out.println("File " + filename + " isn't found");
                    response.setResponse("File wasn't found");
                }
                break;
            }
            case "Hi":{
                response.setResponse("Hi");
                //ctx.writeAndFlush(response);
                break;
            }
            default:{
                System.out.println("Unknown request's type");
            }
        }
        ctx.writeAndFlush(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }
}
