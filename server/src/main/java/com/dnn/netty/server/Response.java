package com.dnn.netty.server;

public class Response {
    private String response;
    private String resComment;
    private long position;
    private byte[] file;

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
