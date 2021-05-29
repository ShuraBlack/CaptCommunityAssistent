package model.idle;

import java.util.Random;

public class War {

    String eventname = "";
    int strength;
    int enemystrength = 0;
    int duration = 0;
    int result = 0;

    public War (int strength) {
        setEventname();
        this.strength = strength;
        setEnemystrength();
        setResult();
        setDuration();
    }

    public War(String eventname, int strength, int enemystrength, int duration) {
        this.eventname = eventname;
        this.strength = strength;
        this.enemystrength = enemystrength;
        this.duration = duration;
    }

    public String getEventname() {
        return eventname;
    }

    public int getStrength() {
        return strength;
    }

    public int getEnemystrength() {
        return enemystrength;
    }

    public int getDuration() {
        return duration;
    }

    public int getResult () {
        return result;
    }

    private void setEventname () {
        String[] names = {"Das benachbarte Land spricht einen Krieg gegen euch aus",
                "Das Volk ist unzufrieden mit euren militärischen Maßnahmen und rebelliert",
                "Es herrscht Gewalt und Randale in den Straßen deiner Stadt",
                "Eure Friedensverträge wurden abgelehnt und die Länder rüsten ihre Armen auf"};
        Random rdm = new Random();
        eventname = names[rdm.nextInt(names.length)];
    }

    private void setEnemystrength () {
        Random rdm = new Random();
        enemystrength = rdm.nextInt(((strength * 2) - (strength / 3)) + 1 ) + (strength / 3);
    }

    public void setResult () {
        Random rdm = new Random();
        int winticket = rdm.nextInt(strength + enemystrength + 1);
        if (winticket < strength) {
            result = 1;
        } else {
            result = -1;
        }
    }

    private void setDuration () {
        int[] times = {5,10,15,20,25};
        Random rdm = new Random();
        duration = times[rdm.nextInt(times.length)];
    }
}
