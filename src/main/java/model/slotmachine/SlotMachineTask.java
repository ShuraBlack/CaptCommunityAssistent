package model.slotmachine;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class SlotMachineTask {

    private int multiplier;

    public List<String> rollMachine () {
        int chance = ThreadLocalRandom.current().nextInt(0, 101);
        List<String> rolles = new ArrayList<>();
        if (chance > 40) {
            multiplier = 0;
            while (rolles.size() < 3) {
                String symbol = getRandom();
                if (rolles.contains(symbol)) {
                    continue;
                }
                rolles.add(symbol);
            }
            return rolles;
        } else if (chance < 40 && chance > 10) {
            multiplier = 1;
            for (int i = 0 ; i < 3 ; i++) {
                rolles.add("\uD83C\uDFAD");
            }
            return rolles;
        } else if (chance < 10 && chance > 3) {
            multiplier = 3;
            String symbol = getRandom();
            for (int i = 0 ; i < 3 ; i++) {
                rolles.add(symbol);
            }
            return rolles;
        }

        multiplier = 10;
        for (int i = 0; i < 3; i++) {
            rolles.add("\uD83D\uDC8E");
        }
        return rolles;
    }

    private String getRandom () {
        List<String> fruits = new ArrayList<>();
        fruits.add("\uD83C\uDF4F");
        fruits.add("\uD83C\uDF4E");
        fruits.add("\uD83C\uDF50");
        fruits.add("\uD83C\uDF4A");
        fruits.add("\uD83C\uDF49");
        fruits.add("\uD83C\uDF52");
        return fruits.get(ThreadLocalRandom.current().nextInt(0, fruits.size()));
    }

    public int getMultiplier() {
        return multiplier;
    }
}
