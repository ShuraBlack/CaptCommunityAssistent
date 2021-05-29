package model.playerroulette;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class PlayerRouletteTask {

    private List<Entry> entryList = new ArrayList<>();

    public PlayerRouletteTask() { }

    public boolean addEntry (String mention, String userid, int gem) {
        if (entryList.stream().map(entry -> entry.userid).anyMatch(id -> id.equals(userid))) {
            return false;
        }
        entryList.add(new Entry(mention, userid, gem));
        return true;
    }

    public Entry getWinner () {
        long pool = entryList.stream().mapToLong(entry -> entry.gems).sum();
        int ticket = ThreadLocalRandom.current().nextInt(0,(int) pool);
        String winnerId = EntryMapping(ticket);
        String mention = entryList.stream().filter(entry -> entry.userid.equals(winnerId)).findFirst().get().mention;
        return createWinnerEntry(mention,winnerId);
    }

    private String EntryMapping (int ticket) {
        List<String> pool = new ArrayList<>();
        for (Entry entry : entryList) {
            long tickets = entry.gems;
            for (int i = 0 ; i < tickets ; i++) {
                pool.add(entry.userid);
            }
        }
        return pool.get(ticket);
    }

    private Entry createWinnerEntry (String mention, String userid) {
        long pool = entryList.stream().mapToLong(entry -> entry.gems).sum();
        return new Entry(mention, userid, pool);
    }

    public long poolSize () {
        return entryList.stream().mapToLong(entry -> entry.gems).sum() * 1000000;
    }

    public int size () {
        return entryList.size();
    }

    public List<String> visualizeChance () {
        long pool = entryList.stream().mapToLong(entry -> entry.gems).sum();
        List<String> chances = new ArrayList<>();
        for (Entry entry : entryList) {
            int totalChance = Integer.parseInt(String.valueOf(((entry.gems / pool) * 10)).split("\\.")[0]);
            StringBuilder s = new StringBuilder();
            s.append(entry.mention).append(" - ").append(entry.gems).append("\n");

            if (totalChance != 0) {
                for (int i = 0 ; i < totalChance ; i++) {
                    s.append("\uD83D\uDD32");
                }
            }
            s.append("\n");
            chances.add(s.toString());
        }
        return chances;
    }

    public void clear () {
        entryList.clear();
    }

    public class Entry {
        private final String mention;
        private final String userid;
        private long gems;

        public Entry(String mention, String userid, long gems) {
            this.mention = mention;
            this.userid = userid;
            this.gems = gems;
        }

        public String getMention() {
            return mention;
        }

        public String getUserid() {
            return userid;
        }

        public long getGems() {
            return gems;
        }
    }

}
