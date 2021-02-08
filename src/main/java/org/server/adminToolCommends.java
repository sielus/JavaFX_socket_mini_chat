package org.server;

import javafx.scene.control.TextInputDialog;

import java.util.Optional;

public class adminToolCommends {

    String showDialog(String userName){
        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("Admin Commends for " + userName);
        dialog.setHeaderText("kick | ban");
        dialog.setContentText("Commend:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()){
           return(result.get());
        }
        return null;
    }

}
