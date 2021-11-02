package com.dnn.clientapp.network.responseWorks;

import com.dnn.clientapp.ClientAppController;
import com.dnn.clientapp.network.requestWorks.ReqCommand;
import com.dnn.clientapp.network.requestWorks.Request;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import javafx.application.Platform;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class ResponseWorker extends SimpleChannelInboundHandler<Response<Object>> {
    private String storagePath;
    private Response res;
    private Callback callback;
    private ClientAppController clientCtrl;

    public ResponseWorker(Callback callback) {
        this.callback = callback;
    }

    public void setClientCtrl(ClientAppController clientCtrl) {
        this.clientCtrl = clientCtrl;
        Request request;
        request = new Request(ReqCommand.SHOW_ALL_FILES);
        request.setClientId(clientCtrl.getClientId());
    }

    public ResponseWorker() {
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Response res) {
        Platform.runLater(() -> {
            if (callback != null) {
                try {
                    callback.callback(res);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            this.res = res;
            System.out.println("Server's response:" + res.getResponse());
            try {
                work(res.getResponse());
            } catch (NullPointerException ignored) {
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void work(ResCommand response) {
        Platform.runLater(() -> {
            switch (response) {
                case NICKNAME_WAS_CHANGED:{
                    clientCtrl.serverResponseToSettingWindow(response);
                    clientCtrl.changeNickname(res.getResComment());
                    break;
                }
                case LOGIN_WAS_CHANGED:
                {
                    clientCtrl.serverResponseToSettingWindow(response);
                    clientCtrl.changeLogin(res.getResComment());
                    break;
                }
                case PASSWORD_WAS_NOT_CHANGED:
                case LOGIN_IS_ALREADY_TAKEN:
                case LOGIN_WAS_NOT_CHANGED_DOU_TO_ERROR:
                case NICKNAME_WAS_NOT_CHANGED:
                case PASSWORD_WAS_CHANGED:{
                    clientCtrl.serverResponseToSettingWindow(response);
                    break;
                }
                case USER_HAS_EXISTED: {
                    break;
                }
                case FOLDER_WAS_ADDED_SUCCESSFULLY:
                case FILE_WAS_DELETED_SUCCESSFULLY:
                case FILE_WAS_RECEIVED_SUCCESSFULLY:
                {
                    clientCtrl.work(ReqCommand.SHOW_ALL_FILES);
                    break;
                }
                case RECEIVE_LIST_OF_FILES: {

                    clientCtrl.getFileArea().getChildren().clear();
                    for (Object fileName : res.getObjects()) {
                        if(String.valueOf(fileName).contains(".")){
                            clientCtrl.addFileToFileArea(String.valueOf(fileName));
                        }
                        else{
                            clientCtrl.addFolderToFilesArea(String.valueOf(fileName));
                        }
                    }
                    if (res.getObjects().isEmpty()) {
                        clientCtrl.showDefaultTextInFileArea();
                    }
                    break;
                }
                case USER_IS_EXIST:
                case SUCCESSFUL_REGISTRATION: {
                    System.out.println("Welcome");
                    break;
                }
                case RECEIVE_FILE:{
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.setTitle("Save file");
                    fileChooser.setInitialFileName(res.getResComment());

                    File file = fileChooser.showSaveDialog(clientCtrl.getFileArea().getScene().getWindow());
                    if (file != null) {
                        storagePath = file.getAbsolutePath();
                        try (RandomAccessFile accessFile = new RandomAccessFile(storagePath, "rw")) {
                            accessFile.seek(res.getPosition());
                            accessFile.write(res.getFile());
                            clientCtrl.addText("File " + res.getResComment() + " is received successfully");
                            System.out.printf("File %s is received%n", res.getResComment());
                        } catch (Exception e) {
                            e.printStackTrace();
                            clientCtrl.addText("Exception while receiving of the file " + res.getResComment());
                            System.out.println("Exception while receiving of the file" + res.getResComment());
                        }
                    }
                    break;
                }
                case HI: {
                    clientCtrl.addText("The server greeted you");
                    System.out.println("The server greeted you");
                    break;
                }
                default:
                    break;
            }
        });
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Platform.runLater(()->
        clientCtrl.lostConnection());
    }
}
