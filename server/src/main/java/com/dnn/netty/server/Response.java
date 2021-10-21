package com.dnn.netty.server;

import java.util.ArrayList;

public class Response<T> {
    private String response;
    private String resComment;
    private long position;
    private byte[] file;
    private ArrayList<T> objects;

    public ArrayList<T> getObjects() {
        return objects;
    }

    public void setObjects(ArrayList<T> objects) {
        this.objects = objects;
    }

    final public static String USER_IS_EXIST = "User is exist";
    final public static String SUCCESS_WAS_REGISTERED = "User was registered successfully";
    final public static String RECEIVE_LIST_OF_FILES = "Receive list of files";

    public Response() {
    }

    public Response(String response, String resComment) {
        this.response = response;
        this.resComment = resComment;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getResComment() {
        return resComment;
    }

    public void setResComment(String resComment) {
        this.resComment = resComment;
    }

    public long getPosition() {
        return position;
    }

    public void setPosition(long position) {
        this.position = position;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }
}
