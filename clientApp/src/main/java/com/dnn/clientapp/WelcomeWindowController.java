package com.dnn.clientapp;

import com.dnn.clientapp.network.*;
import com.dnn.clientapp.network.requestWorks.ReqCommand;
import com.dnn.clientapp.network.requestWorks.Request;
import com.dnn.clientapp.network.responseWorks.ResCommand;
import com.dnn.clientapp.network.responseWorks.Response;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.Objects;

public class WelcomeWindowController {
    public TextField loginField;
    public PasswordField passwordField;
    public Button signUpBtn;
    public Button signInBtn;
    @FXML
    private Text infoLine;
    private Network network;
    private String clientId;
    private String nickname;
    private String login;


    public void sendRequest(ActionEvent actionEvent) {
        String pass = passwordField.getText();
        String login = loginField.getText();
        ReqCommand req = ReqCommand.CHECK_USER;
        if (actionEvent.getSource() == signUpBtn) {
            req = ReqCommand.REGISTER;
        }
        focusAndSend(pass, login, req);
    }

    private void focusAndSend(String pass, String login, ReqCommand req) {
        infoLine.setFill(Color.BLACK);
        if (pass.equals("") || login.equals("")) {
            infoLine.setText("Fill login and password fields");
            if (login.equals("")) {
                loginField.requestFocus();
            } else {
                passwordField.requestFocus();
            }
        } else {
            infoLine.setText("Data is sending...");
            try {
                network.sendMsg(
                        new Request(
                                req,
                                loginField.getText() + " " + passwordField.getText()
                        )
                );
                this.login = loginField.getText();
                loginField.setText("");
                passwordField.setText("");
            } catch (NullPointerException e) {
                infoLine.setText("Server connection\nhasn't been ready yet");
                new Thread(() -> {
                    while (true) {
                        if (network.isConnectionReady()) {
                            infoLine.setText("Connection is ready");
                            focusAndSend(pass, login, req);
                            break;
                        }
                    }
                }).start();
            }
        }
    }

    public void firstEntry() {
        new Thread(() -> {
            try {
                network = new Network();
                receiveResponse();
            } catch (Exception e) {
                infoLine.setFill(Color.RED);
                infoLine.setText("Server connection error");
            }
        }).start();
    }

    public void setNetwork(Network network) {
        new Thread(() -> {
            try {
                this.network = network;
                receiveResponse();
            } catch (Exception e) {
                infoLine.setFill(Color.RED);
                infoLine.setText("Server connection error");
            }
        }).start();
    }

    private boolean isLoginSuccessfully(ResCommand response) {
        return (response.equals(ResCommand.USER_IS_EXIST) || response.equals(ResCommand.SUCCESSFUL_REGISTRATION));
    }

    private void receiveResponse() {
        try {
            network.start((args) -> {
                ResCommand response = ((Response) args[0]).getResponse();
                infoLine.setText(String.valueOf(response));
                if (isLoginSuccessfully(response)) {
                    String[] idAndNickName = (((Response) args[0]).getResComment()).split(" ", 2);
                    clientId = idAndNickName[0];
                    nickname = idAndNickName[1];
                    openMainClientWindow();
                } else {
                    loginField.requestFocus();
                }
            });

        } catch (InterruptedException e) {
            infoLine.setText("Server connection error");
        }
    }

    private void openMainClientWindow() {
        Platform.runLater(() -> {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("clientApp.fxml"));
                Stage stage = new Stage(StageStyle.DECORATED);
                Scene scene = new Scene(fxmlLoader.load(), 400, 400);
                //scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("style.css")).toExternalForm());

                stage.setScene(scene);
                stage.setResizable(false);
                ClientAppController clientAppController = fxmlLoader.getController();
                clientAppController.setNetwork(network);
                clientAppController.setClientId(clientId);
                clientAppController.setLogin(login);
                clientAppController.setNickname(nickname);
                stage.setTitle("File Storage");


                ((Stage) infoLine.getScene().getWindow()).close();
                stage.show();
                network.sendClientCtrl(clientAppController);
                clientAppController.work(ReqCommand.SHOW_ALL_FILES);
                stage.setOnCloseRequest(clientAppController.getCloseEventHandler());
                stage.maxWidthProperty().bind(stage.widthProperty());
                stage.minWidthProperty().bind(stage.widthProperty());
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

    public void EnterPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            String pass = passwordField.getText();
            String login = loginField.getText();
            focusAndSend(pass, login, ReqCommand.CHECK_USER);
        }
        ;
    }
}