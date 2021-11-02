package com.dnn.clientapp;

import com.dnn.clientapp.network.*;
import com.dnn.clientapp.network.requestWorks.ReqCommand;
import com.dnn.clientapp.network.requestWorks.Request;
import com.dnn.clientapp.network.responseWorks.ResCommand;
import com.dnn.clientapp.network.responseWorks.Response;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

import static com.dnn.clientapp.network.requestWorks.ReqCommand.*;

public class ClientAppController implements Initializable {
    public TextField cmdLine;
    public TextArea cmdArea;
    @FXML
    public VBox fileArea;
    public ScrollPane fileScrollPane;
    public Label defaultFileAreaText;
    public VBox cmdAreaBox;
    public VBox mainBox;
    private Network network;
    private String clientId;
    private Response<Object> response;
    private Request request = new Request();
    private String filePath;
    private String login;
    private String nickname;
    private ProfileSettingsController settingsController;

    public void setLogin(String login) {
        this.login = login;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public VBox getFileArea() {
        return fileArea;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    @FXML
    private void showUserSettingWindow() {
        Platform.runLater(() -> {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("profileSettings.fxml"));
                Stage stage = new Stage(StageStyle.DECORATED);
                stage.setScene(new Scene(fxmlLoader.load(), 300, 230));
                stage.setResizable(false);
                settingsController = fxmlLoader.getController();
                settingsController.setNetwork(network);
                settingsController.setClientId(clientId);
                settingsController.sendRequest.requestFocus();
                settingsController.setLogin(login);
                settingsController.setNickname(nickname);

                stage.setTitle("Profile settings");
                settingsController.setHints();
                settingsController.setAppController(this);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void sendMsg(ActionEvent actionEvent) {
        try {
            work(cmdLine.getText());
        } catch (ArrayIndexOutOfBoundsException e) {
            addText("The command isn't complete");
        } catch (IllegalArgumentException e) {
            addText("Unknown command");
        }
        cmdLine.clear();
        cmdLine.requestFocus();
    }

    private String getInfo() {
        ReqCommand[] commands = ReqCommand.values();
        StringBuilder cmdList = new StringBuilder();
        for (ReqCommand command : commands) {
            cmdList.append(command).append("\n");
        }
        return String.valueOf(cmdList);
    }

    public void work(ReqCommand reqCommand) {
        work(String.valueOf(reqCommand));
    }

    public void work(String c) {

        String[] s = c.split(" ", 2);

        ReqCommand req = ReqCommand.valueOf(s[0]);
        addClientText(c);
        System.out.println("Command: " + req);

        switch (req) {
            case CHECK_SEND: {
                filePath = "C:\\Users\\Николай\\Desktop\\Новая папка (2)\\Патент KR1640504 B1.pdf";
                work(ReqCommand.SEND_FILE + " Патент KR1640504 B1.pdf");
                break;
            }
            case TO_THE_PREVIOUS_FOLDER: {
                request.setCommand(TO_THE_PREVIOUS_FOLDER);
                network.sendMsg(request);
                break;
            }
            case GO_TO_THE_FOLDER: {
                request.setCommand(GO_TO_THE_FOLDER);
                request.setCmdComment(s[1]);
                network.sendMsg(request);
                break;
            }
            case CHANGE_PASSWORD: {
                request.setCommand(CHANGE_PASSWORD);
                request.setClientId(clientId);
                request.setCmdComment(s[1]);
                network.sendMsg(request);
                break;
            }
            case INFO: {
                new Thread(() -> {
                    JOptionPane.showMessageDialog(
                            null,
                            getInfo(),
                            "List of commands",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                }).start();
                break;
            }
            case CHANGE_NICKNAME: {
                request.setCommand(CHANGE_NICKNAME);
                request.setClientId(clientId);
                request.setCmdComment(s[1]);
                network.sendMsg(request);
                break;
            }
            case CHANGE_LOGIN: {
                request.setCommand(CHANGE_LOGIN);
                request.setCmdComment(s[1]);
                request.setClientId(clientId);
                network.sendMsg(request);
                break;
            }
            case SHOW_ALL_FILES: {
                request.setCommand(SHOW_ALL_FILES);
                request.setClientId(clientId);
                network.sendMsg(request);
                break;
            }
            case QUIT: {
                request.setCommand(QUIT);
                network.sendMsg(request);
                System.exit(1);
                break;
            }
            case HI: {
                request.setCommand(HI);
                request.setClientId(clientId);
                network.sendMsg(request);
                break;
            }
            case GET_FILE: {
                request.setCommand(GET_FILE);
                request.setCmdComment(s[1]);
                request.setClientId(clientId);
                network.sendMsg(request);
                break;
            }
            case ADD_FOLDER: {
                request.setClientId(clientId);
                request.setCommand(ADD_FOLDER);
                request.setCmdComment(s[1]);
                network.sendMsg(request);
                break;
            }
            case DELETE_FILE: {
                request.setCommand(DELETE_FILE);
                request.setCmdComment(s[1]);
                request.setClientId(clientId);
                network.sendMsg(request);
                break;
            }
            case SEND_FILE: {
                byte[] buffer = new byte[1024 * 512];
                String file = s[1];
                try (RandomAccessFile accessFile = new RandomAccessFile(filePath, "r")) {
                    while (true) {
                        request.setCommand(RECEIVE_FILE);
                        request.setCmdComment(file);
                        request.setPosition(accessFile.getFilePointer());
                        int read = accessFile.read(buffer);
                        if (read < 0) {
                            request.setFile(new byte[0]);
                            request.setClientId(clientId);
                            network.sendMsg(request);
                        }
                        if (read < buffer.length - 1) {
                            byte[] tempBuffer;
                            if (read < 0) {
                                tempBuffer = new byte[0];
                            } else {
                                tempBuffer = new byte[read];
                            }
                            System.arraycopy(buffer, 0, tempBuffer, 0, read);
                            request.setFile(tempBuffer);
                            request.setClientId(clientId);
                            network.sendMsg(request);
                            break;
                        } else {
                            request.setFile(buffer);
                            request.setClientId(clientId);
                            network.sendMsg(request);
                        }
                        buffer = new byte[1024 * 512];
                    }
                } catch (FileNotFoundException e) {
                    addText("File isn't found");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
            default: {
                addText("Unknown command");
                break;
            }
        }
    }


    public void setNetwork(Network network) {
        this.network = network;
        receiveResponse();
    }

    public void receiveResponse() {
        new Thread(() -> {
            try {
                network.start((args) -> {
                    response = (Response) args[0];
                    addText("Server's response: " + response.getResponse());
                });
            } catch (InterruptedException e) {
                System.out.println("ops");
            }
        }).start();
    }

    public void addFileToFileArea(String fileName) {
        Platform.runLater(() -> {
            HBox fileLine = new HBox();
            fileLine.setPrefWidth(fileScrollPane.getWidth() - 18);

            fileLine.setSpacing(5);

            Label file = new Label(fileName);
            file.setAlignment(Pos.CENTER_LEFT);
            file.setFont(Font.font(String.valueOf(Font.getDefault()), 14));
            Button deleteFile = new Button("Delete");
            Button downloadFile = new Button("Download");

            deleteFile.setOnAction(actionEvent -> work(ReqCommand.DELETE_FILE + " " + fileName));
            downloadFile.setOnAction(actionEvent -> work(ReqCommand.GET_FILE + " " + fileName));

            HBox.setHgrow(file, Priority.ALWAYS);
            file.setMaxWidth(Double.MAX_VALUE);

            fileLine.getChildren().add(file);
            fileLine.getChildren().add(deleteFile);
            fileLine.getChildren().add(downloadFile);

            fileArea.getChildren().add(fileLine);
        });
    }

    public void showDefaultTextInFileArea() {
        Platform.runLater(() -> fileArea.getChildren().add(defaultFileAreaText)
        );
    }

    public void addText(String s) {
        cmdArea.appendText(s);
        cmdArea.appendText("\n");
    }

    public void addClientText(String clientCommand) {
        cmdArea.appendText("Command: ");
        cmdArea.appendText(clientCommand);
        cmdArea.appendText("\n");
    }

    private final javafx.event.EventHandler<WindowEvent> closeEventHandler = new javafx.event.EventHandler<>() {
        @Override
        public void handle(WindowEvent event) {
            network.fullDisconnect();
        }
    };

    public javafx.event.EventHandler<WindowEvent> getCloseEventHandler() {
        return closeEventHandler;
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        request.setCommand(ReqCommand.SHOW_ALL_FILES);
    }

    public void exitIsClicked() {
        work(ReqCommand.QUIT);
    }

    public void showCommandList() {
        work(ReqCommand.INFO);
    }

    public void changeProfile() {
        Platform.runLater(() -> {
            try {
                ((Stage) fileArea.getScene().getWindow()).close();
                FXMLLoader fxmlLoader = new FXMLLoader(Client.class.getResource("welcomeWindow.fxml"));
                Stage stage = new Stage(StageStyle.DECORATED);
                Scene scene = new Scene(fxmlLoader.load(), 300, 200);
                scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("style.css")).toExternalForm());

                stage.setScene(scene);
                stage.setTitle("Welcome");
                stage.setResizable(false);
                stage.show();
                WelcomeWindowController windowController = fxmlLoader.getController();
                stage.setOnCloseRequest(windowController.getCloseEventHandler());
                windowController.setNetwork(network);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void addFile(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose file");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All files", "*.*"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                new FileChooser.ExtensionFilter("PNG", "*.png")
        );
        File file = fileChooser.showOpenDialog(fileArea.getScene().getWindow());
        if (file != null) {
            filePath = file.getAbsolutePath();
            work(ReqCommand.SEND_FILE + " " + file.getName());
        }
    }

    public void showingCmdArea(ActionEvent e) {
        if (((CheckMenuItem) e.getSource()).isSelected()) {
            fileArea.setPrefHeight(300);
            cmdAreaBox.setMaxHeight(100);
            cmdAreaBox.setVisible(true);
        } else {
            fileArea.setPrefHeight(400);
            cmdAreaBox.setMaxHeight(0);
            cmdAreaBox.setVisible(false);
        }
    }

    public void update() {
        work(String.valueOf(ReqCommand.SHOW_ALL_FILES));
    }

    public void changeLogin(String newLogin) {
        this.login = newLogin;
        settingsController.setLogin(newLogin);
        settingsController.loginField.setPromptText(newLogin);
    }

    public void serverResponseToSettingWindow(ResCommand response) {
        settingsController.infoLine.setText(String.valueOf(response));
    }

    public void changeNickname(String newNickname) {
        this.nickname = newNickname;
        settingsController.setNickname(newNickname);
        settingsController.nicknameField.setPromptText(newNickname);
    }

    public void lostConnection() {
        Label temporaryLabel = new Label("Server's connection was lost");
        fileArea.getChildren().clear();
        fileArea.getChildren().add(temporaryLabel);

    }

    public void goToPreviousFolder(ActionEvent actionEvent) {
        work(TO_THE_PREVIOUS_FOLDER);
    }

    public void addFolder(ActionEvent actionEvent) {
        new Thread(() -> {
            showFolderNamingWindow();
        }).start();
    }

    private void showFolderNamingWindow() {
        JTextField folderName = new JTextField();
        Object[] message = {
                "File name:", folderName,
        };
        UIManager.put("OptionPane.noButtonText", "Cancer");
        UIManager.put("OptionPane.yesButtonText", "Confirm");
        int option = JOptionPane.showConfirmDialog(null, message, "Name the folder", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            if (!folderName.getText().equals("") && !folderName.getText().contains(".")) {
                work(ADD_FOLDER + " " + folderName.getText());
            } else {
                showFolderNamingWindow();
            }
        }
    }

    public void addFolderToFilesArea(String folderName) {
        Platform.runLater(() -> {
            /*Button folder = new Button(folderName);
            HBox.setHgrow(folder, Priority.ALWAYS);
            folder.setMaxWidth(Double.MAX_VALUE);

            folder.setPrefWidth(fileScrollPane.getWidth() - 15);

            fileArea.getChildren().add(folder);*/

            HBox fileLine = new HBox();
            fileLine.setPrefWidth(fileScrollPane.getWidth() - 18);
            fileLine.setSpacing(5);

            Button folder = new Button(folderName);
            folder.setFont(Font.font(String.valueOf(Font.getDefault()), 14));
            Button deleteFile = new Button("Delete");



            folder.setOnAction(actionEvent -> work(GO_TO_THE_FOLDER + " " + folder.getText()));
            deleteFile.setOnAction(actionEvent -> work(ReqCommand.DELETE_FILE + " " + folderName));

            HBox.setHgrow(folder, Priority.ALWAYS);
            folder.setMaxWidth(Double.MAX_VALUE);

            fileLine.getChildren().add(folder);
            fileLine.getChildren().add(deleteFile);

            fileArea.getChildren().add(fileLine);


        });
    }
}
