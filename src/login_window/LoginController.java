package login_window;

import client.ClientGUI;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class LoginController {


    public void onLogInButton(String userName, String serverIP, int port, ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/client/client_gui.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root1));
            stage.show();
            ClientGUI.createClient(userName,serverIP,port,stage);
            ((Node)(actionEvent.getSource())).getScene().getWindow().hide();

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
