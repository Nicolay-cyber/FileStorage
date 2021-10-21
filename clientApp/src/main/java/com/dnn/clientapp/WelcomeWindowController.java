package com.dnn.clientapp;

import com.dnn.clientapp.network.Network;
import com.dnn.clientapp.network.Request;
import com.dnn.clientapp.network.Response;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

import java.io.IOException;

public class WelcomeWindowController {
    public TextField loginField;
    public PasswordField passwordField;
    @FXML
    private Text infoLine;
    private Network network;
    private String nickname;


    public void SingInBtmAction(ActionEvent actionEvent) {
        String pass = passwordField.getText();
        String login = loginField.getText();

        if (pass.equals("") || login.equals("")) {
            infoLine.setText("Fill login and password fields");
        } else {
            infoLine.setText("Data is sending...");
            network.sendMsg(
                    new Request(
                            Request.CHECK_USER,
                            loginField.getText() + " " + passwordField.getText()
                    )
            );
            loginField.clear();
            passwordField.clear();
        }
    }

    public void SingUpnBtmAction(ActionEvent actionEvent) {
        String pass = passwordField.getText();
        String login = loginField.getText();

        if (pass.equals("") || login.equals("")) {
            infoLine.setText("Fill login and password fields");
        } else {
            infoLine.setText("Data is sending...");
            network.sendMsg(
                    new Request(
                            Request.REGISTER,
                            loginField.getText() + " " + passwordField.getText()
                    )
            );
            loginField.clear();
            passwordField.clear();
        }
    }

    public void firstEntry(){
        new Thread(() -> {
            network = new Network();
            receiveResponse();
        }).start();
    }
    public void setNetwork(Network network){
        new Thread(()->{
            this.network = network;
            receiveResponse();
        }).start();
    }

    private boolean isLoginSuccessfully(String response) {
        return (response.equals(Response.USER_IS_EXIST) || response.equals(Response.USER_WAS_REGISTERED));
    }

    private void receiveResponse() {
        try {
            network.start((args) -> {
                nickname = ((Response) args[0]).getResComment();
                String response = ((Response) args[0]).getResponse();
                infoLine.setText(response);
                if (isLoginSuccessfully(response)) {
                    openMainClientWindow();
                }
            });

        } catch (InterruptedException e) {
            infoLine.setText("Server connection error");
        }
    }

    private void openMainClientWindow() {
        Platform.runLater(() -> {
            try {
                ((Stage) infoLine.getScene().getWindow()).close();
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("clientApp.fxml"));
                Stage stage = new Stage(StageStyle.DECORATED);
                stage.setScene(new Scene(fxmlLoader.load(), 400, 400));
                stage.setResizable(false);
                ClientAppController clientAppController = fxmlLoader.getController();
                clientAppController.setNetwork(network);
                clientAppController.setNickName(nickname);
                stage.setTitle("File Storage");
                stage.show();
                network.sendClientCtrl(clientAppController);
                clientAppController.work(Commands.SHOW_ALL_FILES.get());
                stage.setOnCloseRequest(clientAppController.getCloseEventHandler());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }

    private final javafx.event.EventHandler<WindowEvent> closeEventHandler = new javafx.event.EventHandler<WindowEvent>() {
        @Override
        public void handle(WindowEvent event) {
            network.fullDisconnect();
        }
    };

    public javafx.event.EventHandler<WindowEvent> getCloseEventHandler() {
        return closeEventHandler;
    }

}