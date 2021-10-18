module com.dnn.clientapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires io.netty.all;
    requires java.desktop;
    requires com.fasterxml.jackson.databind;


    opens com.dnn.clientapp to javafx.fxml;
    exports com.dnn.clientapp;
}