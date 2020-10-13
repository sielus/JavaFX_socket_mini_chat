package com.sql_manager;

import java.sql.*;

public class SQLManager {
    private final String url = "jdbc:postgresql://localhost/chatserver";
    private final String user = "postgres";
    private final String password = "7992";
    Connection connection = connect();

    public Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to the PostgreSQL server successfully.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return conn;
    }

    public void getAllData() throws SQLException {
        PreparedStatement st = connection.prepareStatement("SELECT * FROM users.users_list");

        ResultSet rs = st.executeQuery();
        while (rs.next()) {
            System.out.println("From SQL : " + rs.getString(1));
        }
        rs.close();
        st.close();
    }


}
