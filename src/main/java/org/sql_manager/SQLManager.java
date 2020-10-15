package org.sql_manager;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;


public class SQLManager{


    private final String url = "jdbc:postgresql://localhost/sielusDB";
    private final String user = "postgres";
    private final String password = "7992";

    public void connect() {
//        Connection conn = null;
//        try {
//            conn = DriverManager.getConnection(url, user, password);
//            System.out.println("Connected to the PostgreSQL server successfully.");
//        } catch (SQLException e) {
//            System.out.println(e.getMessage());
//        }
//
//        return conn;
    }







}
