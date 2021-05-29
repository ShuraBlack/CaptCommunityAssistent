package commands;

import commands.types.ServerCommand;
import model.slotmachine.SlotMachineTask;
import model.sql.LoadDriver;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static model.util.SQLUtil.*;
import static model.util.ChannelUtil.*;

public class SlotMashine implements ServerCommand {

    private final String mesID = "844995905027571783";
    private final List<Entry> entryList = new ArrayList<>();
    private LocalDateTime lastActiv = null;

    @Override
    public void performCommand(Member m, TextChannel channel, Message message) {
        String[] args = message.getContentDisplay().split(" ");
        message.delete().queue();

        if (message.getContentDisplay().equals("!sm machine") && m.hasPermission(Permission.ADMINISTRATOR)) {
            createStartMessage(channel);
            //channel.retrieveMessageById(mesID).complete().addReaction("\uD83C\uDFB0").queue();
        } else if (message.getContentDisplay().equals("!sm help") && m.hasPermission(Permission.ADMINISTRATOR)) {
            helper(channel);
        } else if (args.length == 2) {
            long gems = 0;
            try {
                gems = Integer.parseInt(args[1]);
                if (gems <= 0) {
                    EmbedBuilder eb = new EmbedBuilder()
                            .setColor(Color.WHITE)
                            .setDescription(m.getAsMention() + ", dein Einsatz ist zu gering *[<0]*");
                    channel.sendMessage(eb.build()).complete().delete().queueAfter(5, TimeUnit.SECONDS);
                    return;
                }
            } catch (NumberFormatException nfe) {
                EmbedBuilder eb = new EmbedBuilder()
                        .setColor(Color.WHITE)
                        .setDescription(m.getAsMention() + ", **" + args[1] + "** ist keine erlaubte Zahl *[0-9]*");
                channel.sendMessage(eb.build()).complete().delete().queueAfter(5, TimeUnit.SECONDS);
            }
            LoadDriver ld = new LoadDriver();
            ResultSet rs = ld.executeSQL(SELECTGEMSOFMEMBER(m.getId()));
            try {
                if (rs.next()) {
                    long balance = rs.getLong(1);
                    if (gems > (balance/100000)) {
                        EmbedBuilder eb = new EmbedBuilder()
                                .setColor(Color.WHITE)
                                .setDescription(m.getAsMention() + ", du besitzen nicht genügend \uD83D\uDC8E auf deinem Konto.\n" +
                                        "Besuchen sie " + channel.getGuild().getTextChannelById(IDLEGAME).getAsMention());
                        channel.sendMessage(eb.build()).complete().delete().queueAfter(10, TimeUnit.SECONDS);
                    } else {
                        if (entryList.stream().map(entry -> entry.userid).anyMatch(userid -> userid.equals(m.getId()))) {
                            EmbedBuilder eb = new EmbedBuilder()
                                    .setColor(Color.WHITE)
                                    .setDescription(m.getAsMention() + ", du bist bereits in der aktuellen Runde");
                            channel.sendMessage(eb.build()).complete().delete().queueAfter(5, TimeUnit.SECONDS);
                        } else {
                            ld.executeSQL(UPDATEGEMSOFMEMBER(m.getId(), ((balance/100000) - gems) * 100000));
                            entryList.add(new Entry(m.getAsMention(), m.getId(), gems));
                            redrawMessage(channel);
                        }
                    }
                } else {
                    EmbedBuilder eb = new EmbedBuilder()
                            .setColor(Color.WHITE)
                            .setDescription(m.getAsMention() + ", du besitzen noch kein Konto.\n" +
                                    "Besuche " + channel.getGuild().getTextChannelById(IDLEGAME).getAsMention() + " und " +
                                    "erspielen dir regelmäßig \uD83D\uDC8E");
                    channel.sendMessage(eb.build()).complete().delete().queueAfter(10, TimeUnit.SECONDS);
                }
            } catch (SQLException throwables) {
                ld.close();
                throwables.printStackTrace();
            }
            ld.close();
        }
    }

    @Override
    public void reactionperform(Member m, TextChannel channel, String mesID, String emote, MessageReactionAddEvent event) {
        event.getReaction().removeReaction(m.getUser()).queue();
        if (entryList.isEmpty()) {
            return;
        }
        if (lastActiv == null) {
            lastActiv = LocalDateTime.now();
        } else {
            LocalDateTime timeSave = LocalDateTime.now();
            if (ChronoUnit.SECONDS.between(lastActiv, timeSave) > 15) {
                lastActiv = timeSave;
            } else {
                return;
            }
        }
        SlotMachineTask slotMachineTask = new SlotMachineTask();
        finalMessage(channel, slotMachineTask.rollMachine(), slotMachineTask.getMultiplier());
        entryList.clear();
    }

    @Override
    public void privateperform(String command, User u) {
        throw new UnsupportedOperationException();
    }

    public void createStartMessage (TextChannel channel) {
        EmbedBuilder eb = new EmbedBuilder()
                .setColor(Color.WHITE)
                .setTitle("SlotMachine")
                .setDescription("Setze deinen Einsatz!")
                .addField("Aktuelle Runde","Gesamter Einsatz: 0 \uD83D\uDC8E",false)
                .addField("","```\n ❔ \n```",true)
                .addField("","```\n ❔ \n```",true)
                .addField("","```\n ❔ \n```",true)
                .addField("Teilnehmer:","...",false)
                .setFooter("Viel Glück");
        //channel.sendMessage(eb.build()).queue();
        channel.editMessageById(mesID, eb.build()).queue();
    }

