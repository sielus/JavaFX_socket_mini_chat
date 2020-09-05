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
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginController {

    public void onLogInButton(String userName, String passwd, String serverIP, int port, ActionEvent actionEvent) {
        if(serverIsAvailable(serverIP,port)) {
            if (checkUserPassAndLogin(userName, passwd)) {
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/client/client_gui.fxml"));
                    Parent root1 = (Parent) fxmlLoader.load();
                    Stage stage = new Stage();
                    stage.setScene(new Scene(root1));
                    stage.show();
                    ClientGUI.createClient(userName, serverIP, port, stage);
                    ((Node) (actionEvent.getSource())).getScene().getWindow().hide();

                    //getActiveUsersList("\\userList \\e",serverIP,port);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void getActiveUsersList(String commend, String serverIP, int port) throws SocketException {
        Thread thread = new Thread("listUser"){
            public void run(){
                while (true){

                    DatagramSocket socket = sendToServerRequest(commend,serverIP,port);
                    byte[] getBackData = new byte[1024];
                    DatagramPacket getBack = new DatagramPacket(getBackData, getBackData.length);
                    try {
                        socket.receive(getBack);
                        String usersActiveListString = new String(getBackData);
                        if(usersActiveListString.startsWith("\\userList")){

                            usersActiveListString = usersActiveListString.substring(0,usersActiveListString.indexOf("\\e")); //end line tag
                            usersActiveListString = (usersActiveListString.replace("\\userList",""));

                            ArrayList<String> userActiveList = new ArrayList<>();
                            Pattern pattern = Pattern.compile("\\w+");
                            Matcher matcher = pattern.matcher(usersActiveListString);
                            System.out.println("getActiveUsersList dziala");
                            while (matcher.find()) {
                                userActiveList.add(matcher.group());
                                ClientGUI.addUsersToUsersList(userActiveList);
                            }
                            System.out.println(usersActiveListString);
                            usersActiveListString = "";

                        }
                        //end line tagSystem.out.println(messageFromClient + " userlist");
                        //socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };thread.start();

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
