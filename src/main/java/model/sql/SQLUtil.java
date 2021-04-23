package model.sql;

public class SQLUtil {

    /*-- Request types -----------------------------------------------------------------------------------------------*/
    public final static byte INSERTREQUESTTYPE = 0;
    public final static byte SELECTREQUESTTYPE = 1;
    public final static byte DELETEREQUESTTYPE = 2;
    public final static byte UPDATEREQUESTTYPE = 3;
    /*----------------------------------------------------------------------------------------------------------------*/

    /*-- Prepared Statments PLAYLISTS --------------------------------------------------------------------------------*/

    public static String INSERTSONG (String userid, String playlist, String song) {
        return "INSERT INTO playlist VALUE (null,'" + userid + "'," + "'" + playlist + "','" + song + "');";
    }

    public static String SELECTCOUNTPLAYLIST (String userid) {
        return "SELECT count(DISTINCT songlist) FROM playlist WHERE userid = '" + userid + "';";
    }

    public static String SELECTALLSONGS (String userid) {
        return "SELECT song,id FROM playlist p WHERE p.userid = '" + userid + "';";
    }

    public static String SELECTSONGSOFPLAYLIST (String userid, String playlist) {
        return "SELECT song FROM playlist WHERE userid = '" + userid + "' AND songlist = '" + playlist + "';";
    }

    public static String SELECTPLAYLISTS (String userid) {
        return "SELECT DISTINCT songlist FROM playlist WHERE userid='" + userid + "';";
    }

    public static String SELECTSONGPLAYLIST (String userid, String playlist) {
        return "SELECT p.songlist,p.song,p.id FROM playlist p WHERE p.userid = '"
                + userid + "' AND p.songlist = '" + playlist + "';";
    }

    public static String DELETESONG (String userid, String id) {
        return "DELETE FROM playlist WHERE userid = '" + userid + "' AND id = '" + id + "';";
    }

    public static String DELETEALLSONGS (String userid) {
        return "DELETE FROM playlist WHERE userid = '" + userid + "';";
    }

    /*----------------------------------------------------------------------------------------------------------------*/

    /*-- Prepared Statments REACTION ---------------------------------------------------------------------------------*/

    public static String SELECTIDTOCOMMAND (String messageID) {
        return "SELECT command FROM reactioncommand WHERE messageid = '" + messageID + "';";
    }

    public static String INSERTCOMMANDMESSAGE (String messageID, String command) {
        return  "INSERT INTO reactioncommand VALUE ('" + messageID + "','" + command + "');";
    }

    public static String DELETECOMMANDMESSAGE (String messageID) {
        return "DELETE FROM reactioncommand WHERE messageid = '" + messageID + "';";
    }

    public static String SELECTMESSAGESIDS () {
        return "SELECT messageid FROM reactioncommand;";
    }

    /*----------------------------------------------------------------------------------------------------------------*/

    /*-- Prepared Statments IDLEGAME ---------------------------------------------------------------------------------*/

    public static String SELECTSAVEGAME (String userid) {
        return "SELECT code FROM idlegamesave WHERE userid = '" + userid + "';";
    }

    public static String UPDATESAVEGAME (String userid, String code) {
        return "UPDATE idlegamesave SET code = '" + code + "' WHERE userid = '" + userid + "';" ;
    }

    public static String INSERTSAVEGAME (String userid, String code) {
        return "INSERT INTO idlegamesave VALUE ('" + userid + "','" + code + "');";
    }

    /*----------------------------------------------------------------------------------------------------------------*/

    /*-- Prepared Statments MANAGMENT --------------------------------------------------------------------------------*/

    public static String INSERTMEMBER (String userid, String nickname) {
        return "INSERT INTO member VALUE ('" + userid + "','" + nickname + "');";
    }

    public static String SELECTMEMBER (String userid) {
        return "SELECT * FROM member WHERE userid = '" + userid + "';";
    }

    /*----------------------------------------------------------------------------------------------------------------*/

    /*-- Prepared Statments TMPCHANNEL -------------------------------------------------------------------------------*/

    public static String INSERTTMPCHANNEL (String channelid, String userid) {
        return "INSERT INTO tmpchannel VALUE ('" + channelid + "','" + userid + "');";
    }

    public static String SELECTTMPCHANNELS (String channelid) {
        return  "SELECT channelid FROM tmpchannel WHERE channelid = '" + channelid + "';";
    }

    public static String DELETETMPCHANNEL (String channelid) {
        return "DELETE FROM tmpchannel WHERE channelid = '" + channelid + "';";
    }

    /*----------------------------------------------------------------------------------------------------------------*/

    /*-- Prepared Statments PROMOTION --------------------------------------------------------------------------------*/

    public static String INSERTPROMO (String link, String name, String userid) {
        return "INSERT INTO promo VALUE (null,0,'" + link + "','" + name + "','" + userid + "','B');";
    }

    public static String UPDATEPROMOSTATUS (String id) {
        return "UPDATE promo SET status = 1 WHERE id = " + id + ";";
    }

    public static String UPDATEPROMOABO (String id) { return "UPDATE promo SET abonement = 'P' WHERE id = " + id + ";"; }

    public static String SELECTPROMO () {
        return "SELECT link,name,userid,abonement FROM promo WHERE status = 1;";
    }

    public static String SELECTOPENPROMO (String userid) {
        return "SELECT id FROM promo WHERE userid = '" + userid + "';";
    }

    public static String SELECTIDTOPROMO (String id) {
        return "SELECT link,name,userid FROM promo WHERE id = " + id + ";";
    }

    /*----------------------------------------------------------------------------------------------------------------*/
}
