package model.blackjack;

import com.mysql.cj.log.Slf4JLogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class BlackJackTask {

    private final Player iniziator;
    private boolean inizCheck = false;
    private Player opponent = null;
    private boolean oppoCheck = false;
    private long bet = 0;

    private final List<String> cards = loadCards();

    public BlackJackTask(String userid) {
        this.iniziator = new Player(userid);
    }

    public BlackJackTask(String userid, long bet) {
        this.iniziator = new Player(userid);
        this.bet = bet;
    }

    public void joinGame (String userid) {
        this.opponent = new Player(userid);
    }

    public int setCard (String userid, String card) {
        if (iniziator.userid.equals(userid)) {
            iniziator.addCard(card);
            if (iniziator.getValue() > 21) {
                return 1;
            }
        } else if (opponent.userid.equals(userid)) {
            opponent.addCard(card);
            if (opponent.getValue() > 21) {
                return -1;
            }
        }
        return 0;
    }

    public boolean check (String userid) {
        if (iniziator.userid.equals(userid)) {
            inizCheck = true;
        } else if (opponent.userid.equals(userid)) {
            oppoCheck = true;
        }
        if (inizCheck && oppoCheck) {
            return true;
        }
        return false;
    }

    public List<String> loadCards () {
        List<String> cards = new LinkedList<>();
        InputStream is = getClass().getResourceAsStream("/textfiles/cards.txt");
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);

        try {
            for (String line; (line = br.readLine()) != null; ) {
                cards.add(line);
            }
        } catch (IOException ioe) {
            Slf4JLogger logger = new Slf4JLogger(this.getClass().getName());
            logger.logError("Couldnt load the cards.txt file", ioe);
        }
        Collections.shuffle(cards);
        return cards;
    }

    public String drawCard () {
        int index = ThreadLocalRandom.current().nextInt(0, cards.size());
        String card = cards.get(index);
        cards.remove(index);
        return card;
    }

    public Player getIniziator() {
        return iniziator;
    }

    public Player getOpponent() {
        return opponent;
    }

    public boolean isInizCheck() {
        return inizCheck;
    }

    public boolean isOppoCheck() {
        return oppoCheck;
    }

    public long getBet() {
        return bet;
    }
}
