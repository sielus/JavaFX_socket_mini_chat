package server;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Hyperlink;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
public class Server {
    private static DatagramSocket datagramSocket;
    private static int port;
    private static boolean running;
    private static ArrayList<ClientInfo> clients = new ArrayList<ClientInfo>(); //Clients list
    static String usersList = "\\userActive";

    public void start(int port) {
        try {
            datagramSocket = new DatagramSocket(port);
            running = true;
            listen();
            System.out.println("Server start on port " + port);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void send(String message, InetAddress address, int port) {
        try {
            message += "\\e";
            byte[] data = message.getBytes();
            DatagramPacket datagramPacket = new DatagramPacket(data, data.length, address, port);
            datagramSocket.send(datagramPacket);
            System.out.println("Send message to " + address + port);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void broadcast(String messageFromClient) {
        for (ClientInfo info : clients) {
            System.out.println("broadadsad");
            send(messageFromClient, info.getAddress(), info.getPort());
        }
    }

    private void sendActiveUserList(String userNametoExit) {
        if (userNametoExit != null) {
            int index = 0;
            for (ClientInfo info : clients) {
                if (userNametoExit.equalsIgnoreCase(info.getName())) {
                    clients.remove(index);
                    break;
                }
                index++;
            }
            usersList = "\\userList "; // List of active users on server
            for (ClientInfo info : clients) {
                usersList += info.getName() + "|";
            }
            usersList.replace(userNametoExit, "");
            broadcast(usersList + "\\e");
        } else {
            usersList = "\\userList "; // List of active users on server
            for (ClientInfo info : clients) {
                usersList += info.getName() + "|";
            }
            broadcast(usersList + "\\e");
        }
    }

    private void beforeSendPVMessage(String messagePV, String userPVTargetName, String userPVSenderName) {
        InetAddress targetAddress;
        int targetPort;
//TODO dokonczyć pisanie, aktualnie masz target i adres odbiorcy, co masz to działa
        for (ClientInfo info : clients) {
            if (userPVTargetName.equalsIgnoreCase(info.getName())) {
                targetAddress = info.getAddress();
                targetPort = info.getPort();
                sendPVmessage(userPVTargetName, targetAddress, targetPort, messagePV, userPVSenderName);
                break;
            }
        }
    }

    private void sendPVmessage(String userPVTargetName, InetAddress targetAddress, int targetPort, String messagePV, String userPVSenderName) {
        System.out.println(targetAddress);
        send("\\pvMessage:" + userPVTargetName + "|" + userPVSenderName + "|" + messagePV + "\\e", targetAddress, targetPort);
    }

    private void listen() {
        Thread thread = new Thread("serverListen") {
            public void run() {
                try {
                    while (running) {
                        byte[] data = new byte[1024];
                        DatagramPacket datagramPacket = new DatagramPacket(data, data.length);
                        datagramSocket.receive(datagramPacket);
                        String messageFromClient = new String(data);

                        messageFromClient = messageFromClient.substring(0, messageFromClient.indexOf("\\e")); //end line tag
                        //broadcast(messageFromClient);
                        //  System.out.println(messageFromClient + " wiadomosc od klienta ");
                        if (!isCommand(messageFromClient, datagramPacket)) {
                            broadcast(messageFromClient);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    /*

    Commands :
    // \con -> new user connected
    // \stopServer -> stopping server
    // \ userList -> Active TCP and send active users list
    // \isAvailable-> check if server is available

     */
    private boolean isCommand(String message, DatagramPacket datagramPacket) {
        if (message.startsWith("\\con:")) {
            String name = message.substring(message.indexOf(":") + 1);
            clients.add(new ClientInfo(name, datagramPacket.getAddress(), datagramPacket.getPort()));
            broadcast("\\con:User " + name + " Connected");
            return true;
        } else if (message.startsWith("\\stopServer")) {
            close();
            return true;  //TODO zmienić sposób wyłączenia serwera.
        } else if (message.startsWith("\\isAvailable")) {
            send("true", datagramPacket.getAddress(), datagramPacket.getPort());
            return true;
        } else if (message.startsWith("\\userList")) {
            sendActiveUsersList();
            return true;
        } else if (message.startsWith("\\disc:")) {
            String name = message.substring(message.indexOf(":") + 1);
            userDisconectFromServer(name);
            return true;
        } else if (message.startsWith("\\pvMessage:")) {
            String pvMessageBeforeEncode = message.substring(message.indexOf(":") + 1);
            String usersActiveListString = new String(pvMessageBeforeEncode);

            String[] result = usersActiveListString.split("\\|");
            String targetUserName = result[0];
            String nameSender = result[1];
            String messagePV = result[2];

            System.out.println(messagePV);
            beforeSendPVMessage(messagePV, targetUserName, nameSender);
            return true;
        } else if (message.startsWith("\\startSendingTCPfile:")) {
            String userSenderName = message.substring(message.indexOf(":") + 1);

            startReceivingFileViaTCP(userSenderName);
            return true;
        } else if (message.startsWith("\\startSendingFileToClientTCP")) {
            sendFileToClient();
            return true;
        }
        return false;
    }

    private void startReceivingFileViaTCP(String userSenderName) {
        ServerSocket serverSocket = null;
        Socket socket = null;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;

        try {
            serverSocket = new ServerSocket(2137);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            socket = serverSocket.accept();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            inputStream = socket.getInputStream();
            inputStreamReader = new InputStreamReader((socket.getInputStream()));
            bufferedReader = new BufferedReader(inputStreamReader);
            String fileName = bufferedReader.readLine();
            outputStream = new FileOutputStream("filesOnServer\\" + fileName);
            createHyperlink(fileName, userSenderName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            byte[] bytes = new byte[20 * 1024 * 1024];
            int count;
            while ((count = inputStream.read(bytes)) > 0) {
                outputStream.write(bytes, 0, count);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            outputStream.close();
            inputStream.close();
            socket.close();
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createHyperlink(String fileName, String userSenderName) {
        broadcast("\\newFileOnServer:" + fileName + "|" + userSenderName);
    }

    private void userDisconectFromServer(String name) {
        sendActiveUserList(name);
    }

    private void sendActiveUsersList() {
        sendActiveUserList(null);
    }

    private static void close() {
        running = false;
        datagramSocket.close();
    }

    private void sendFileToClient() {

        ServerSocket serverSocket = null;
        Socket socket = null;
        String fileName = null;

        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;

        try {
            serverSocket = new ServerSocket(2137);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            socket = serverSocket.accept();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            inputStreamReader = new InputStreamReader(socket.getInputStream());
            bufferedReader = new BufferedReader(inputStreamReader);
            fileName = bufferedReader.readLine();
        }catch (Exception e){
            e.printStackTrace();
        }

        try {

            byte[] bytes = new byte[20*1024*1024];
            InputStream inputStream = new FileInputStream("filesOnServer\\" + fileName);
            OutputStream outputStream = socket.getOutputStream();

            int i;
            while ((i = inputStream.read(bytes)) > 0){
                outputStream.write(bytes,0,i);
            }
            outputStream.close();
            inputStream.close();
            socket.close();
            serverSocket.close();
            bufferedReader.close();
            inputStreamReader.close();

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}