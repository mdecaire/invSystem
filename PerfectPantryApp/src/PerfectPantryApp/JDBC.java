package PerfectPantryApp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLNonTransientConnectionException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;



/**
 *
 * @author Josh
 */
// Reference:
// http://www.mysqltutorial.org/connecting-to-mysql-using-jdbc-driver/
//https://docs.oracle.com/javase/tutorial/jdbc/basics/retrieving.html
public class JDBC {
    
        /**
     * Get database connection
     * @return a Connection object
     * @throws SQLException
     */
    public static Connection getConnection() throws SQLException {
        Connection conn = null;
        
        try (FileInputStream f = new FileInputStream("db.properties")) {
 
            // load the properties file
            Properties pros = new Properties();
            pros.load(f);
 
            // assign db parameters
            String url = pros.getProperty("url");
            String user = pros.getProperty("user");
            String password = pros.getProperty("password");
            
            // create a connection to the database
            conn = DriverManager.getConnection(url, user, password);
        } catch (IOException | SQLNonTransientConnectionException e) {
            conn=null;
            System.out.println("File not found or invalid, writing db.properties");
            //Logger.getLogger(InventoryData.class.getName()).log(Level.SEVERE, null, e);
        }
        return conn;
    }
    
  
    
    
   
    
}



