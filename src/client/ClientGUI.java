package client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.ArrayList;

public class ClientGUI extends Application {
    private static Parent parent;
    static ClientServer clientServer;
    static public TextArea chatTextarea;
    private static String name;
    static ListView<String> activeUserList;
    public static void main(String[] args) {
        launch(args);
    }

    public void printMessage (String message){
        setTextInTextArea(message);
    }

    public static void createClient(String userName, String ipAdress, int port, Stage stage){
        parent = stage.getScene().getRoot();
        name = userName;
        stage.setResizable(false);
        clientServer = new ClientServer(userName,ipAdress,port);
        activeUserList = (ListView<String>) parent.lookup("#group_chat_list_users");

        activeUserList.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
              String targetUserName = activeUserList.getSelectionModel().getSelectedItem();
                clientServer.send("\\pvMessage:" + targetUserName + "|" + name + "|" + "xxxx");
            }
        });

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                clientServer.sendOnDisconectRequest();
            }
        });


    }

    @Override
    public void start(Stage primaryStage) {
    }

    private void setTextInTextArea(String message){
        TextArea textArea = (TextArea) parent.lookup("#chatTextarea");
        textArea.appendText(message + "\n");
    }

    public void sendMessageButton(ActionEvent actionEvent) {
        TextField userMessage = (TextField) parent.lookup("#newMessage");
        String newMessage = userMessage.getText();
        clientServer.send(name + " : " + newMessage + " \\endLine");
    }

    public static void addUsersToUsersList(ArrayList list){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                activeUserList.getItems().removeAll();
                activeUserList.getItems().clear();
                activeUserList.getItems().addAll(list);
                activeUserList.refresh();
            }
        });



    }
}
