package org.login_window.client;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class DialogsManager {
    void showKickedAlert() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Server alert");
                alert.setHeaderText(null);
                alert.setContentText("You been kicked from server");
                Optional<ButtonType> result = alert.showAndWait();
                if(result.get() == ButtonType.OK){
                    ClientServer.disconnectFromServer();
                }
            }
        });
    }

    public void showBannedAlert() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Server alert");
                alert.setHeaderText(null);
                alert.setContentText("You been banned from server");
                Optional<ButtonType> result = alert.showAndWait();
                if(result.get() == ButtonType.OK){
                    ClientServer.disconnectFromServer();
                }
            }
        });
    }

    public void showErrorAllert(String title, String header, String details){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(details);
        alert.showAndWait();
    }
}
