package com.dnn.netty.server;

public class Request {
    private String command;
    private String cmdComment;
    private byte[] file;
    private long position;

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

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }
}
