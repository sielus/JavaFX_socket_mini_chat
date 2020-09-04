package server;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Server {
    private static DatagramSocket datagramSocket;
    private static int port;
    private static boolean running;
    private static ArrayList<ClientInfo> clients = new ArrayList<ClientInfo>(); //Clients list
    private static ArrayList<String> clientsList = new ArrayList<>();


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
        for(ClientInfo info : clients){
            send(messageFromClient,info.getAddress(),info.getPort());
            clientsList.add(info.getName());
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
    private static boolean isCommand(String message, DatagramPacket datagramPacket) {
        if(message.startsWith("\\con:")){
            String name = message.substring(message.indexOf(":") + 1);
            clients.add(new ClientInfo(name,datagramPacket.getAddress(),datagramPacket.getPort()));
            broadcast("User " + name + " Connected");
            sendActiveUsersList();
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

    private static void sendActiveUsersList(){ // Wysyłanie listy aktywnych użytkowników

        try {
            ServerSocket serverSocket = new ServerSocket(7777);
            Socket socket = serverSocket.accept(); // blocking call, this will wait until a connection is attempted on this port.

            InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            System.out.println(bufferedReader.readLine());

          //  PrintWriter printWriter = new PrintWriter(socket.getOutputStream()); // Wysłanie TCP do klienta

            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());


            objectOutputStream.writeObject(clientsList);
            serverSocket.close();
            socket.close();
          //  socket.close();
           // printWriter.println("TCP request from server");
           // printWriter.flush();
//            DatagramPacket datagramPacket = new DatagramPacket(,arrayList.size(),address,port);
//            datagramSocket.send(datagramPacket);
//            System.out.println("Send message to " + address.getHostAddress() + port);


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void close(){
        running = false;
        datagramSocket.close();
    }


}
