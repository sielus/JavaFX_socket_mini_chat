package login_window;

import client.ClientGUI;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import java.io.*;
import java.net.*;

public class LoginController {

    public void onLogInButton(String userName, String passwd, String serverIP, int udpPort,int tcpPort, ActionEvent actionEvent) {
        if(serverIsAvailable(serverIP,udpPort)) {
            if (checkUserPassAndLogin(userName, passwd)) {
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/client/client_gui.fxml"));
                    Parent root1 = (Parent) fxmlLoader.load();
                    Stage stage = new Stage();
                    stage.setScene(new Scene(root1));
                    stage.show();
                    ClientGUI clientGUI = new ClientGUI();
                    clientGUI.createClient(userName, serverIP, udpPort,tcpPort, stage);
                    ((Node) (actionEvent.getSource())).getScene().getWindow().hide();
                    //getActiveUsersList("\\userList \\e",serverIP,port);
                } catch (Exception e) {
                    e.printStackTrace();

                }
            }
        }
    }

    private boolean checkUserPassAndLogin(String userName, String passwd) {
        return true; //TODO mechanizm logownia do bazy
    }

    private boolean serverIsAvailable(String serverIP, int port) {
        try {
            String checkServer = "\\isAvailable\\e";
            DatagramSocket socket = sendToServerRequest(checkServer,serverIP,port);
            byte[] getBackData = new byte[1024];
            socket.setSoTimeout(1000);
                DatagramPacket getBack = new DatagramPacket(getBackData, getBackData.length);
                try {
                    socket.receive(getBack);
                    String messageFromClient = new String(getBackData);
                    messageFromClient = messageFromClient.substring(0,messageFromClient.indexOf("\\e")); //end line tag
                    if(messageFromClient.equals("true")){
                        socket.close();
                        return true;
                    }
                } catch (SocketTimeoutException e) {
                     showWarningDialog(serverIP,port);
                    return false;
                }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private DatagramSocket sendToServerRequest(String commend, String serverIP, int port) {
        try {
            byte[] data = commend.getBytes();
            DatagramPacket datagramPacket = new DatagramPacket(data,data.length, InetAddress.getByName(serverIP),port);
            DatagramSocket socket = new DatagramSocket();
            socket.send(datagramPacket);
            byte[] getBackData = new byte[1024];
            return socket;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private void showWarningDialog(String serverIP,int serverPORT) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning Dialog");
        alert.setHeaderText("Server is unavailable!");
        alert.setContentText("Server  on IP " + serverIP + " port " + String.valueOf(serverPORT) + " is unavailable");
        alert.showAndWait();
    }
}
