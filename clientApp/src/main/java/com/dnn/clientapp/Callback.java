package com.dnn.clientapp;

import java.io.IOException;

public interface Callback {
    void callback(Object... args) throws IOException;
}
