package test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class test {
    public static void main(String[] args) {
        Connection con = null;

        // JDBC URL for SID-based connection
        String url = "jdbc:oracle:thin:@localhost:1521/XEPDB1";  // Replace 'ora11g' with your actual SID
        String username = "C##SRIK";                        // Replace with your DB username
        String password = "user";                       // Replace with your DB password

        try {
            // Load Oracle JDBC driver (for older versions; optional in newer Java versions)
            Class.forName("oracle.jdbc.driver.OracleDriver");

            // Connect to the database
            con = DriverManager.getConnection(url, username, password);
            System.out.println("Connection successful!");

        } catch (ClassNotFoundException e) {
            System.out.println("Oracle JDBC Driver not found.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Connection failed.");
            e.printStackTrace();
        } finally {
            try {
                if (con != null) con.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}
