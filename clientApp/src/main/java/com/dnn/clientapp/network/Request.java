package com.dnn.clientapp.network;

public class Request {
    private String command = "";
    private String cmdComment = "";
    private byte[] file;
    private long position;
    private String nickname;
    final public static String CHECK_USER = "Check user";
    final public static String REGISTER = "Register user";
    final public static String HI = "Hi";
    final public static String QUIT = "Quit";
    final public static String SHOW_ALL_FILES = "Show all files";
    final public static String DELETE = "Delete";

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Request() {
    }

    public Request(String command) {
        this.command = command;
    }

    public Request(String command, String cmdComment) {
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