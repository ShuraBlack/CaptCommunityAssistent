package model.sql;

public class PromoModel {

    private int id;
    private int status;
    private String link;
    private String name;
    private String userid;
    private String abonement;

    public PromoModel(int id, int status, String link, String name, String userid, String abonement) {
        this.id = id;
        this.status = status;
        this.link = link;
        this.name = name;
        this.userid = userid;
        this.abonement = abonement;
    }

    public int getId() {
        return id;
    }

    public int getStatus() {
        return status;
    }

    public String getLink() {
        return link;
    }

    public String getName() {
        return name;
    }

    public String getUserid() {
        return userid;
    }

    public String getAbonement() {
        return abonement;
    }
}
