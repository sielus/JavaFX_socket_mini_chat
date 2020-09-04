package client;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.ArrayList;

public class ClientGUI extends Application {
    private static Parent parent;
    private static ClientServer clientServer;
    static public TextArea chatTextarea;
    private static String name;

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
        ListView<String> activeUserList= (ListView<String>) parent.lookup("#group_chat_list_users");
        activeUserList.getItems().addAll(list);
    }
}
