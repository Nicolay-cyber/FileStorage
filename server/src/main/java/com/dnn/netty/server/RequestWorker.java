package com.dnn.netty.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RequestWorker extends SimpleChannelInboundHandler<Request> {
    private Map<String, Client> clients;
    private Request req;
    private Response response;
    private String nickname;

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        clients = new ConcurrentHashMap<>();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Request req) throws Exception {
        this.req = req;
        response = new Response();
        String reqCommand = req.getCommand();
        System.out.printf("Client sent command: %s%nResult: ", reqCommand);
        work(reqCommand);
        ctx.writeAndFlush(response);
    }

    private void work(String reqCommand) throws IOException {
        String storagePath = "C:\\Users\\Николай\\IdeaProjects\\FileStorage\\server\\src\\storage\\";
        switch (reqCommand) {
            case "Check user": {
                String[] cmdComment = req.getCmdComment().split(" ");
                String nickname = Database.getNickByLoginAndPass(cmdComment[0], cmdComment[1]);
                if (nickname == null) {
                    System.out.println("Unknown user");
                    response.setResponse("Unknown user");
                } else {
                    System.out.println("User is exist");
                    response.setResponse("User is exist");
                    response.setResComment(nickname);
                }
                break;
            }
            case "Register user": {
                String[] cmdComment = req.getCmdComment().split(" ");
                String nickname = Database.registerUser(cmdComment[0], cmdComment[1]);
                if (nickname != null) {
                    this.nickname = nickname;
                    response.setResponse("User was registered successfully");
                    response.setResComment(nickname);
                    System.out.printf("Client %s was registered successfully%n", nickname);
                    Files.createDirectory(Path.of(storagePath + nickname));
                    System.out.printf("%s's directory was created successfully", nickname);
                    break;
                } else {
                    System.out.println("User with such login has already existed");
                    response.setResponse("User has existed");
                }
                break;
            }
            case "Get file": {
                String filename = req.getCmdComment();
                byte[] buffer = new byte[1024 * 512];
                try (RandomAccessFile accessFile = new RandomAccessFile(storagePath + nickname + "\\" + filename, "r")) {
                    while (true) {
                        response.setResponse("Receive file");
                        response.setResComment(filename);
                        response.setPosition(accessFile.getFilePointer());
                        int read = accessFile.read(buffer);
                        if (read < buffer.length - 1) {
                            byte[] tempBuffer = new byte[read];
                            System.arraycopy(buffer, 0, tempBuffer, 0, read);
                            response.setFile(tempBuffer);
                            break;
                        } else {
                            response.setFile(buffer);
                        }
                        buffer = new byte[1024 * 512];
                    }
                    System.out.printf("File %s is sent%n", filename);
                } catch (FileNotFoundException e) {
                    System.out.printf("File %s isn't found%n", filename);
                    response.setResponse("File wasn't found");
                }
                break;
            }
            case "Hi": {
                response.setResponse("Hi");
                break;
            }
            case "Receive file": {
                try (RandomAccessFile accessFile = new RandomAccessFile(storagePath + nickname + "\\" + req.getCmdComment(), "rw")) {
                    accessFile.seek(req.getPosition());
                    accessFile.write(req.getFile());
                    System.out.printf("File %s is received%n", req.getCmdComment());
                    response.setResponse(String.format("File %s was received successfully", req.getCmdComment()));
                } catch (Exception e) {
                    System.out.println("Exception while receiving of the file " + req.getCmdComment());
                    response.setResponse("Exception while receiving of the file " + req.getCmdComment());
                }
                break;
            }
            default: {
                System.out.println("Unknown request's type");
                response.setResponse("Unknown request's type");
            }
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
    }
}
