package org.server.SQL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class SQLHelper {
    public Connection connect() throws SQLException {
        String url = "jdbc:postgresql://localhost/chatserver";
        Properties props = new Properties();
        props.setProperty("user","postgres");
        props.setProperty("password","7992");
        Connection conn = DriverManager.getConnection(url, props);
        return conn;
    }
}
