package com.dnn.clientapp;

import com.dnn.clientapp.network.Network;
import com.dnn.clientapp.network.Request;
import com.dnn.clientapp.network.Response;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

import javax.swing.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.util.ResourceBundle;

public class ClientAppController implements Initializable {
    public TextField cmdLine;
    public TextArea cmdArea;
    @FXML
    public VBox fileArea;
    public ScrollPane fileScrollPane;
    public Label defaultFileAreaText;
    private Network network;
    private String nickName;
    private Response response;
    private Request request = new Request();
    private String storagePath = "C:\\Users\\Николай\\IdeaProjects\\FileStorage\\clientApp\\src\\storage\\";

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public void sendMsg(ActionEvent actionEvent) {
        addClientText(cmdLine.getText());

        try {
            work(cmdLine.getText());
        } catch (ArrayIndexOutOfBoundsException e) {
            addText("The command isn't complete");
        }
        cmdLine.clear();
        cmdLine.requestFocus();
    }

    private String getInfo() {
        Commands[] commands = Commands.values();
        StringBuilder cmdList = new StringBuilder();
        for (Commands command : commands) {
            cmdList.append(command.get()).append("\n");
        }
        return String.valueOf(cmdList);
    }

    public void work(String c) {
        String[] s = c.split(" ");
        String cmd = s[0];
        switch (Commands.get(cmd)) {
            case INFO: {
                JOptionPane.showMessageDialog(
                        null,
                        getInfo(),
                        "List of commands",
                        JOptionPane.INFORMATION_MESSAGE
                );
                break;
            }
            case SHOW_ALL_FILES: {
                request.setCommand(Request.SHOW_ALL_FILES);
                request.setNickname(nickName);
                network.sendMsg(request);
                break;
            }

            case QUIT: {
                request.setCommand(Request.QUIT);
                network.sendMsg(request);
                System.exit(1);
                break;
            }

            case HI: {
                request.setCommand(Request.HI);
                request.setNickname(nickName);
                network.sendMsg(request);
                break;
            }
            case GET_FILE: {
                request.setCommand("Get file");
                request.setCmdComment(s[1]);
                request.setNickname(nickName);
                network.sendMsg(request);
                break;
            }
            case DELETE: {
                request.setCommand(Request.DELETE);
                request.setCmdComment(s[1]);
                request.setNickname(nickName);
                network.sendMsg(request);
                break;
            }
            case SEND_FILE: {
                byte[] buffer = new byte[1024 * 512];
                try (RandomAccessFile accessFile = new RandomAccessFile(storagePath + s[1], "r")) {
                    while (true) {
                        request.setCommand("Receive file");
                        request.setCmdComment(s[1]);
                        request.setPosition(accessFile.getFilePointer());
                        int read = accessFile.read(buffer);
                        if (read < 0) {
                            request.setFile(new byte[0]);
                            request.setNickname(nickName);
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
                            request.setNickname(nickName);
                            network.sendMsg(request);
                            break;
                        } else {
                            request.setFile(buffer);
                            request.setNickname(nickName);
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
            default:
                addText("Unknown command");
        }
        if (!request.getCommand().equals("")) {
            addClientText(request.getCommand());
        }
        request.setCommand("");
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
                    addText("Server's response:", response.getResponse());
                    System.out.println();
                });
            } catch (InterruptedException e) {
                System.out.println("ops");
            }
        }).start();
    }

    public void addFileToFileArea(String fileName) {
        Platform.runLater(() -> {
            Button button = new Button(fileName);
            button.setMinWidth(fileScrollPane.getWidth() - 5);
            fileArea.getChildren().add(button);
            ContextMenu cm = new ContextMenu();
            MenuItem deleteFile = new MenuItem("Delete");
            deleteFile.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    work(Commands.DELETE.get() + " " + fileName);
                }
            });
            cm.getItems().add(deleteFile);
            MenuItem downloadFile = new MenuItem("Download");
            downloadFile.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    System.out.println(fileName);
                    work(Commands.GET_FILE.get() + " " + fileName);
                }
            });
            cm.getItems().add(downloadFile);

            button.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

                @Override
                public void handle(MouseEvent t) {
                    cm.show(button, t.getScreenX(), t.getScreenY());
                }
            });
        });
    }

    public void showDefaultTextInFileArea() {
        Platform.runLater(() -> fileArea.getChildren().add(defaultFileAreaText)
        );
    }

    public void addText(String... s) {
        for (String msgPart : s) {
            cmdArea.appendText(" " + msgPart + " ");
        }
        cmdArea.appendText("\n");
    }

    public void addClientText(String clientCommand) {
        cmdArea.appendText("Command: ");
        cmdArea.appendText(clientCommand);
        cmdArea.appendText("\n");
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


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        request.setCommand(Request.SHOW_ALL_FILES);
    }

    public void exitIsClicked(ActionEvent actionEvent) {
        work(Commands.QUIT.get());
    }

    public void showCommandList(ActionEvent actionEvent) {
        work(Commands.INFO.get());
    }

    public void changeProfile(ActionEvent actionEvent) {
        Platform.runLater(() -> {
            try {
                ((Stage) fileArea.getScene().getWindow()).close();
                FXMLLoader fxmlLoader = new FXMLLoader(Client.class.getResource("welcomeWindow.fxml"));
                Stage stage = new Stage(StageStyle.DECORATED);
                stage.setScene(new Scene(fxmlLoader.load(), 300, 200));
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

}
