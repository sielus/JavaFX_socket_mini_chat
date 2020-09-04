package server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

public class Server {
    private static DatagramSocket datagramSocket;
    private static int port;
    private static boolean running;
    private static ArrayList<ClientInfo> clients = new ArrayList<ClientInfo>(); //Clients list


    public static void start(int port){
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
        System.out.println("test");
        for(ClientInfo info : clients){
            send(messageFromClient,info.getAddress(),info.getPort());
        }
    }

    private static void listen(){
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
                        System.out.println(messageFromClient);
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
\con -> new user connected
\stopServer -> stopping server
\isAvailable-> check if server is available
 */
    private static boolean isCommand(String message, DatagramPacket datagramPacket) {
        if(message.startsWith("\\con:")){
            String name = message.substring(message.indexOf(":") + 1);
            clients.add(new ClientInfo(name,datagramPacket.getAddress(),datagramPacket.getPort()));
            broadcast("User " + name + " Connected");
            return true;
        }
        else if(message.startsWith("\\stopServer")){
            running = false;
        } else if(message.startsWith("\\isAvailable")){
            send("true",datagramPacket.getAddress(),datagramPacket.getPort());
        }
        return false;
    }

    private static void close(){
        running = false;
        datagramSocket.close();
    }


}
