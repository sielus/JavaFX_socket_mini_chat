package org.login_window.settings;

import java.io.*;

public class Settings implements Serializable {
    private String TCP = "4445";
    private String UDP = "4444";
    private String IP = "192.168.1.16";

    public void setTCP(String TCP) {
        this.TCP = TCP;
    }

    public void setUDP(String UDP) {
        this.UDP = UDP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public int getTCP() {
        return Integer.parseInt(TCP);
    }

    public int getUDP() {
        return Integer.parseInt(UDP);
    }

    public String getIP() {
        return IP;
    }
}
