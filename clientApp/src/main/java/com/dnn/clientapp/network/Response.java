package com.dnn.clientapp.network;

import java.util.ArrayList;

public class Response<T> {
    final public static String USER_IS_EXIST = "User is exist";
    final public static String USER_WAS_REGISTERED = "User was registered successfully";
    final public static String RECEIVE_LIST_OF_FILES = "Receive list of files";

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
