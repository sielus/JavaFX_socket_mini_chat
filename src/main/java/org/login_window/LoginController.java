package org.login_window;

import org.login_window.client.ClientGUI;
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

    public void onLogInButton(String userName, String passwd, String serverIP, int udpPort, int tcpPort, ActionEvent actionEvent) throws Exception {
        if (serverIsAvailable(serverIP, udpPort)) {
            startLoggingOnServer(serverIP,udpPort);
            if (checkUserPassAndLogin(userName, passwd, serverIP, tcpPort)) {
                try {
                    Stage stage = new Stage();
                    URL url = new File("src/main/resources/org/client_gui.fxml").toURI().toURL();
                    Parent root1 = FXMLLoader.load(url);
                    stage.setScene(new Scene(root1));
                    ClientGUI clientGUI = new ClientGUI();
                    clientGUI.createClient(userName, serverIP, udpPort, tcpPort, stage);
                    stage.show();
                    ((Node) (actionEvent.getSource())).getScene().getWindow().hide();
                    //getActiveUsersList("\\userList \\e",serverIP,port);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else {
                wrongPasswdDialog();
            }
        }
    }
    public void startLoggingOnServer(String address, int port){
        try {
            String commend =  "\\startLoginUser\\e";
            sendToServerRequest(commend,address,port);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private boolean checkUserPassAndLogin(String userName, String passwd, String serverIp, int tcpPort) throws Exception {
        Socket socket = new Socket(serverIp, tcpPort);
        String userData = userName + ":" + passwd;
        Security security = new Security(socket,userData);
        security.getPublicKey();
        security.sendEncryptedData();
        String serverRequest = security.getServerRequest();

        if (serverRequest.equals("true")) {
            return true;
        } else {//TODO mechanizm logownia do bazy
            return false;
        }
    }



    private boolean serverIsAvailable(String serverIP, int port) {
        try {
            String checkServer = "\\isAvailable\\e";
            DatagramSocket socket = sendToServerRequest(checkServer, serverIP, port);
            byte[] getBackData = new byte[1024];
            socket.setSoTimeout(1000);
            DatagramPacket getBack = new DatagramPacket(getBackData, getBackData.length);
            try {
                socket.receive(getBack);
                String messageFromClient = new String(getBackData);
                messageFromClient = messageFromClient.substring(0, messageFromClient.indexOf("\\e")); //end line tag
                if (messageFromClient.equals("true")) {
                    socket.close();
                    socket = null;
                    return true;
                }
            } catch (SocketTimeoutException e) {
                serverIsOfflineDialog(serverIP, port);
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
            DatagramPacket datagramPacket = new DatagramPacket(data, data.length, InetAddress.getByName(serverIP), port);
            DatagramSocket socket = new DatagramSocket();
            socket.send(datagramPacket);
            return socket;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void serverIsOfflineDialog(String serverIP, int serverPORT) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning Dialog");
        alert.setHeaderText("Server is unavailable!");
        alert.setContentText("Server  on IP " + serverIP + " port " + String.valueOf(serverPORT) + " is unavailable");
        alert.showAndWait();
    }

    private void wrongPasswdDialog() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning Dialog");
        alert.setHeaderText("User name or passwd are wrong");
        alert.setContentText("Try again!");
        alert.showAndWait();
    }
}
