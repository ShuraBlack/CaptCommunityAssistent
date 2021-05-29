package model.blackjack;

import java.util.LinkedList;
import java.util.List;

public class Player {

    String userid;
    List<String> hand = new LinkedList<>();

    public Player(String userid) {
        this.userid = userid;
    }

    public String getUserid() {
        return userid;
    }

    public void addCard (String card) {
        hand.add(card);
    }

    public String getHand () {
        StringBuilder s = new StringBuilder();
        for (String card : hand) {
            String[] args = card.split(" ");
            switch (args[1]) {
                case "kreuz": s.append("♣ ").append(args[0]).append("\n");
                    break;
                case "pike": s.append("♠ ").append(args[0]).append("\n");
                    break;
                case "herz": s.append("♥ ").append(args[0]).append("\n");
                    break;
                case "karo": s.append("♦ ").append(args[0]).append("\n");
                    break;
            }
        }
        return s.toString();
    }

    public int getValue () {
        int value = 0;
        for (String card : hand) {
            String[] args = card.split(" ");
            if (args[0].equals("Bube")
                || args[0].equals("Dame")
                || args[0].equals("Koenig")) {
                value += 10;
            } else if (args[0].equals("Ass")) {
                value += 11;
            } else {
                value += Integer.parseInt(args[0]);
            }
        }
        return value;
    }
}
