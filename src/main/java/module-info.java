module org {
    requires java.desktop;
    requires java.sql;
    requires javafx.graphics;
    requires javafx.fxml;
    requires javafx.controls;


    opens org to javafx.fxml;
    exports org.login_window.client;
    exports org.login_window;
    exports org.server;
    exports org.login_window.settings;


}