package com.dnn.clientapp.network.requestWorks;

public enum ReqCommand {
    CHECK_USER,
    REGISTER,

    SHOW_ALL_FILES,

    CHANGE_PASSWORD,
    CHANGE_LOGIN,
    CHANGE_NICKNAME,

    INFO,
    HI,

    GET_FILE,
    SEND_FILE,
    DELETE_FILE,
    RECEIVE_FILE,
    FILE_HAD_SENT_FULLY,
    CHECK_SEND,
    QUIT,

    ADD_FOLDER,
    GO_TO_THE_FOLDER,
    UNKNOWN_REQUEST,
    TO_THE_PREVIOUS_FOLDER,
}
