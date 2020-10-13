module com {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;


    opens com to javafx.fxml;
    exports com.server;
    exports com.login_window;
    exports com.client;

}