    private void redrawMessage (TextChannel channel) {
        EmbedBuilder eb = new EmbedBuilder()
                .setColor(Color.WHITE)
                .setTitle("SlotMachine")
                .setDescription("Setze deinen Einsatz!");

        NumberFormat numFormat = new DecimalFormat();

        StringBuilder s = new StringBuilder();
        long poolValue = 0;
        for (Entry entry : entryList) {
            s.append(entry.mention).append(" ").append(entry.gems).append("\n");
            poolValue += entry.gems*100000;
        }

        eb.addField("Aktuelle Runde","Gesamter Einsatz:\n" + numFormat.format(poolValue) + " \uD83D\uDC8E",false)
                .addField("Teilnehmer:",s.toString(),false)
                .addField("","```\n ❔ \n```",true)
                .addField("","```\n ❔ \n```",true)
                .addField("","```\n ❔ \n```",true)
                .setFooter("Viel Glück");

        channel.editMessageById(mesID, eb.build()).queue();
    }

    private void finalMessage (TextChannel channel, List<String> symbols, int multiplier ) {
        EmbedBuilder eb = new EmbedBuilder()
                .setTitle("SlotMachine");

        NumberFormat numFormat = new DecimalFormat();

        StringBuilder s = new StringBuilder();
        long poolValue = 0;
        if (multiplier == 0) {
            for (Entry entry : entryList) {
                s.append(entry.mention).append(" ").append(0).append("\n");
                poolValue += entry.gems*100000;
            }
            eb.setColor(Color.RED)
                    .setFooter("VERLOREN");
        } else {
            LoadDriver ld = new LoadDriver();
            for (Entry entry : entryList) {
                try {
                    long newGems = entry.gems * multiplier;
                    s.append(entry.mention).append(" ").append(newGems).append("\n");
                    long converted = entry.gems * 100000;
                    ResultSet rs = ld.executeSQL(SELECTGEMSOFMEMBER(entry.userid));
                    rs.next();
                    long currentBalance = rs.getLong(1);
                    ld.executeSQL(UPDATEGEMSOFMEMBER(entry.userid, converted * multiplier + currentBalance));
                    poolValue += converted;
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
            ld.close();
            if (multiplier == 1) {
                eb.setColor(Color.CYAN);
            } else if (multiplier == 3) {
                eb.setColor(Color.YELLOW);
            } else if (multiplier == 10) {
                eb.setColor(Color.ORANGE);
            }
            eb.setFooter("GEWONNEN - " + multiplier + "x");
        }

        eb.addField("Aktuelle Runde","Gesamter Einsatz:\n" + numFormat.format(poolValue) + " \uD83D\uDC8E",false)
                .addField("","```\n " + symbols.get(0) + " \n```",true)
                .addField("","```\n " + symbols.get(1) + " \n```",true)
                .addField("","```\n " + symbols.get(2) + " \n```",true)
                .addField("Teilnehmer:",s.toString(),false);

        channel.editMessageById(mesID, eb.build()).queue();
    }

    public void helper (TextChannel channel) {
        EmbedBuilder eb = new EmbedBuilder()
                .setColor(Color.WHITE)
                .setTitle("CaptCommunity DSMH")
                .setThumbnail("https://img.freepik.com/vektoren-kostenlos/buendel-von-slots-casino-set-icons" +
                        "-vektor-illustration-design_24908-68124.jpg?size=338&ext=jpg&ga=GA1.2.2105300053.1620432000")
                .setDescription("Willkommen zur SlotMashine. Über dieses Minigame können Member die in Idlegame erspielte Währung \uD83D\uDC8E vergambeln")
                .addField("!sm [Zahl]","```cs\nSetzt dein Einsatz für die aktuelle Runde. Hierbei werden die \uD83D\uDC8E umgerechnet (100.000:1)\n```",false)
                .addField("\uD83C\uDFB0 startet die SlotMachine","```cs\nDies ist nur alle 15 Sekunden möglich\n```",false)
                .addField("Kombinationen:","",false)
                .addField("","```cs\n\uD83D\uDC8E \uD83D\uDC8E \uD83D\uDC8E \n-> 10x (3%)\n```",true)
                .addField("","```cs\nDrei Gleiche\n-> 3x (7%)\n```",true)
                .addField("","```cs\n\uD83C\uDFAD \uD83C\uDFAD \uD83C\uDFAD \n-> 1x (30%)\n```",true)
                .addField("","```cs\nAlles andere \nVerloren (60%)\n```",true)
                .setFooter("Made by ShuraBlack - Head of Server");
        //channel.sendMessage(eb.build()).queue();
        channel.editMessageById("844995571198328852",eb.build()).queue();
    }

    private class Entry {
        private final String mention;
        private final String userid;
        private final long gems;

        public Entry(String mention, String userid, long gems) {
            this.mention = mention;
            this.userid = userid;
            this.gems = gems;
        }
    }
}
