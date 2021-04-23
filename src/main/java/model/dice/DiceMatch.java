package model.dice;

import net.dv8tion.jda.api.entities.Member;

import java.util.Random;
import java.util.Vector;

public class DiceMatch {

    private final Member iniziator;
    private int roleIni;

    private Member opponent;
    private int roleOppo;

    private Member winner;

    public DiceMatch(Member iniziator) {
        this.iniziator = iniziator;
    }

    public void acceptMatch (Member opponent) {
        this.opponent = opponent;
        generateRandomWinner();
    }

    private void generateRandomWinner () {
        Random rdm = new Random();
        roleIni = rdm.nextInt((6 - 1) + 1) + 1;
        roleOppo = rdm.nextInt((6 - 1) + 1) + 1;

        if (roleIni > roleOppo) {
            winner = iniziator;
        } else if (roleIni == roleOppo) {
            winner = null;
        } else {
           winner = opponent;
        }
    }

    public Vector<Integer> getValues() {
        Vector<Integer> values = new Vector<>();
        values.add(0,roleIni);
        values.add(1,roleOppo);
        return values;
    }

    public Member getWinner() {
        return this.winner;
    }

    public Member getIniziator() {
        return iniziator;
    }

    public Member getOpponent() {
        return opponent;
    }
}
