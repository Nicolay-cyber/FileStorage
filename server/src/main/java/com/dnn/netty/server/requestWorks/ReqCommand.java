package com.dnn.netty.server.requestWorks;

import com.dnn.netty.server.responseWorks.ResCommand;

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

    ADD_FOLDER,
    GO_TO_THE_FOLDER,

    QUIT,
    TO_THE_PREVIOUS_FOLDER,

    UNKNOWN_REQUEST,
}
