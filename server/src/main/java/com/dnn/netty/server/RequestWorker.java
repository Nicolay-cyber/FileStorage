package com.dnn.netty.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class RequestWorker extends SimpleChannelInboundHandler<Request> {
    private Request req;
    private Response response;
    private String nickname;
    private ChannelHandlerContext ctx;

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("New channel active");
        this.ctx = ctx;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        System.out.println(nickname + " is inactive");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Request req) throws Exception {
        this.req = req;
        this.nickname = req.getNickname();
        response = new Response();
        String reqCommand = req.getCommand();
        System.out.printf("Client %s sent command: %s%nResult: ", nickname, reqCommand);
        work(reqCommand);
        ctx.writeAndFlush(response);
    }

    private void work(String reqCommand) throws IOException {
        String storagePath = "C:\\Users\\Николай\\IdeaProjects\\FileStorage\\server\\src\\storage\\";
        switch (reqCommand) {
            case Request.SHOW_ALL_FILES:{
                ArrayList<String> fileList = new ArrayList<>();
                try (Stream<Path> pathStream = Files.walk(Paths.get(storagePath + nickname))) {
                    pathStream
                            .forEach(x -> fileList.add(String.valueOf(x.getFileName())));
                }
                fileList.remove(0);
                response.setResponse(Response.RECEIVE_LIST_OF_FILES);
                response.setObjects(fileList);
                break;
            }
            case Request.QUIT: {
                ctx.close();
                break;
            }
            case "Delete": {
                File file = new File(storagePath + nickname + "\\" + req.getCmdComment());
                if (file.delete()) {
                    response.setResponse("File was deleted successfully");
                } else {
                    response.setResponse("Error deleting files");
                }

                //work(Request.SHOW_ALL_FILES);
                break;
            }
            case Request.CHECK_USER: {
                String[] cmdComment = req.getCmdComment().split(" ");
                String nickname = Database.getNickByLoginAndPass(cmdComment[0], cmdComment[1]);
                if (nickname == null) {
                    response.setResponse("Wrong login or password");
                } else {
                    this.nickname = nickname;
                    response.setResponse("User is exist");
                    response.setResComment(nickname);
                }
                break;
            }
            case Request.REGISTER: {
                String[] cmdComment = req.getCmdComment().split(" ");
                String nickname = Database.registerUser(cmdComment[0], cmdComment[1]);
                if (nickname != null) {
                    this.nickname = nickname;
                    Files.createDirectory(Path.of(storagePath + nickname));
                    response.setResponse("User was registered successfully");
                    response.setResComment(nickname);
                    break;
                } else {
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
                        if(read < 0){
                            response.setFile(new byte[0]);
                            break;
                        }
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
                    response.setResponse("File wasn't found");
                }
                break;
            }
            case Request.HI: {
                response.setResponse("Hi");
                break;
            }
            case "Receive file": {
                try (RandomAccessFile accessFile = new RandomAccessFile(storagePath + nickname + "\\" + req.getCmdComment(), "rw")) {
                    accessFile.seek(req.getPosition());
                    accessFile.write(req.getFile());
                    response.setResponse("File was received successfully");
                } catch (Exception e) {
                    response.setResponse("Exception while receiving of the file " + req.getCmdComment());
                }
                break;
            }
            default: {
                response.setResponse("Unknown request's type");
            }
        }
        System.out.println(response.getResponse());
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.out.println(nickname + "'s exception");
        cause.printStackTrace();
    }
}
