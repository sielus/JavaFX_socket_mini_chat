package com.server;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.File;
import java.io.IOException;

import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ChatServerGUI extends Application {

    private static Server server;
    public Button serverStartButton;
    public Button serverStopButton;
    public static Text serverStatus;
    public static Stage stage;
    public static Parent parent;
    private static int eventsCount;
    private static int seconds;
    private static LineChart <Number,Number>lineChart;
    private static XYChart.Series<Number,Number> series;
    static boolean runChart = false;
    public String getServerPortUDP() {
        return serverPortUDP.getText();
    }

    public String getServerPortTCP() {
        return serverPortTCP.getText();
    }

    public TextField serverPortUDP;
    public TextField serverPortTCP;

    @FXML
    static ListView<String> activeUserList;

    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) throws IOException {
        URL url = new File("src/main/resources/com/server_gui.fxml").toURI().toURL();
        Parent root = FXMLLoader.load(url);
        primaryStage.setTitle("Server chat GUI manager");
        primaryStage.setScene(new Scene(root, 844, 430));
        eventsCount = 0;
        primaryStage.show();
        primaryStage.setResizable(false);
        stage = primaryStage;
        parent = primaryStage.getScene().getRoot();
        activeUserList = (ListView<String>) parent.lookup("#fx_list_users");

        lineChart = (LineChart)parent.lookup("#lineChart");
        lineChart.setLegendVisible(false);
        series = new XYChart.Series<Number,Number>();
        lineChart.getData().add(series);

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                Platform.exit();
                System.exit(0);
            }
        });

        activeUserList.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                onSelectedActiveUser(activeUserList.getSelectionModel().getSelectedItem());
            }
        });
    }

    private void onSelectedActiveUser(String selectedUser) {
        adminToolCommends adminToolCommends = new adminToolCommends();
        String commend = adminToolCommends.showDialog(selectedUser);
        if(!commend.isEmpty()){
            System.out.println(commend);
            switch (commend){
                case "kick":
                    kickUser(selectedUser,"\\" + commend) ;
                    break;

                case "ban":
                    banUser(selectedUser,"\\" + commend) ;
                    break;
            }
        }
    }

    private void banUser(String userToKick,String commend) {
        server.sendCommendToUser(userToKick,commend);
        server.sendActiveUserList(userToKick);

    }

    private void kickUser(String userToKick,String commend) {
        server.sendCommendToUser(userToKick,commend);
        server.sendActiveUserList(userToKick);
    }

    public void startServer(ActionEvent actionEvent) {
        server = new Server();
        String portUDP = getServerPortUDP();
        String portTCP = getServerPortTCP();
        eventsCount +=1;
        runChart = true;
        Runnable helloRunnable = new Runnable() {
            public void run() {
                seconds += 1;
                refreshChart();
            }
        };
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(helloRunnable, 0, 5, TimeUnit.SECONDS);

        if(portUDP!=null && portTCP != null) {
            server.start(Integer.parseInt(portUDP));
            server.portTCP = Integer.parseInt(portTCP);
            serverStartButton.setDisable(true);
            serverStopButton.setDisable(false);
            serverPortUDP.setDisable(true);
            serverPortTCP.setDisable(true);
        }
    }

    private static void refreshChart() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {

                if(runChart){
                    addUsersToUsersList(server.getAllusersName());
                    series.getData().add(new XYChart.Data<Number, Number>(seconds, eventsCount));
                    if(seconds%50==0){
                        series.getData().clear();
                        seconds = 0;
                    }
                    eventsCount = 0;
                }
            }
        });
    }



    public static void addUsersToUsersList(ArrayList list){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                activeUserList.getItems().removeAll();
                activeUserList.getItems().clear();
                activeUserList.getItems().addAll(list);
                activeUserList.refresh();
            }
        });
    }


    public void stopServer(ActionEvent actionEvent) {
        stopNow();
    }

    private void stopNow() {
        Server.broadcast("\\disconnect");
        server.thread.interrupt();
        server.close();
        serverStartButton.setDisable(false);
        serverStopButton.setDisable(true);
        serverPortUDP.setDisable(false);
        serverPortTCP.setDisable(false);
        setServerStatus("Stopped");
        eventsCount +=1;
        seconds = 0;
        runChart = false;
        server.clearUsersList();
        activeUserList.getItems().removeAll();
        activeUserList.getItems().clear();

    }

    public static void printLogServer(String message){
        TextArea serverLogs = (TextArea) parent.lookup("#serverLogs");
        serverLogs.appendText(message +"\n");
        eventsCount +=1;

    }

    public static void setServerStatus(String status){
        Text userMessage = (Text) parent.lookup("#serverStatus");
        userMessage.setText(status);
        eventsCount +=1;

    }


}
