package client;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ClientGUI extends Application {
    private static Parent parent;
    private static ClientServer clientServer;
    static public TextArea chatTextarea;
    public TextField newMessage;
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
        clientServer = new ClientServer(userName,ipAdress,port);
    }

    @Override
    public void start(Stage primaryStage) {
    }

    private void setTextInTextArea(String message){
        TextArea textArea = (TextArea) parent.lookup("#chatTextarea");
        System.out.println("test");
        textArea.appendText(message + "\n");
    }


    public void sendMessageButton(ActionEvent actionEvent) {
        TextField textField = (TextField) parent.lookup("#newMessage");
        String newMessage = textField.getText();
        clientServer.send(name + " : " + newMessage + " \\endLine");
    }
}
