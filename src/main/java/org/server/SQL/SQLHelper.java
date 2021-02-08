package org.server.SQL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLHelper {
    Connection connection;
    DatabaseCreator databaseCreator;

    public SQLHelper() {
        databaseCreator = new DatabaseCreator();
        connect();
    }

    public void closeConnect() {
        this.connection = null;
    }

    private void connect() {
        if (this.connection == null) {
            this.connection = databaseCreator.connect();
        }
    }

    public boolean checkUserPasswd(String login, String passwd) {
        String sql = "SELECT COUNT(*) AS rowcount FROM USERS WHERE LOGIN == ? AND PASSWD == ?;";
        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, login);
            pstmt.setString(2, passwd);
            ResultSet resultSet = pstmt.executeQuery();
            resultSet.next();
            if(resultSet.getInt("rowcount") == 1){
                return true;
            }else {
                return false;
            }
        } catch (Exception e) {
            e.getCause();
        }
        return false;
    }

    public void addUser(String login, String passwd) {
        String sql = "INSERT INTO USERS(login,passwd) VALUES(?,?)";
        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, login);
            pstmt.setString(2, passwd);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
