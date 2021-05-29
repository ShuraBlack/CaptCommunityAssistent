package model.idle;

import java.util.Random;

public class Peace {

    String eventname = "";
    double bonusvalue = 0.0;
    int duration = 0;

    public Peace () {
        setEventname();
        setBonusvalue();
        setDuration();
    }

    public Peace(String eventname, double bonusvalue, int duration) {
        this.eventname = eventname;
        this.bonusvalue = bonusvalue;
        this.duration = duration;
    }

    public String getEventname() {
        return eventname;
    }

    public double getBonusvalue() {
        return bonusvalue;
    }

    public int getDuration() {
        return duration;
    }

    private void setEventname () {
        String[] names = {"Die Königin ruft zu einem Fest auf und läd alle befreundeten Länder dazu ein",
        "Der Jahrestag des Königreiches wird gefeiert und das Volk freut sich darüber",
        "Die Bevölkerung feiert zu ehren des Königs ein Blumenfest",
        "Ein unbekanntest Land akzeptiert den Friedensvertrag",
        "Die Bewöhner des Landes fühlen sich in ihrem Königreich sicher"};
        Random rdm = new Random();
        eventname = names[rdm.nextInt(names.length)];
    }

    private void setBonusvalue () {
        double[] values = {1.5,2.0,2.5,3.0,3.5,4.0,4.5,5.0};
        Random rdm = new Random();
        bonusvalue = values[rdm.nextInt(values.length)];
    }

    private void setDuration () {
        int[] times = {5,10,15,20,25,30,35,40,45,50};
        Random rdm = new Random();
        duration = times[rdm.nextInt(times.length)];
    }
}
