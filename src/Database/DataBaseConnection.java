/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Database;

import java.sql.*;

/**
 *
 * @author cosmi
 */
public class DataBaseConnection {

    private static final String DEFAULT_DRIVERNAME = "oracle.jdbc.driver.OracleDriver";

    private static final String DEFAULT_SERVERNAME = "localhost";
    private static final String DEFAULT_SERVERPORT = "1521";
    private static final String DEFAULT_SID = "xe";
    private static final String DEFAULT_URL = "jdbc:oracle:thin:@" + DEFAULT_SERVERNAME + ":" + DEFAULT_SERVERPORT + ":" + DEFAULT_SID;

    private static final String DEFAULT_USERNAME = "cosmin29";
    private static final String DEFAULT_PASSWORD = "cosmin29";

    private Connection conn;
    
    public DataBaseConnection() {
        
        conn = null;
        
        try {

            Class.forName(DEFAULT_DRIVERNAME);

            conn = DriverManager.getConnection(DEFAULT_URL, DEFAULT_USERNAME, DEFAULT_PASSWORD);
            System.out.println("Connection successfull to: " + DEFAULT_URL);

        } catch (ClassNotFoundException e) {

            System.out.println("Could not find the database driver " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Could not connect to the database " + e.getMessage());
        }
    }

    public DataBaseConnection(String serverName, String serverPort, String sid, String username, String password) {
        
        conn = null;
        
        try {

            Class.forName(DEFAULT_DRIVERNAME);
            
            String url = "jdbc:oracle:thin:@" + serverName + ":" + serverPort + ":" + sid;

            conn = DriverManager.getConnection(url, username, password);
            System.out.println("Connection successfull to: " + url);

        } catch (ClassNotFoundException e) {

            System.out.println("Could not find the database driver " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Could not connect to the database " + e.getMessage());
        }
    }

    public Connection getConnection() {   
        return conn;
    }
    
}
