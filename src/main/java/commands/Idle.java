package commands;

import model.idle.Peace;
import model.idle.War;
import model.sql.LoadDriver;
import model.sql.SQLUtil;
import com.mysql.cj.log.Slf4JLogger;
import commands.types.ServerCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Idle implements ServerCommand {

    private IdleGame instance = null;
    private String mesID = null;
    private boolean activ = true;

    private War war = null;
    private Peace peace = null;
    private int time = 0;

    @Override
    public void performCommand(Member m, TextChannel channel, Message message) {
        String[] args = message.getContentDisplay().split(" ");

        message.delete().queue();

        if (!channel.getId().equals("804762124349997078")) {
            EmbedBuilder eb = new EmbedBuilder()
                    .setColor(Color.WHITE)
                    .setDescription("Starte das Idlegame im " + channel.getGuild().getTextChannelById("804762124349997078").getAsMention() + " TextChannel");
            channel.sendMessage(eb.build()).complete().delete().queueAfter(5, TimeUnit.SECONDS);

            return;
        }
        if (args.length == 1) {
            if (instance != null) {
                return;
            }
            LoadDriver ld = new LoadDriver();
            activ = true;
            instance = new IdleGame(m.getEffectiveName(), m.getId(), channel);

            ResultSet existsload = ld.executeSQL(SQLUtil.SELECTSAVEGAME(m.getId()), SQLUtil.SELECTREQUESTTYPE);
            try {
                if (existsload.next()) {
                    ResultSet rs = ld.executeSQL(SQLUtil.SELECTSAVEGAME(m.getId()), SQLUtil.SELECTREQUESTTYPE);
                    rs.next();
                    loadcode(rs.getString(1));
                }
            } catch (SQLException throwables) {
                Slf4JLogger logger = new Slf4JLogger("Idlegame.load");
                logger.logError("Couldnt load the save codes",throwables);
            }

            EmbedBuilder eb = createMessage();
            mesID = channel.sendMessage(eb.build()).complete().getId();


            ld.executeSQL(SQLUtil.INSERTCOMMANDMESSAGE(mesID,"!idle"), SQLUtil.INSERTREQUESTTYPE);

            ld.close();

            String[] reactionname = {"⚔","\uD83C\uDFF9","\uD83D\uDEE1","⚖","\uD83D\uDECC","\uD83D\uDCBF","\uD83D\uDCC0","\uD83D\uDCE4"};
            for (String s : reactionname) {
                channel.editMessageById(mesID,eb.build()).complete().addReaction(s).queue();
            }
        } else if (args.length == 2 && args[1].equals("help")) {
            if (!m.hasPermission(Permission.ADMINISTRATOR)) {
                return;
            }
            helper(channel);
        }
    }

    @Override
    public void reactionperform(Member m, TextChannel channel, String mesID, String emote, MessageReactionAddEvent event) {
        event.getReaction().removeReaction(m.getUser()).queue();
        if (!m.getId().equals(instance.userID)) {
            return;
        }

        switch (emote) {
            case "⚔":
                if (instance.value < instance.costupgrade1) {
                    break;
                }
                instance.upgrade1level++;
                instance.value -= instance.costupgrade1;
                instance.multiplier += instance.upgrade1amount;
                instance.upgrade1amount += 0.2;
                instance.costupgrade1 *= 1.8;
                break;
            case "\uD83C\uDFF9":
                if (instance.value < instance.costupgrade2) {
                    break;
                }
                instance.upgrade2level++;
                instance.value -= instance.costupgrade2;
                instance.multiplier += instance.upgrade2amount;
                instance.upgrade2amount += 0.5;
                instance.costupgrade2 *= 1.8;
                break;
            case "\uD83D\uDEE1":
                if (instance.value < instance.costupgrade3) {
                    break;
                }
                instance.upgrade3level++;
                instance.value -= instance.costupgrade3;
                instance.multiplier += instance.upgrade3amount;
                instance.upgrade3amount += 2.0;
                instance.costupgrade3 *= 1.8;
                break;
            case "⚖":
                if (instance.value < instance.costupgrade4) {
                    break;
                }
                instance.upgrade4level++;
                instance.value -= instance.costupgrade4;
                instance.multiplier += instance.upgrade4amount;
                instance.upgrade4amount += 6.0;
                instance.costupgrade4 *= 2.0;
                break;
            case "\uD83D\uDCBF":
                if (instance.value < instance.costcritchance) {
                    break;
                }
                if (instance.upgradecritchance == 24) {
                    break;
                }
                instance.upgradecritchance++;
                instance.value -= instance.costcritchance;
                instance.critchance += instance.upgradecritchanceamount;
                instance.costcritchance *= 1.9;
                break;
            case "\uD83D\uDCC0":
                if (instance.value < instance.costcritdamage) {
                    break;
                }
                instance.upgradecritdamage++;
                instance.value -= instance.costcritdamage;
                instance.critdamage += instance.upgradecritdamageamount;
                instance.costcritdamage *= 1.9;
                break;
            case "\uD83D\uDECC":
                if (instance.value < instance.costvillager) {
                    break;
                }
                instance.upgradevillager++;
                instance.value -= instance.costvillager;
                instance.villager++;
                instance.costvillager *= 2.5;
                break;
            case "\uD83D\uDCE4": // Save
                LoadDriver ld = new LoadDriver();
                ResultSet exists = ld.executeSQL(SQLUtil.SELECTSAVEGAME(m.getId()), SQLUtil.SELECTREQUESTTYPE);
                try {
                    if (!exists.next()) {
                        ld.executeSQL(SQLUtil.INSERTSAVEGAME(m.getId(),SQLsavecode()), SQLUtil.INSERTREQUESTTYPE);
                    } else {
                        ld.executeSQL(SQLUtil.UPDATESAVEGAME(m.getId(),SQLsavecode()), SQLUtil.UPDATEREQUESTTYPE);
                    }
                    ld.executeSQL(SQLUtil.DELETECOMMANDMESSAGE(mesID), SQLUtil.DELETEREQUESTTYPE);
                    channel.deleteMessageById(mesID).queue();
                } catch (SQLException throwables) {
                    Slf4JLogger logger = new Slf4JLogger("Idlegame.save");
                    logger.logError("Couldnt save the user code or delete the instance message",throwables);
                }
                EmbedBuilder eb = new EmbedBuilder()
                        .setColor(Color.WHITE)
                        .setDescription("Dein Spiel würde gespeichert!")
                        .setFooter(m.getUser().getAsTag() + " : " + m.getId());
                channel.sendMessage(eb.build()).complete().delete().queueAfter(5, TimeUnit.SECONDS);
                activ = false;
                instance = null;
                ld.close();
                break;
        }

    }

    @Override
    public void privateperform(String command, User u) { throw new UnsupportedOperationException(); }

    public class IdleGame {

        private final String userID;
        private final String username;

        final int delay = 5000; // delay for 5 sec.
        final int period = 5000; // repeat every 5 sec.

        private long value = 0;
        private int villager = 1;
        private double multiplier = 1.0;
        private double critdamage = 105;
        private int critchance = 5;

        private int costupgrade1 = 10;
        private double upgrade1amount = 0.2;
        private int upgrade1level = 1;

        private int costupgrade2 = 25;
        private double upgrade2amount = 0.5;
        private int upgrade2level = 1;

        private int costupgrade3 = 100;
        private double upgrade3amount = 5.0;
        private int upgrade3level = 1;

        private int costupgrade4 = 1000;
        private double upgrade4amount = 12.0;
        private int upgrade4level = 1;

        private int costvillager = 1500;
        private final int upgradevillageramount = 1;
        private int upgradevillager = 1;

        private int costcritchance = 100;
        private final int upgradecritchanceamount = 4;
        private int upgradecritchance = 1;

        private int costcritdamage = 120;
        private final int upgradecritdamageamount = 10;
        private int upgradecritdamage = 1;

        public IdleGame (String username, String ID, TextChannel channel) {
            this.userID = ID;
            this.username = username;
            count(channel);
        }

        public void count (TextChannel channel) {
            Timer timer = new Timer();
            Random rdm = new Random();
            timer.scheduleAtFixedRate(new TimerTask() {
                public void run() {
                    if (!activ) {
                        this.cancel();
                    }
                    double bonus = 1;
                    if (peace != null) {
                        bonus = peace.getBonusvalue();
                    }
                    int valueplus = (int) ((villager * multiplier) * 5 * bonus);
                    if (rdm.nextInt(100) < instance.critchance) {
                        value += valueplus * (instance.critdamage / 100);
                    } else {
                        value += valueplus;
                    }
                    if (rdm.nextInt(100) < 5 && war == null && peace == null) {
                        int attack = upgrade1level + upgrade2level + upgrade3level;
                        int depo = (int) ((upgradevillager + upgrade4level) * 2.5);
                        if (attack < depo) {
                            peace = new Peace();
                        } else {
                            war = new War(attack);
                        }
                    }

                    EmbedBuilder eb = createMessage();
                    if (war != null) {
                        if (time < war.getDuration()) {
                            eb.addField("\uD83D\uDCA2 Ein Krieg ist ausgebrochen! \uD83D\uDCA2",war.getEventname() + "\n" +
                                    "Deine Truppenstärke: " + war.getStrength() + "\n" +
                                    "Gegnerische Truppenstärke: " + war.getEnemystrength() + "\n" +
                                    "Laufzeit: " + (time * 5) + "/" + war.getDuration() * 5 + " Sekunden",false);
                            time++;
                        } else {
                            int result = war.getResult();
                            if (result < 0) {
                                eb.addField("Du hast den Krieg verloren!","Deine Güter wurden gestohlen und du bist verschuldet",false);
                                war = null;
                                if (instance.value < 0) {
                                    instance.value -=  (Math.abs(instance.value) * 1.5);
                                } else {
                                    instance.value -= (instance.value * 1.5);
                                }
                            } else {
                                eb.addField("Du hast den Krieg gewonnen!","Du plünderst das andere Land aus und dein Reichtum steigt",false);
                                war = null;
                                if (value < 0) {
                                    value = 0;
                                } else {
                                    value += value;
                                }
                            }
                            time = 0;
                        }
                    } else if (peace != null) {
                        if (time < peace.getDuration()) {
                            eb.addField("\uD83C\uDF3C Die Diplomatie ist deine Waffe! \uD83C\uDF3C",peace.getEventname() + "\n" +
                                    "Bonus: " + peace.getBonusvalue() + "x\n" +
                                    "Laufzeit: " + (time * 5) + "/" + peace.getDuration() * 5 + " Sekunden",false);
                            time++;
                        } else {
                            peace = null;
                            time = 0;
                        }
                    }
                    if (activ) {
                        channel.editMessageById(mesID, eb.build()).queue();
                    }
                }

            }, delay, period);
        }
    }

    public EmbedBuilder createMessage () {
        NumberFormat numFormat;
        if (instance.value > 1000000 || instance.value < -1000000) {
            numFormat = new DecimalFormat("000000E0");
        } else {
            numFormat = new DecimalFormat();
        }
        String[] reactionname = {"⚔","\uD83C\uDFF9","\uD83D\uDEE1","⚖","\uD83D\uDECC","\uD83D\uDCBF","\uD83D\uDCC0","\uD83D\uDCE4"};
        EmbedBuilder eb = new EmbedBuilder();

        eb.setColor(Color.BLACK);
        eb.setTitle("Discord Idler CaptCom (EXPERIMENTAL)");
        eb.setDescription("Willkommen zum servereigenen Idle Game\nDu siehst dich eines Tages Als König/-in eines Volkes wieder und bist " +
                "nun verantwortlich dafür das dein Reich in Wohlstand erblüht. Treffe sinnvolle Entscheidungen um an Reichtum zu kommen. " +
                "Ob mit Krieg oder Diplomatie\n");

        eb.addField("\uD83D\uDC8E " + numFormat.format(instance.value),"**Bevölkerung:** " + instance.villager + "mio.\n" + "**Multiplikator:** " + instance.multiplier + "x\n" +
                "**Kritische Chance:** " + instance.critchance + "%\n" + "**Kritischer Schaden:** " + instance.critdamage + "%",false);
        eb.addField("","",false);
        eb.addField(reactionname[0] + " Schwerter schärfen","**Stufe:** " + instance.upgrade1level + " - **Kosten:** " + numFormat.format(instance.costupgrade1) + " - **Steigerung:** " + instance.upgrade1amount + "x",false);
        eb.addField(reactionname[1] + " Bögen spannen","**Stufe:** " + instance.upgrade2level + " - **Kosten:** " + numFormat.format(instance.costupgrade2) + " - **Steigerung:** " + instance.upgrade2amount + "x",false);
        eb.addField(reactionname[2] + " Schilder härten","**Stufe:** " + instance.upgrade3level + " - **Kosten:** " + numFormat.format(instance.costupgrade3) + " - **Steigerung:** " + instance.upgrade3amount + "x",false);
        eb.addField(reactionname[3] + " Diplomatie anwenden","**Stufe:** " + instance.upgrade4level + " - **Kosten:** " + numFormat.format(instance.costupgrade4) + " - **Steigerung:** " + instance.upgrade4amount + "x",false);
        eb.addField(reactionname[4] + " Häuser bauen","**Stufe:** " + instance.upgradevillager + " - **Kosten:** " + numFormat.format(instance.costvillager) + " - **Steigerung:** " + instance.upgradevillageramount + "mio.",false);
        eb.addField("","",false);
        eb.addField(reactionname[5] + " Kritische Chance erhöhen","**Stufe:** " + instance.upgradecritchance + " - **Kosten:** " + numFormat.format(instance.costcritchance) + " - **Steigerung:** " + instance.upgradecritchanceamount + "%",false);
        eb.addField(reactionname[6] + " Kritischer Schaden erhöhen","**Stufe:** " + instance.upgradecritdamage + " - **Kosten:** " + numFormat.format(instance.costcritdamage) + " - **Steigerung:** " + instance.upgradecritdamageamount + "%",false);
        eb.addField("","",false);
        eb.addField(reactionname[7] + " Speichern und schließen","",false);
        eb.setFooter("Instanz von " + instance.username);

        return eb;
    }

    public void helper (TextChannel channel) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.WHITE);
        eb.setTitle("CaptCommunity DIGH");
        eb.setThumbnail("https://s12.directupload.net/images/210311/py53wagd.png");
        eb.setDescription("Eine Anleitung zum verwenden des Discord eigenen Idles Games");
        eb.addField("Start-up:","Vergewissere dich das nicht bereits eine andere Instanz des Spieles aktiv ist!\n" +
                "```cs\nStarte eine neue Instanz mit \"!idle\"\n```\nDie Nachricht braucht ein paar Sekunden bis sie" +
                " vollständig aufgebaut wurde",false);
        eb.addField("Managment: (DEPRECATED)","```cs\nPause das Spiel mit \"!idle close\"\noder\nlass es wieder starten mit \"!idle open\"\n\n" +
                "De/-aktiviert den Gametask auch im Hintergrund\n```",false);
        eb.addField("How to play:","Nutze Reaections unterhalb der Nachricht um Verbesserungen zu kaufen\n" +
                "Nutze die Reaction \uD83D\uDCE4 um zu speichern. Dabei wird dein Spielstand auf der Datenbank abgelegt und die Nachricht geschlossen",false);
        eb.setFooter("Made by ShuraBlack - Head of Server");

        channel.sendMessage(eb.build()).complete().pin().queue();
        channel.purgeMessages(get(channel,1));
    }

    private String SQLsavecode () {
        if (instance == null) {
            return "";
        }
        StringBuilder s = new StringBuilder();
        s.append("va").append("-").append(instance.value).append("_");
        s.append("vi").append("-").append(instance.villager).append("_");
        s.append("mu").append("-").append(instance.multiplier).append("_");

        s.append("cuone").append("-").append("CU").append("-").append(instance.costupgrade1).append("-").append("UA").append("-").append(instance.upgrade1amount)
                .append("-").append("UL").append("-").append(instance.upgrade1level).append("_");

        s.append("cutwo").append("-").append("CU").append("-").append(instance.costupgrade2).append("-").append("UA").append("-").append(instance.upgrade2amount)
                .append("-").append("UL").append("-").append(instance.upgrade2level).append("_");

        s.append("cuthree").append("-").append("CU").append("-").append(instance.costupgrade3).append("-").append("UA").append("-").append(instance.upgrade3amount)
                .append("-").append("UL").append("-").append(instance.upgrade3level).append("_");

        s.append("cufour").append("-").append("CU").append("-").append(instance.costupgrade4).append("-").append("UA").append("-").append(instance.upgrade4amount)
                .append("-").append("UL").append("-").append(instance.upgrade4level).append("_");

        s.append("vi").append("-").append("CU").append("-").append(instance.costvillager).append("-").append("UA").append("-").append(instance.upgradevillageramount)
                .append("-").append("UL").append("-").append(instance.upgradevillager).append("_");

        s.append("cc").append("-").append("CU").append("-").append(instance.costcritchance).append("-").append("UA").append("-").append(instance.upgradecritchanceamount)
                .append("-").append("UL").append("-").append(instance.upgradecritchance).append("_");

        s.append("_").append("cd").append("-").append("CU").append("-").append(instance.costcritdamage).append("-").append("UA").append("-").append(instance.upgradecritdamageamount)
                .append("-").append("UL").append("-").append(instance.upgradecritdamage).append("_");
        return s.toString();
    }

    public void createcode (User u) {
        StringBuilder s = new StringBuilder();
        if (instance != null) {

            s.append("va").append("-").append(instance.value).append("_");
            s.append("vi").append("-").append(instance.villager).append("_");
            s.append("mu").append("-").append(instance.multiplier).append("_");

            s.append("cuone").append("-").append("CU").append("-").append(instance.costupgrade1).append("-").append("UA").append("-").append(instance.upgrade1amount)
                    .append("-").append("UL").append("-").append(instance.upgrade1level).append("_");

            s.append("cutwo").append("-").append("CU").append("-").append(instance.costupgrade2).append("-").append("UA").append("-").append(instance.upgrade2amount)
                    .append("-").append("UL").append("-").append(instance.upgrade2level).append("_");

            s.append("cuthree").append("-").append("CU").append("-").append(instance.costupgrade3).append("-").append("UA").append("-").append(instance.upgrade3amount)
                    .append("-").append("UL").append("-").append(instance.upgrade3level).append("_");

            s.append("cufour").append("-").append("CU").append("-").append(instance.costupgrade4).append("-").append("UA").append("-").append(instance.upgrade4amount)
                    .append("-").append("UL").append("-").append(instance.upgrade4level).append("_");

            s.append("vi").append("-").append("CU").append("-").append(instance.costvillager).append("-").append("UA").append("-").append(instance.upgradevillageramount)
                    .append("-").append("UL").append("-").append(instance.upgradevillager).append("_");

            s.append("cc").append("-").append("CU").append("-").append(instance.costcritchance).append("-").append("UA").append("-").append(instance.upgradecritchanceamount)
                    .append("-").append("UL").append("-").append(instance.upgradecritchance).append("_");

            s.append("_").append("cd").append("-").append("CU").append("-").append(instance.costcritdamage).append("-").append("UA").append("-").append(instance.upgradecritdamageamount)
                    .append("-").append("UL").append("-").append(instance.upgradecritdamage).append("_");
        } else {
            s.append("xxx");
        }
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("CaptCommunity DIGC");
        eb.setDescription("**Username:** " + u.getName() + "\n**ID:** " + u.getId());
        if (s.toString().equals("xxx")) {
            eb.addField("**Code:** ","No gamecode for this user!",false);
        } else {
            eb.addField("**Code:** ",s.toString(),false);
        }
        eb.setFooter("Made by ShuraBlack");
        u.openPrivateChannel().complete().sendMessage(eb.build()).queue();
    }

    public void loadcode (String code) {
        if (instance == null) {
            return;
        }
        String[] parts = code.split("_");

        for (String part : parts) {
            String[] values = part.split("-");

            if (values[0].equals("va")) {
                instance.value = Long.parseLong(values[1]);
            } else if (values[0].equals("vi") && values.length < 3) {
                instance.villager = Integer.parseInt(values[1]);
            } else if (values[0].equals("mu")) {
                instance.multiplier = Double.parseDouble(values[1]);
            } else if (values[0].equals("cuone")) {
                instance.costupgrade1 = Integer.parseInt(values[2]);
                instance.upgrade1amount = Double.parseDouble(values[4]);
                instance.upgrade1level = Integer.parseInt(values[6]);
            } else if (values[0].equals("cutwo")) {
                instance.costupgrade2 = Integer.parseInt(values[2]);
                instance.upgrade2amount = Double.parseDouble(values[4]);
                instance.upgrade2level = Integer.parseInt(values[6]);
            } else if (values[0].equals("cuthree")) {
                instance.costupgrade3 = Integer.parseInt(values[2]);
                instance.upgrade3amount = Double.parseDouble(values[4]);
                instance.upgrade3level = Integer.parseInt(values[6]);
            } else if (values[0].equals("cufour")) {
                instance.costupgrade4 = Integer.parseInt(values[2]);
                instance.upgrade4amount = Double.parseDouble(values[4]);
                instance.upgrade4level = Integer.parseInt(values[6]);
            } else if (values.length > 2 && values[0].equals("vi")) {
                instance.costvillager = Integer.parseInt(values[2]);
                instance.upgradevillager = Integer.parseInt(values[6]);
            } else if (values[0].equals("cc")) {
                instance.costcritchance = Integer.parseInt(values[2]);
                instance.upgradecritchance = Integer.parseInt(values[6]);
                if (Integer.parseInt(values[6]) != 1) {
                    instance.critchance = instance.critchance + (instance.upgradecritchanceamount * Integer.parseInt(values[6]));
                }
            } else if (values[0].equals("cd")) {
                instance.costcritdamage = Integer.parseInt(values[2]);
                instance.upgradecritdamage = Integer.parseInt(values[6]);
                if (Integer.parseInt(values[6]) != 1) {
                    instance.critdamage = instance.critdamage + (instance.upgradecritdamageamount * Integer.parseInt(values[6]));
                }
            }
        }
    }

    public double round (double value, int scala) {
        double s = Math.pow(10, scala);
        return Math.round(value * s) / s;
    }

    private java.util.List<Message> get(MessageChannel channel, int amount) {
        List<Message> mes = new ArrayList<>();
        int i = amount + 1;
        for (Message message : channel.getIterableHistory().cache(false)) {
            if(!message.isPinned()) {
                mes.add(message);
                if(--i <= 0) {
                    break;
                }
            }
        }
        return mes;
    }

}
