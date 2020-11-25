package org.login_window.client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Hyperlink;

import javax.swing.*;
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClientServer {
    public static DatagramSocket socket;
    public InetAddress address;
    private int port;
    private boolean running;
    private String name;
    ClientGUI clientGUI;

    public ClientServer(String name, String address, int port) {
        try {
            this.address = InetAddress.getByName(address);
            this.port = port;
            socket = new DatagramSocket();
            this.name = name;
            clientGUI = new ClientGUI();
        }catch (Exception e){
            e.printStackTrace();
        }
        running = true;
        listen();
        send("\\con:" + name);

        send("\\userList");

        clientGUI = new ClientGUI();
    }

    public void send(String message){
        try {
            message += "\\e";
            byte[] data = message.getBytes();
            DatagramPacket datagramPacket = new DatagramPacket(data,data.length,address,port);
            socket.send(datagramPacket);
            System.out.println(message);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void listen(){
        Thread thread = new Thread("serverListen"){
            public void run(){
                try {
                    while(running){
                        byte[] data = new byte[1024];
                        DatagramPacket datagramPacket = new DatagramPacket(data,data.length);
                        socket.receive(datagramPacket);
                        String messageFromClient = new String(data);
                        System.out.println(messageFromClient);
                        messageFromClient = messageFromClient.substring(0,messageFromClient.indexOf("\\e")); //end line tag
                        if(!isCommand(messageFromClient,datagramPacket)){
                            //Print message
                            if(!messageFromClient.isEmpty()){
                                clientGUI.printMessage(messageFromClient,"normal",null);
                            }
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }; thread.start();
    }

    private boolean isCommand(String message, DatagramPacket datagramPacket) {
        DialogsManager dialogsManager = new DialogsManager();
        if(message.startsWith("\\con:")){
            String newUserConnected = message.substring(message.indexOf(":") + 1);
            clientGUI.printMessage(newUserConnected,"serverCOMMAND",null);
            return true;
        }else if(message.startsWith("\\userList")) {
            refreshUserActiveList(message);
            return true;
        }else if(message.startsWith("\\pvMessage:")) {
            startNewPWwindow(message);
        return true;
        }else if(message.startsWith("\\disconnect")) {
            disconnectFromServer();
            return true;
        }else if(message.startsWith("\\kick")) {
            dialogsManager.showKickedAlert();
            return true;
        }else if(message.startsWith("\\ban")) {
            dialogsManager.showBannedAlert();
            return true;
        }else if(message.startsWith("\\newFileOnServer")) {
            String fileName = message.substring(message.indexOf(":") + 1,message.indexOf("|"));
           // fileName = (fileName.replace("\\userList",""));
            String senderName = message.substring(message.indexOf("|")+1);

            generateHyperlink(fileName,senderName);
            return true;
        }
        return false;
    }

    static void disconnectFromServer() {
        Platform.exit();
        System.exit(0);
    }

    private void generateHyperlink(String fileName,String senderName) {
        Hyperlink hyperlink = new Hyperlink(fileName);
        hyperlink.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                downloadFileFromServer(fileName);
            }
        });
        clientGUI.printMessage("Plik użytkownika " + senderName,"hyperlink",hyperlink);
    }

    private void downloadFileFromServer(String fileName) {
        send("\\startSendingFileToClientTCP");
        try {
            Socket sock = new Socket(address, ClientGUI.tcpPort);
            PrintWriter printWriter = new PrintWriter(sock.getOutputStream());
            printWriter.println(fileName);
            printWriter.flush();

            InputStream inputStream = sock.getInputStream();
            OutputStream outputStream = null;
            JFrame parentFrame = new JFrame();

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save as");
            fileChooser.setSelectedFile(new File(fileName));
            int userSelection = fileChooser.showSaveDialog(parentFrame);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                String path = fileChooser.getSelectedFile().getAbsolutePath();
                System.out.println(path);
                outputStream = new FileOutputStream(path);
                byte[] bytes = new byte[20 * 1024 * 1024];
                int count;
                while ((count = inputStream.read(bytes)) > 0) {
                    outputStream.write(bytes, 0, count);
                }
                outputStream.close();
                inputStream.close();
                sock.close();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void startNewPWwindow(String message) {
        String pvMessageBeforeEncode = message.substring(message.indexOf(":") + 1);
        String usersActiveListString = new String(pvMessageBeforeEncode);
        String[] result = usersActiveListString.split("\\|");
        String targetUserName = result[0];
        String nameSender = result[1];
        String messagePV = result[2];
        clientGUI.printMessage("Wiadomośc prywatna od " + nameSender + " do ciebie : " + messagePV,"pvRECIEVE",null);
    }

    private static void refreshUserActiveList(String message) {
        String usersActiveListString = new String(message);
        usersActiveListString = (usersActiveListString.replace("\\userList",""));
        ArrayList<String> userActiveList = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\w+");
        Matcher matcher = pattern.matcher(usersActiveListString);
        while (matcher.find()) {
            userActiveList.add(matcher.group());
        }
        ClientGUI.addUsersToUsersList(userActiveList);
    }

    public void sendOnDisconectRequest() {
        send("\\disc:" + name);
    }
}
