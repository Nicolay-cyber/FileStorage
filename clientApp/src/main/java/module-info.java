module com.dnn.clientapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires io.netty.all;
    requires java.desktop;
    requires com.fasterxml.jackson.databind;


    opens com.dnn.clientapp to javafx.fxml;
    exports com.dnn.clientapp;
    exports com.dnn.clientapp.network;
    opens com.dnn.clientapp.network to javafx.fxml;
    exports com.dnn.clientapp.network.responseWorks;
    opens com.dnn.clientapp.network.responseWorks to javafx.fxml;
    exports com.dnn.clientapp.network.requestWorks;
    opens com.dnn.clientapp.network.requestWorks to javafx.fxml;
}