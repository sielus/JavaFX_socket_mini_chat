package login_window;

import client.ClientGUI;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import server.ClientInfo;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class LoginController {

    public void onLogInButton(String userName, String passwd, String serverIP, int port, ActionEvent actionEvent) {
        if(serverIsAvailable(serverIP,port))
            if(checkUserPassAndLogin(userName,passwd)){
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/client/client_gui.fxml"));
                    Parent root1 = (Parent) fxmlLoader.load();
                    Stage stage = new Stage();
                    stage.setScene(new Scene(root1));
                    stage.show();
                    ClientGUI.createClient(userName,serverIP,port,stage);
                    ((Node)(actionEvent.getSource())).getScene().getWindow().hide();

                    sendToServerRequest("\\userList \\e",serverIP,port); //aktywowanie socketu TCP na serwerze
                    getTCPactiveUsersList(serverIP);


                }catch (Exception e){
                    e.printStackTrace();
                }
            }
    }

    private void getTCPactiveUsersList(String serverIP) {
        Socket socket = sendTCPRequest(serverIP);
        recieveTCPRequest(socket);

    }

    private void recieveTCPRequest(Socket socket) {
        try {
            ObjectInputStream  objectInputStream = new ObjectInputStream(socket.getInputStream());
            ArrayList<ArrayList> listOfUser = (ArrayList<ArrayList>) objectInputStream.readObject();

            ClientGUI.addUsersToUsersList(listOfUser);
           // BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
          //  System.out.println(bufferedReader.readLine());
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private Socket sendTCPRequest(String serverIP) {
        try {
            Socket socket = new Socket(serverIP,7777);
            PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
            printWriter.println("tcp quest from client");
            printWriter.flush();
            return socket;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
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
