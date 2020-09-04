package login_window;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginGUI extends Application {

    public String getUserPasswd() {
        return text_input_user_passwd.getText();
    }

    public TextField text_input_user_passwd;

    public int getPort() {
        return Integer.parseInt(text_input_server_port.getText());
    }

    public String getUserName() {
        return text_input_user_name.getText();
    }

    public String getServerIP() {
        return text_input_server_ip.getText();
    }

    public TextField text_input_server_port;
    public TextField text_input_user_name;
    public TextField text_input_server_ip;
    LoginController loginController;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("login_gui.fxml"));
        primaryStage.setTitle("LogIn to sielus chat");
        primaryStage.setScene(new Scene(root, 385, 189));
        primaryStage.show();
        primaryStage.setResizable(false);
    }

    public static void main(String[] args) {
        launch(args);
    }



    public void logIntoServer(ActionEvent actionEvent) { //Button from login sreen
        //ClientGUI clientGUI = new ClientGUI(text_input_user_name.getText(),text_input_server_ip.getText(),Integer.parseInt(text_input_server_port.getText()));
        loginController = new LoginController();
        loginController.onLogInButton(getUserName(),getUserPasswd(),getServerIP(),getPort(),actionEvent);
    }
}


