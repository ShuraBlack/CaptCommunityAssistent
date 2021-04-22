package Model.sql;

import Startup.DiscordBot;
import com.mysql.cj.log.Slf4JLogger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Class for starting a Connection to the Pebblehost Database
 * @since 10.03.2021
 * @author Klotz, Ronny
 * @version 0.0.3
 */
public class LoadDriver {

    /**
     * Holds the connection to the mySQL DataBase - by Pebblehost
     */
    private Connection conn = null;

    /**
     * Creates and hold the Query Statment you want to send
     */
    private Statement stmt = null;

    /**
     * Holds the Resultset of the Query Statment
     */
    private ResultSet rs = null;

    /**
     * Logger for logging the requested queries and there result
     */
    private Slf4JLogger  logger = null;

    /**
     * Creates the connection to the Database
     */
    public LoadDriver () {
        try {
            String URL = DiscordBot.PROPERTIES.getProperty("url");
            String username = DiscordBot.PROPERTIES.getProperty("username");
            String password = DiscordBot.PROPERTIES.getProperty("password");
            conn = DriverManager.getConnection(URL,username,password);
            logger = new Slf4JLogger("SQL.Managment");
            logger.logInfo("Opens Connection to mySQL Database");
            stmt = conn.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Execution for the SQL Query Statments (no Prepared Statments)
     * @param command is your SQL Statment
     * @param type is the typ of the Statment (SELECT,INSERT,etc.). You can use the SQLRequests Variables for more clean code
     * @return the Resultset of your Query Statment
     * @apiNote uses the SQL Connection/J API - https://www.mysql.com/de/products/connector/
     * @since 10.03.2021
     */
    public ResultSet executeSQL (String command, byte type) {

        try {
            if (type == 0) {
                stmt.executeUpdate(command);
                logger.logInfo("INSERT Request granted");

            } else if (type == 1) {
                rs = stmt.executeQuery(command);
                logger.logInfo("SELECT Request granted");

            } else if (type == 2) {
                stmt.executeUpdate(command);
                logger.logInfo("DELETE Request granted");

            } else if (type == 3) {
                stmt.executeUpdate(command);
                logger.logInfo("UPDATE Request granted");
            }

        } catch (SQLException ex){
            logger.logError("Request got denied" +
                    "\nRequest: " + command +
                    "\nType: " + type +
                    "\nSQLException: " + ex.getMessage() +
                    "\nSQLState: " + ex.getSQLState() +
                    "\nVendorError: " + ex.getErrorCode(),ex);
        }
        return rs;
    }

    /**
     * Closed the Statment,Resultset and Connection for clean up
     * @since 10.03.2021
     */
    public void close () {

        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException sqlEx) {
            } // ignore

            rs = null;
        }

        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException sqlEx) {
            } // ignore

            stmt = null;
        }

        if (conn != null) {
            try {
                conn.close();
                logger.logInfo("Close Connection to mySQL Database");
            } catch (SQLException sqlEx) {
            } // ignore

            conn = null;
        }

        if (logger != null) {
            logger = null;
        }
    }
}
