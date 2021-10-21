package com.dnn.clientapp;

public enum Commands {
    INFO("?"),
    SHOW_ALL_FILES("showFiles"),
    HI("hi"),
    GET_FILE ("getFile"),
    DELETE ("delete"),
    SEND_FILE ("sendFile"),
    QUIT ("quit"),
    UNKNOWN_REQUEST("");

    private final String command;

    public static Commands get(String s) {
        for (Commands c : values()) {
            if (c.command.equals(s)) {
                return c;
            }
        }
        return UNKNOWN_REQUEST;
    }
    public String get(){
        return this.command;
    }

    Commands(String command) {
        this.command = command;
    }
}
