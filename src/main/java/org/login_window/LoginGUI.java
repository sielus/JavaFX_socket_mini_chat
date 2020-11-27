package org.login_window;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.login_window.settings.Settings;
import org.login_window.settings.SettingsControllerGUI;

import java.io.*;
import java.net.URL;

public class LoginGUI extends Application {
    public PasswordField text_input_user_passwd;

    public String getUserPasswd() {
        return text_input_user_passwd.getText();
    }

    public String getUserName() {
        return text_input_user_name.getText();
    }

    public TextField text_input_user_name;
    LoginController loginController;
    private static Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/org/login_gui.fxml"));
        Parent root = loader.load();

        primaryStage.setTitle("LogIn to sielus chat");
        primaryStage.setScene(new Scene(root, 800, 355));
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.show();
        LoginGUI.primaryStage = primaryStage;
        primaryStage.setResizable(false);
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

    public void exit(MouseEvent mouseEvent) {
        LoginGUI.primaryStage.close();
        System.exit(0);
    }
}


