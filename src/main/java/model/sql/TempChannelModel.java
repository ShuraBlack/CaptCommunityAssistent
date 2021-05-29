package model.sql;

public class TempChannelModel {

    private String channelid;
    private String userid;

    public TempChannelModel(String channelid, String userid) {
        this.channelid = channelid;
        this.userid = userid;
    }

    public String getChannelid() {
        return channelid;
    }

    public String getUserid() {
        return userid;
    }
}
