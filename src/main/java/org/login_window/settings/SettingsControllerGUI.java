package org.login_window.settings;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.login_window.client.DialogsManager;
import java.io.*;
import java.net.URL;

public class SettingsControllerGUI {
    @FXML
    public TextField text_server_ip;
    @FXML
    public TextField text_port_tcp;
    @FXML
    public TextField text_port_udp;

    private static Parent root;
    private static Stage stage;

    private String getTextServerIp() {
        return text_server_ip.getText();
    }

    private String getTextServerTcp() {
        return text_port_tcp.getText();
    }

    private String getTextServerUdp() {
        return text_port_udp.getText();
    }

    private void setTextServerIp(String serverIp) {
        TextField textField = (TextField) root.lookup("#text_server_ip");
        textField.setText(serverIp);
    }

    private void setTextServerTcp(String serverTcp) {
        TextField textField = (TextField) root.lookup("#text_port_tcp");
        textField.setText(serverTcp);
    }

    private void setTextServerUdp(String serverUdp) {
        TextField textField = (TextField) root.lookup("#text_port_udp");
        textField.setText(serverUdp);
    }

    public void showStage() throws IOException {
        stage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/org/settings_gui.fxml"));
        root = loader.load();
        stage.setScene(new Scene(root));
        stage.getIcons().add(new Image(SettingsControllerGUI.class.getResourceAsStream("/icons/settings_50px.png")));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Settings");
        stage.setResizable(false);
        loadSettingsFromFile();
        stage.showAndWait();
    }

    public void saveNewSettings(ActionEvent actionEvent) {
        saveSettingsToFile();
    }

    public void defaultSettings(ActionEvent actionEvent) {
        setTextServerIp("192.168.1.16");
        setTextServerTcp("4445");
        setTextServerUdp("4444");
    }

    private void saveSettingsToFile(){
        Settings settings = new Settings();
        boolean checking = true;
        String ip = getTextServerIp();
        String tcp = getTextServerTcp();
        String udp = getTextServerUdp();
        DialogsManager dialogsManager = new DialogsManager();

        if(checkIp(ip)){
            settings.setIP(getTextServerIp());
        }else {
            dialogsManager.showErrorAllert("Settings error","Wrong IP address","Bad address structure!");
            checking = false;
        }

        if(isNumber(tcp)){
            settings.setTCP(tcp);
        }else {
            dialogsManager.showErrorAllert("Settings error","Wrong TCP port","Default is 4445");
            checking = false;
        }

        if(isNumber(udp)){
            settings.setUDP(udp);
        }else {
            checking = false;
            dialogsManager.showErrorAllert("Settings error","Wrong UDP port","Default is 4444");
        }

        if (checking) {
            try {
                FileOutputStream fileOut = new FileOutputStream("settings.dat");
                ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
                objectOut.writeObject(settings);
                objectOut.close();
                stage.close();
            }catch (Exception e){
                //e.getMessage();
            }
        }
    }
    public static boolean isNumber(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double test = Integer.parseInt(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    private boolean checkIp(String ip) {
        String PATTERN = "^((0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)\\.){3}(0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)$";
        return ip.matches(PATTERN);
    }

    private void loadSettingsFromFile() throws IOException {
        File f = new File("settings.dat");
        if(f.exists() && !f.isDirectory()) {
            try {
                FileInputStream fileIn = new FileInputStream("settings.dat");
                ObjectInputStream objectIn = new ObjectInputStream(fileIn);
                Object obj = objectIn.readObject();
                objectIn.close();
                Settings loaded = (Settings) obj;

                setTextServerIp(loaded.getIP());
                setTextServerTcp(String.valueOf(loaded.getTCP()));
                setTextServerUdp(String.valueOf(loaded.getUDP()));
            }catch (FileNotFoundException | ClassNotFoundException ignored){
            }
        }
    }
}
