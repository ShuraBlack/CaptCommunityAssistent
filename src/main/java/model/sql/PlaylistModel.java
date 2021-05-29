package model.sql;

public class PlaylistModel {

    private int id;
    private String userid;
    private String songlist;
    private String song;

    public PlaylistModel(int id, String userid, String songlist, String song) {
        this.id = id;
        this.userid = userid;
        this.songlist = songlist;
        this.song = song;
    }

    public int getId() {
        return id;
    }

    public String getUserid() {
        return userid;
    }

    public String getSonglist() {
        return songlist;
    }

    public String getSong() {
        return song;
    }
}
