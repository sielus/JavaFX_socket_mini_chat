package org.login_window;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.login_window.settings.Settings;
import org.login_window.settings.SettingsControllerGUI;

import java.io.*;
import java.net.URL;

public class LoginGUI extends Application {
    public TextField text_input_user_passwd;

    public String getUserPasswd() {
        return text_input_user_passwd.getText();
    }

    public String getUserName() {
        return text_input_user_name.getText();
    }

    public TextField text_input_user_name;
    LoginController loginController;

    @Override
    public void start(Stage primaryStage) throws Exception {
        URL url = new File("src/main/resources/org/login_gui.fxml").toURI().toURL();
        Parent root = FXMLLoader.load(url);
        primaryStage.setTitle("LogIn to sielus chat");
        primaryStage.setScene(new Scene(root, 707, 374));
        primaryStage.show();
        primaryStage.setResizable(true);
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void logIntoServer(ActionEvent actionEvent) throws Exception { //Button from login sreen
        //ClientGUI clientGUI = new ClientGUI(text_input_user_name.getText(),text_input_server_ip.getText(),Integer.parseInt(text_input_server_port.getText()));
        Settings settings = loadSettingsFromFile();
        if(settings == null){
            settings = new Settings();
        }
        loginController = new LoginController();
        loginController.onLogInButton(getUserName(), getUserPasswd(), settings.getIP(), settings.getUDP(), settings.getTCP(), actionEvent);
    }

    private Settings loadSettingsFromFile() {
        try {
            FileInputStream fileIn = new FileInputStream("settings.dat");
            ObjectInputStream objectIn = new ObjectInputStream(fileIn);
            Object obj = objectIn.readObject();
            objectIn.close();
            return (Settings) obj;
        }catch (Exception e){
            e.getMessage();
        }
        return null;
    }

    public void showSettings(MouseEvent mouseEvent) throws IOException {
        SettingsControllerGUI settingsControllerGUI = new SettingsControllerGUI();
        settingsControllerGUI.showStage();
    }
}


