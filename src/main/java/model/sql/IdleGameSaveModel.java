package model.sql;

public class IdleGameSaveModel {

    private String userid;
    private final String code;
    private final String event;
    private final long gem;
    private final int prestige;

    public IdleGameSaveModel(String userid, String code, String event, long gem, int prestige) {
        this.userid = userid;
        this.code = code;
        this.event = event;
        this.gem = gem;
        this.prestige = prestige;
    }

    public String getCode() {
        return code;
    }

    public String getEvent() {
        return event;
    }

    public long getGem() {
        return gem;
    }

    public int getPrestige() {
        return prestige;
    }
}
