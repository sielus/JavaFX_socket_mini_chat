package org.login_window.settings;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.*;
import java.net.URL;

public class SettingsControllerGUI {
    @FXML
    public TextField text_server_ip;
    @FXML
    public TextField text_port_tcp;
    @FXML
    public TextField text_port_udp;

    private Parent root;
    private Stage stage;

    private String getTextServerIp() {
        return text_server_ip.getText();
    }

    private String getTextServerTcp() {
        return text_port_tcp.getText();
    }

    private String getTextServerUdp() {
        return text_port_udp.getText();
    }

    public void setTextServerIp(String serverIp) {
        TextField textField = (TextField) root.lookup("#text_server_ip");
        textField.setText(serverIp);
    }

    public void setTextServerTcp(String serverTcp) {
        TextField textField = (TextField) root.lookup("#text_port_tcp");
        textField.setText(serverTcp);
    }

    public void setTextServerUdp(String serverUdp) {
        TextField textField = (TextField) root.lookup("#text_port_udp");
        textField.setText(serverUdp);
    }

    public void showStage() throws IOException {
        stage = new Stage();
        URL url = new File("src/main/resources/org/settings_gui.fxml").toURI().toURL();
        root = FXMLLoader.load(url);
        stage.setScene(new Scene(root));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Settings");
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
        settings.setIP(getTextServerIp());
        settings.setTCP(getTextServerTcp());
        settings.setUDP(getTextServerUdp());
        try {
            FileOutputStream fileOut = new FileOutputStream("settings.dat");
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(settings);
            objectOut.close();
        }catch (Exception e){
           e.getMessage();
        }
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
            }catch (FileNotFoundException | ClassNotFoundException fileNotFoundException){
            }
        }
    }
}
