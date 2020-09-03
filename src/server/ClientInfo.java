package server;

import java.net.InetAddress;

public class ClientInfo {
    private InetAddress address;
    private int port;
    private String name;
    private int id;

    public ClientInfo(String name, InetAddress address, int port) {
        this.address = address;
        this.port = port;
        this.name = name;
    }

    public InetAddress getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public String getName() {
        return name;
    }






}
