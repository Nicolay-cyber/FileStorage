package com.dnn.netty.server.responseWorks;

import java.util.ArrayList;

public class Response<T> {
    private ResCommand response;
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


    public Response() {
    }

    public Response(ResCommand response, String resComment) {
        this.response = response;
        this.resComment = resComment;
    }

    public ResCommand getResponse() {
        return response;
    }

    public void setResponse(ResCommand response) {
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
