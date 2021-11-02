package com.dnn.clientapp.network.responseWorks;

import java.io.IOException;

public interface Callback {
    void callback(Object... args) throws IOException;
}
