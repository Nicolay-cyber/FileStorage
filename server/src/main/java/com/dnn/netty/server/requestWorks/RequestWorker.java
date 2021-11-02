package com.dnn.netty.server.requestWorks;

import com.dnn.netty.server.Database;
import com.dnn.netty.server.responseWorks.ResCommand;
import com.dnn.netty.server.responseWorks.Response;
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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class RequestWorker extends SimpleChannelInboundHandler<Request> {
    private Request req;
    private Response response;
    private String clientId;
    private ChannelHandlerContext ctx;
    private String storagePath ;
   // "C:\\Users\\Николай\\IdeaProjects\\FileStorage\\server\\src\\storage\\";

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws IOException {
        storagePath = new File("server/src/storage").getCanonicalPath() + "\\";
        System.out.println("New channel active");
        this.ctx = ctx;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {

        System.out.println(clientId + " is inactive");
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Request req) throws Exception {
        if (clientId == null) {
            this.clientId = req.getClientId();
            storagePath += clientId + "\\";
        }
        this.req = req;
        response = new Response();
        ReqCommand reqCommand = req.getCommand();
        System.out.printf("Client %s sent command: %s%nResult: ", clientId, reqCommand);
        work(reqCommand);
        ctx.writeAndFlush(response);
    }

    private void work(ReqCommand reqCommand) throws IOException {

        switch (reqCommand) {
            case SHOW_ALL_FILES: {
                File dir = new File(storagePath);
                File[] arrFiles = dir.listFiles();

                ArrayList<String> fileList = new ArrayList<>();
                for (File file : arrFiles) {
                    fileList.add(file.getName());
                }
                response.setResponse(ResCommand.RECEIVE_LIST_OF_FILES);

                response.setObjects(fileList);
                break;
            }
            case ADD_FOLDER: {
                File theDir = new File(storagePath + req.getCmdComment());
                if (!theDir.exists()) {
                    theDir.mkdirs();
                }
                response.setResponse(ResCommand.FOLDER_WAS_ADDED_SUCCESSFULLY);
                break;
            }
            case GO_TO_THE_FOLDER: {
                storagePath += req.getCmdComment() + "\\";
                work(ReqCommand.SHOW_ALL_FILES);
                break;
            }
            case TO_THE_PREVIOUS_FOLDER: {

                int slash = storagePath.lastIndexOf("\\", storagePath.length() - 2);
                String lastFolder = storagePath.substring(slash + 1, storagePath.length() - 1);
                if (!lastFolder.equals(clientId)) {
                    storagePath = storagePath.substring(0, slash + 1);
                }
                work(ReqCommand.SHOW_ALL_FILES);
                break;
            }
            case CHANGE_LOGIN: {
                String sameLogin = Database.checkLogin(req.getCmdComment());
                if (sameLogin == null) {
                    if (Database.changeLogin(req.getClientId(), req.getCmdComment())) {
                        response.setResponse(ResCommand.LOGIN_WAS_CHANGED);
                        response.setResComment(req.getCmdComment());
                    } else
                        response.setResponse(ResCommand.LOGIN_WAS_NOT_CHANGED_DOU_TO_ERROR);
                } else {
                    response.setResponse(ResCommand.LOGIN_IS_ALREADY_TAKEN);
                }
                break;
            }
            case CHANGE_NICKNAME: {
                if (Database.changeNickname(req.getClientId(), req.getCmdComment())) {
                    response.setResponse(ResCommand.NICKNAME_WAS_CHANGED);
                    response.setResComment(req.getCmdComment());
                } else
                    response.setResponse(ResCommand.NICKNAME_WAS_NOT_CHANGED);
                break;
            }

            case CHANGE_PASSWORD: {
                String[] oldAndNewPasswords = req.getCmdComment().split(" ");
                if (Database.changePassword(req.getClientId(), oldAndNewPasswords[0], oldAndNewPasswords[1])) {
                    response.setResponse(ResCommand.PASSWORD_WAS_CHANGED);
                } else {
                    response.setResponse(ResCommand.PASSWORD_WAS_NOT_CHANGED);
                }
                break;
            }
            case QUIT: {
                ctx.close();
                break;
            }
            case DELETE_FILE: {
                File file = new File(storagePath + req.getCmdComment());
                if (file.delete()) {
                    response.setResponse(ResCommand.FILE_WAS_DELETED_SUCCESSFULLY);
                } else {
                    recursiveDelete(file);
                    response.setResponse(ResCommand.FILE_WAS_DELETED_SUCCESSFULLY);

                }
                break;
            }
            case CHECK_USER: {
                String[] cmdComment = req.getCmdComment().split(" ");
                String ID = Database.getId(cmdComment[0], cmdComment[1]);
                if (ID == null) {
                    response.setResponse(ResCommand.WRONG_LOGIN_OR_PASSWORD);
                } else {
                    this.clientId = ID;
                    response.setResponse(ResCommand.USER_IS_EXIST);
                    response.setResComment(ID + " " + Database.getNickname(ID));
                    storagePath += ID + "\\";
                }
                break;
            }
            case REGISTER: {
                String[] cmdComment = req.getCmdComment().split(" ");
                String ID = Database.registerUser(cmdComment[0], cmdComment[1]);
                if (ID != null) {
                    this.clientId = ID;
                    Files.createDirectory(Path.of(storagePath + ID));
                    response.setResponse(ResCommand.SUCCESSFUL_REGISTRATION);
                    response.setResComment(ID + " " + Database.getNickname(ID));
                    break;
                } else {
                    response.setResponse(ResCommand.USER_HAS_EXISTED);
                }
                break;
            }
            case GET_FILE: {
                String filename = req.getCmdComment();
                byte[] buffer = new byte[1024 * 512];
                try (RandomAccessFile accessFile = new RandomAccessFile(storagePath + filename, "r")) {
                    while (true) {
                        response.setResponse(ResCommand.RECEIVE_FILE);
                        response.setResComment(filename);
                        response.setPosition(accessFile.getFilePointer());
                        int read = accessFile.read(buffer);
                        if (read < 0) {
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
                    response.setResponse(ResCommand.FILE_WAS_NOT_FOUND);
                }
                break;
            }
            case HI: {
                response.setResponse(ResCommand.HI);
                break;
            }
            case RECEIVE_FILE: {
                try (RandomAccessFile accessFile = new RandomAccessFile(storagePath + req.getCmdComment(), "rw")) {
                    accessFile.seek(req.getPosition());
                    accessFile.write(req.getFile());
                    response.setResponse(ResCommand.FILE_WAS_RECEIVED_SUCCESSFULLY);
                } catch (Exception e) {
                    response.setResponse(ResCommand.EXCEPTION_WHILE_RECEIVING_OF_THE_FILE);
                }
                break;
            }
            case FILE_HAD_SENT_FULLY: {
                work(ReqCommand.SHOW_ALL_FILES);
                break;
            }
            default: {
                response.setResponse(ResCommand.UNKNOWN_REQUEST);
            }
        }
        System.out.println(response.getResponse());
    }
    public static void recursiveDelete(File file) {
        // до конца рекурсивного цикла
        if (!file.exists())
            return;

        //если это папка, то идем внутрь этой папки и вызываем рекурсивное удаление всего, что там есть
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                // рекурсивный вызов
                recursiveDelete(f);
            }
        }
        // вызываем метод delete() для удаления файлов и пустых(!) папок
        file.delete();
        System.out.println("Удаленный файл или папка: " + file.getAbsolutePath());
    }



    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.out.println(clientId + "'s exception");
        cause.printStackTrace();
    }
}
