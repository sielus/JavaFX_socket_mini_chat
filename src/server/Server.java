package server;

import java.net.*;
import java.util.ArrayList;

public class Server {
    private static DatagramSocket datagramSocket;
    private static int port;
    private static boolean running;
    private static ArrayList<ClientInfo> clients = new ArrayList<ClientInfo>(); //Clients list
    static String usersList = "\\userActive";

    public void start(int port){
        try {
            datagramSocket = new DatagramSocket(port);
            running = true;
            listen();
            System.out.println("Server start on port " + port);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void send(String message, InetAddress address, int port){
        try {
            message += "\\e";
            byte[] data = message.getBytes();
            DatagramPacket datagramPacket = new DatagramPacket(data,data.length,address,port);
            datagramSocket.send(datagramPacket);
            System.out.println("Send message to " + address.getHostAddress() + port);

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private static void broadcast(String messageFromClient){
        for(ClientInfo info : clients){
            send(messageFromClient,info.getAddress(),info.getPort());
        }
    }

    private void sendActiveUserList(String endMessage){
        usersList = "\\userList "; // List of active users on server
        for(ClientInfo info : clients){
            usersList += info.getName() + "|";
        }
        broadcast(usersList + "\\e");

    }

    private void listen(){
        Thread thread = new Thread("serverListen"){
            public void run(){
                try {
                    while(running){
                        byte[] data = new byte[1024];
                        DatagramPacket datagramPacket = new DatagramPacket(data,data.length);
                        datagramSocket.receive(datagramPacket);
                        String messageFromClient = new String(data);

                        messageFromClient = messageFromClient.substring(0,messageFromClient.indexOf("\\e")); //end line tag
                        //broadcast(messageFromClient);
                        System.out.println(messageFromClient + " wiadomosc od klienta ");
                        if(!isCommand(messageFromClient,datagramPacket)){
                            broadcast(messageFromClient);
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }; thread.start();
    }
/*

Commands :
// \con -> new user connected
// \stopServer -> stopping server
// \ userList -> Active TCP and send active users list
// \isAvailable-> check if server is available

 */
    private boolean isCommand(String message, DatagramPacket datagramPacket) {
        if(message.startsWith("\\con:")){
            String name = message.substring(message.indexOf(":") + 1);
            clients.add(new ClientInfo(name,datagramPacket.getAddress(),datagramPacket.getPort()));
            broadcast("User " + name + " Connected");
            //sendActiveUsersList(datagramPacket.getAddress());
            return true;
        }else if(message.startsWith("\\stopServer")){
            running = false;
            return true;  //TODO zmienić sposób wyłączenia serwera.

        }else if(message.startsWith("\\isAvailable")){
            send("true",datagramPacket.getAddress(),datagramPacket.getPort());
            return true;
        }else if(message.startsWith("\\userList")){
            sendActiveUsersList();
            return true;
        }

        return false;
    }

    private void sendActiveUsersList(){ // Wysyłanie listy aktywnych użytkowników
        //TODO ogarnąć dodawnaie nazw do stringa
        //  usersList += "\\e";
        sendActiveUserList("\\e");
    }

    private static void close(){
        running = false;
        datagramSocket.close();
    }


}
