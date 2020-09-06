package client;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClientServer {
    DatagramSocket socket;
    private InetAddress address;
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
                                clientGUI.printMessage(messageFromClient);
                            }
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }; thread.start();
    }

    private static boolean isCommand(String message, DatagramPacket datagramPacket) {
        if(message.startsWith("\\con:")){
            return true;
        }else if(message.startsWith("\\userList")) {
            refreshUserActiveList(message);
            return true;
        } else if(message.startsWith("\\pvStart:")) {
            String senderUserNamePV = message.substring(message.indexOf(":") + 1);
            startNewPWwindow(senderUserNamePV);
            return true;
        }else if(message.startsWith("\\pvMessage:")) {
        startNewPWwindow(message);
        return true;
    }
        return false;
    }

    private static void startNewPWwindow(String senderUserNamePV) {
        System.out.println(senderUserNamePV + " okno");

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
