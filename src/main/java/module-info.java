module org {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.sql;
    requires java.persistence;
    requires net.bytebuddy;

    opens org to javafx.fxml;
    exports org.client;
    exports org.login_window;
    exports org.server;
    exports org.sql_manager;


}