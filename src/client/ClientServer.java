package client;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

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


        clientGUI = new ClientGUI();

    }

    public void send(String message){
        try {
            message += "\\e";
            byte[] data = message.getBytes();
            DatagramPacket datagramPacket = new DatagramPacket(data,data.length,address,port);

            socket.send(datagramPacket);
         //   System.out.println(message + " ip : " + datagramPacket.getAddress() + " port " + datagramPacket.getPort());

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
                            clientGUI.printMessage(messageFromClient);
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
        }
        return false;
    }


}
