package org.server.SQL;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseCreator {
    private String dbUrl = "jdbc:sqlite:database.db";
    public DatabaseCreator(){
        createDatabase();
    }
    private void createDatabase() {
        File file = new File("database.db");
        if (!file.exists()) {
            try (Connection conn = DriverManager.getConnection(dbUrl)) {
                if (conn != null) {
                    System.out.println("A new database has been created.");
                    createUsersTable(conn);
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    protected Connection connect() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(dbUrl);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        if (connection != null) {
            System.out.println("Connection to SQLite has been established.");
            return connection;
        } else {
            System.out.println("SQL fail");
            return null;
        }
    }

    private void createUsersTable(Connection connection) {
         String loginsTable = "CREATE TABLE IF NOT EXISTS USERS (\n" +
                 "id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                 "                 login text NOT NULL,\n" +
                 "                 passwd text NOT NULL);";
         try {
             Statement stmt = connection.createStatement();
             stmt.execute(loginsTable);
         }catch (Exception e){
             e.getCause();
         }
    }
}
