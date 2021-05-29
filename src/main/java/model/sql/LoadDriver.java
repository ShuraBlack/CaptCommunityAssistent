package model.sql;

import startup.DiscordBot;
import com.mysql.cj.log.Slf4JLogger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

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
            logger = new Slf4JLogger("SQL.Managment");
            conn = DriverManager.getConnection(URL,username,password);
            stmt = conn.createStatement();
        } catch (SQLException e) {
            logger.logError("Couldnt etablish connection to MySQL database", e);
        }
    }

    /**
     * Execution for the SQL Query Statments (no Prepared Statments)
     * @param command is your SQL Statment
     * @return the Resultset of your Query Statment
     * @apiNote uses the SQL Connection/J API - https://www.mysql.com/de/products/connector/
     * @since 10.03.2021
     */
    public ResultSet executeSQL (String command) {

        try {

            if (command.startsWith("INSERT")
                    || command.startsWith("DELETE")
                    || command.startsWith("UPDATE")) {
                stmt.executeUpdate(command);

            } else if (command.startsWith("SELECT")) {

                rs = stmt.executeQuery(command);

            }

        } catch (SQLException ex){
            logger.logError("Request got denied" +
                    "\nRequest: " + command +
                    "\nSQLException: " + ex.getMessage() +
                    "\nSQLState: " + ex.getSQLState() +
                    "\nVendorError: " + ex.getErrorCode(),ex);
        }
        return rs;
    }

    public LoadDriver executeSQLModelable (String command) {

        try {
            if (command.startsWith("INSERT")
                    || command.startsWith("UPDATE")
                    || command.startsWith("DELETE")) {

                stmt.executeUpdate(command);

            } else if (command.startsWith("SELECT")) {

                rs = stmt.executeQuery(command);

            }

        } catch (SQLException ex){
            logger.logError("Request got denied" +
                    "\nRequest: " + command +
                    "\nSQLException: " + ex.getMessage() +
                    "\nSQLState: " + ex.getSQLState() +
                    "\nVendorError: " + ex.getErrorCode(),ex);
        }
        return this;
    }

    public List<IdleGameSaveModel> getIdlegameSaveModels () {
        List<IdleGameSaveModel> list = new LinkedList<>();
        try {
            while (rs.next()) {
                IdleGameSaveModel igsm = new IdleGameSaveModel(
                        rs.getString(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getLong(4),
                        rs.getInt(5)
                );
                list.add(igsm);
            }
            rs.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return list;
    }

    public List<PlaylistModel> getPlaylistModels () {
        List<PlaylistModel> list = new LinkedList<>();
        try {
            while (rs.next()) {
                PlaylistModel plm = new PlaylistModel(
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4)
                );
                list.add(plm);
            }
            rs.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return list;
    }

    public List<PromoModel> getPromoModels () {
        List<PromoModel> list = new LinkedList<>();
        try {
            while (rs.next()) {
                PromoModel pm = new PromoModel(
                        rs.getInt(1),
                        rs.getInt(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getString(5),
                        rs.getString(6)
                );
                list.add(pm);
            }
            rs.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return list;
    }

    public List<TempChannelModel> getTempChannelModels () {
        List<TempChannelModel> list = new LinkedList<>();
        try {
            while (rs.next()) {
                TempChannelModel tcm = new TempChannelModel(
                        rs.getString(1),
                        rs.getString(2)
                );
                list.add(tcm);
            }
            rs.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return list;
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
            } catch (SQLException sqlEx) {
            } // ignore

            conn = null;
        }

        if (logger != null) {
            logger = null;
        }
    }
}
