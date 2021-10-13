package com.dnn.netty.client;

public class Request {
    private String command;
    private String filename;
    private byte[] file;
    private long position;

    public Request() {
    }

    public Request(String command) {
        this.command = command;
    }

    public Request(String command, String filename) {
        this.command = command;
        this.filename = filename;
    }

    public long getPosition() {
        return position;
    }

    public void setPosition(long position) {
        this.position = position;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
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
