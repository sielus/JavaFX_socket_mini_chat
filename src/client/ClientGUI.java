package client;

import com.sun.prism.Image;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Optional;

public class ClientGUI extends Application {
    private static Parent parent;
    public static Stage stagex;
    public static ClientServer clientServer;
    private static String name;

    @FXML static ListView<String> activeUserList;

    public static void main(String[] args) {
        launch(args);
    }

    public void printMessage (String message,String command){
        setTextInTextFlow(message + "\n",command);
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
            printMessage("Wiadomość prywatna "+ result.get() +" od ciebie do " + targetUserName,"pvSEND");
        }
    }

    @Override
    public void start(Stage primaryStage) {
    }

    private void setTextInTextFlow(String message,String command){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                TextFlow chat_text = new TextFlow();

                chat_text =  (TextFlow) parent.lookup("#chat_text");
                if(command.equals("normal")){
                    Text text = new Text(message);
                    chat_text.getChildren().addAll(text);
                }else if(command.equals("pvSEND")) {
                    Text text = new Text(message);
                    text.setFill(Color.BLUE);
                    chat_text.getChildren().addAll(text);
                }else if(command.equals("pvRECIEVE")){
                    Text text = new Text(message);
                    text.setFill(Color.GREEN);
                    chat_text.getChildren().addAll(text);
                }else if(command.equals("serverCOMMAND")){
                    Text text = new Text(message);
                    text.setFill(Color.RED);
                    chat_text.getChildren().addAll(text);
                }

            }
        });

    }

    public void sendMessageButton(ActionEvent actionEvent) {
        TextField userMessage = (TextField) parent.lookup("#newMessage");
        String newMessage = userMessage.getText();
        clientServer.send(name + " : " + newMessage + " \\endLine");
        userMessage.setText("");
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

    public void sendFileButton(ActionEvent actionEvent) throws IOException {

        FileChooser fileChooser = new FileChooser();
        clientServer.send("\\startSendingTCPfile");
        Socket sock = new Socket("127.0.0.1", 2137);
        File file = fileChooser.showOpenDialog(null);

        PrintWriter printWriter = new PrintWriter(sock.getOutputStream());
        printWriter.println(file.getName());
        printWriter.flush();

        byte[] bytes = new byte[20*1024*1024];
        InputStream inputStream = new FileInputStream(file);
        OutputStream outputStream = sock.getOutputStream();

        int i;
        while ((i = inputStream.read(bytes)) > 0){
            outputStream.write(bytes,0,i);
        }
        outputStream.close();
        inputStream.close();
        sock.close();

    }
}
