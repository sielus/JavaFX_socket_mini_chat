package client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import java.util.ArrayList;
import java.util.Optional;

public class ClientGUI extends Application {
    private static Parent parent;
    public static Stage stagex;
    public static ClientServer clientServer;
    static public TextArea chatTextarea;
    private static String name;
    static ListView<String> activeUserList;

    public static void main(String[] args) {
        launch(args);
    }

    public void printMessage (String message){
        setTextInTextArea(message);
    }

    public  void createClient(String userName, String ipAdress, int port, Stage stage){
        stagex = stage;
        parent = stage.getScene().getRoot();
        name = userName;
        stage.setTitle("Chat Sielus 'Let's Talk! :3 | Użytkownik " + userName);
        stage.setResizable(false);
        clientServer = new ClientServer(userName,ipAdress,port);
        activeUserList = (ListView<String>) parent.lookup("#group_chat_list_users");

        activeUserList.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
              String targetUserName = activeUserList.getSelectionModel().getSelectedItem();
              openInputDialog(targetUserName);
            }
        });

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                clientServer.sendOnDisconectRequest();
            }
        });
    }

    private void openInputDialog(String targetUserName) {
        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("Okno wiadomości prywatnej");
        dialog.setHeaderText("Wiadomość prywatna do " + targetUserName);
        dialog.setContentText("Wiadomość : ");
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()){
            clientServer.send("\\pvMessage:" + targetUserName + "|" + name + "|" + result.get());
            printMessage("Wiadomość prywatna "+ result.get() +" od ciebie do " + targetUserName);
        }
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
