package com.dnn.clientapp;

import com.dnn.clientapp.network.Network;
import com.dnn.clientapp.network.requestWorks.ReqCommand;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ProfileSettingsController {
    public TextField loginField;
    public TextField nicknameField;
    public PasswordField oldPassword;
    public PasswordField newPassword;
    public Button sendRequest;
    public Button cancel;
    public Text infoLine;
    private String login;
    private String nickname;
    private String clientId;
    private Network network;
    private ClientAppController appController;

    public void setLogin(String login) {
        this.login = login;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public void setNetwork(Network network) {
        this.network = network;
    }


    public void EnterPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            sendRequest();
        }
    }

    private boolean isPasswordsCorrect() {
        String oldPass = oldPassword.getText();
        String newPass = newPassword.getText();
        return
                !oldPass.isEmpty() &&
                        !oldPass.contains(" ") &&
                        !newPass.isEmpty() &&
                        !newPass.equals(" ");
    }

    public void sendRequest() {
        String newLogin = loginField.getText();
        String newNickname = nicknameField.getText();
        String oldPass = oldPassword.getText();
        String newPass = newPassword.getText();

        if (!newLogin.isEmpty()) {
            if (newLogin.equals(login)) {
                infoLine.setText("Logins are the same");
            } else {
                appController.work(ReqCommand.CHANGE_LOGIN + " " + newLogin);
                infoLine.setText("Data is sending");
            }
        }
        if (!newNickname.isEmpty()) {
            if (newNickname.equals(nickname)) {
                infoLine.setText("Nicknames are the same");
            } else {
                appController.work(ReqCommand.CHANGE_NICKNAME + " " + newNickname);
                infoLine.setText("Data is sending");
            }
        }
        if (!oldPass.isEmpty() && !newPass.isEmpty()) {
            if (oldPass.equals(newPass)) {
                infoLine.setText("Passwords are the same");
            } else if (isPasswordsCorrect()) {
                appController.work(ReqCommand.CHANGE_PASSWORD + " " + oldPass + " " + newPass);
                infoLine.setText("Data is sending");
            } else {
                infoLine.setText("Wrong password format");
            }
        }
        if (oldPass.isEmpty() && newPass.isEmpty() && loginField.getText().isEmpty() && nicknameField.getText().isEmpty())
            cancel();
        loginField.setText("");
        nicknameField.setText("");
        newPassword.setText("");
        oldPassword.setText("");
        sendRequest.requestFocus();
    }

    public void cancel() {
        ((Stage) nicknameField.getScene().getWindow()).close();
    }

    public void setHints() {
        loginField.setPromptText(login);
        nicknameField.setPromptText(nickname);
    }

    public void setAppController(ClientAppController appController) {
        this.appController = appController;
    }
}
