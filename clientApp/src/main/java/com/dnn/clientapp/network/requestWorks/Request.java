package com.dnn.clientapp.network.requestWorks;

public class Request {
    private ReqCommand command;
    private String cmdComment = "";
    private byte[] file;
    private long position;
    private String clientId;


    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public Request() {
    }

    public Request(ReqCommand command) {
        this.command = command;
    }

    public Request(ReqCommand command, String cmdComment) {
        this.command = command;
        this.cmdComment = cmdComment;
    }


    public long getPosition() {
        return position;
    }

    public void setPosition(long position) {
        this.position = position;
    }

    public String getCmdComment() {
        return cmdComment;
    }

    public void setCmdComment(String cmdComment) {
        this.cmdComment = cmdComment;
    }

    public ReqCommand getCommand() {
        return command;
    }

    public void setCommand(ReqCommand command) {
        this.command = command;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }
}
