package com.dnn.clientapp.network;

import com.dnn.clientapp.Callback;
import com.dnn.clientapp.ClientAppController;
import com.dnn.clientapp.Commands;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import javafx.application.Platform;

import java.io.RandomAccessFile;

public class ResponseWorker extends SimpleChannelInboundHandler<Response> {
    private String storagePath = "C:\\Users\\Николай\\IdeaProjects\\FileStorage\\clientApp\\src\\storage\\";
    private Response res;
    private ChannelHandlerContext ctx;
    private Callback callback;
    private ClientAppController clientCtrl;
    public ResponseWorker(Callback callback) {
        this.callback = callback;
    }

    public void setClientCtrl(ClientAppController clientCtrl) {
        this.clientCtrl = clientCtrl;
        Request request;
        request = new Request(Request.SHOW_ALL_FILES);
        request.setNickname(clientCtrl.getNickName());
        //ctx.writeAndFlush(request);
    }

    public ResponseWorker() {
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Response res) throws Exception {
        if (callback != null) {
            callback.callback(res);
        }
        this.res = res;
        System.out.println("Server's response: " + res.getResponse());
        try {
            work(res.getResponse());
        } catch (NullPointerException ignored) {
        }
    }

    private void work(String response) throws Exception {
        switch (response) {
            case "Unknown user":
            case "User has existed": {
                break;
            }
            case "File was received successfully":
            case "File was deleted successfully":{
                clientCtrl.work(Commands.SHOW_ALL_FILES.get());
                break;
            }
            case Response.RECEIVE_LIST_OF_FILES:{
                Platform.runLater(() -> {
                    clientCtrl.fileArea.getChildren().clear();
                    for (Object fileName: res.getObjects()){
                        clientCtrl.addFileToFileArea(String.valueOf(fileName));
                    }
                    if (res.getObjects().isEmpty()){
                        clientCtrl.showDefaultTextInFileArea();
                    }
                });
                break;
            }
            case Response.USER_IS_EXIST:
            case Response.USER_WAS_REGISTERED: {
                System.out.println("Welcome");
                break;
            }
            case "Receive file": {
                try (RandomAccessFile accessFile = new RandomAccessFile(storagePath + res.getResComment(), "rw")) {
                    accessFile.seek(res.getPosition());
                    accessFile.write(res.getFile());
                    clientCtrl.addText("File", res.getResComment(), "is received successfully");
                    System.out.printf("File %s is received%n", res.getResComment());
                } catch (Exception e) {
                    e.printStackTrace();
                    clientCtrl.addText("Exception while receiving of the file", res.getResComment());
                    System.out.println("Exception while receiving of the file" + res.getResComment());
                }
                break;
            }
            case "Hi": {
                clientCtrl.addText("The server greeted you", res.getResComment());
                System.out.println("The server greeted you");
                break;
            }
            default:break;
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
    }

}
