package commands;

import commands.types.ServerCommand;
import model.playerroulette.PlayerRouletteTask;
import model.sql.LoadDriver;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;


import static model.util.ChannelUtil.IDLEGAME;
import static model.util.SQLUtil.*;

public class PlayerRoulette implements ServerCommand {

    private final PlayerRouletteTask playerRouletteTask = new PlayerRouletteTask();
    private boolean timerActiv = false;
    private final String mesId = "845767297066991664";

    @Override
    public void performCommand(Member m, TextChannel channel, Message message) {
        message.delete().queue();
        String[] args = message.getContentDisplay().split(" ");

        if (args.length == 2 && args[1].equals("help")) {
            helper(channel);
        } else if (args.length == 2 && args[1].equals("roulette")) {
            createMessage(channel);
        } else if (args.length == 2) {
            try {
                int gems = Integer.parseInt(args[1]);
                LoadDriver ld = new LoadDriver();
                ResultSet rs = ld.executeSQL(SELECTGEMSOFMEMBER(m.getId()));
                if (!rs.next()) {
                    ld.close();
                    EmbedBuilder eb = new EmbedBuilder()
                            .setColor(Color.WHITE)
                            .setDescription(m.getAsMention() + ", du besitzen noch kein Konto.\n" +
                                    "Besuche " + channel.getGuild().getTextChannelById(IDLEGAME).getAsMention() + " und " +
                                    "erspielen dir regelmäßig \uD83D\uDC8E");
                    channel.sendMessage(eb.build()).complete().delete().queueAfter(10, TimeUnit.SECONDS);
                    return;
                }
                long balance = rs.getLong(1);
                if ((balance/1000000) < gems) {
                    ld.close();
                    EmbedBuilder eb = new EmbedBuilder()
                            .setColor(Color.WHITE)
                            .setDescription(m.getAsMention() + ", du besitzen nicht genügend \uD83D\uDC8E auf deinem Konto.\n" +
                                    "Besuchen sie " + channel.getGuild().getTextChannelById(IDLEGAME).getAsMention());
                    channel.sendMessage(eb.build()).complete().delete().queueAfter(10, TimeUnit.SECONDS);
                    return;
                }
                if (!playerRouletteTask.addEntry(m.getAsMention(), m.getId(), gems)) {
                    ld.close();
                    EmbedBuilder eb = new EmbedBuilder()
                            .setColor(Color.WHITE)
                            .setDescription(m.getAsMention() + ", du bist bereits in der aktuellen Runde");
                    channel.sendMessage(eb.build()).complete().delete().queueAfter(5, TimeUnit.SECONDS);
                    return;
                }
                if (!timerActiv && playerRouletteTask.size() > 1) {
                    timerActiv = true;
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            finalMessage(channel);
                            playerRouletteTask.clear();
                            timerActiv = false;
                        }
                    },10000);
                }
                ld.executeSQL(UPDATEGEMSOFMEMBER(m.getId(), balance - (gems * 1000000)));
                ld.close();
                redrawMessage(channel);
            } catch (NumberFormatException | SQLException nfe) {
                EmbedBuilder eb = new EmbedBuilder()
                        .setColor(Color.WHITE)
                        .setDescription(m.getAsMention() + ", **" + args[1] + "** ist keine erlaubte Zahl *[0-9]*");
                channel.sendMessage(eb.build()).complete().delete().queueAfter(5, TimeUnit.SECONDS);
            }
        }
    }

    @Override
    public void reactionperform(Member m, TextChannel channel, String mesID, String emote, MessageReactionAddEvent event) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void privateperform(String command, User u) {
        throw new UnsupportedOperationException();
    }

    public void createMessage (TextChannel channel) {
        EmbedBuilder eb = new EmbedBuilder()
                .setColor(Color.WHITE)
                .setTitle("Roulette Table")
                .addField("Aktuelle Runde","Gesamter Einsatz:\n0 \uD83D\uDC8E",false)
                .addField("Teilnehmer:","",false)
                .setFooter("Viel Glück");
        channel.sendMessage(eb.build()).queue();
    }

    public void redrawMessage (TextChannel channel) {
        NumberFormat numFormat = new DecimalFormat();
        EmbedBuilder eb = new EmbedBuilder()
                .setTitle("Roulette Table")
                .addField("Aktuelle Runde","Gesamter Einsatz:\n"
                        + numFormat.format(playerRouletteTask.poolSize()) + " \uD83D\uDC8E",false);
        StringBuilder sb = new StringBuilder();
        for (String s : playerRouletteTask.visualizeChance()) {
            sb.append(s);
        }
        eb.addField("Teilnehmer:",sb.toString(),false);
        eb.setFooter("Viel Glück");

        if (timerActiv) {
            eb.setColor(Color.GREEN);
        } else {
            eb.setColor(Color.WHITE);
        }

        channel.editMessageById(mesId, eb.build()).queue();
    }

    public void finalMessage (TextChannel channel) {
        PlayerRouletteTask.Entry winner = playerRouletteTask.getWinner();
        EmbedBuilder eb = new EmbedBuilder()
                .setColor(Color.WHITE)
                .setTitle("Roulette Table")
                .addField("Aktuelle Runde","Gewinner:\n"
                        + winner.getMention() + "\nPreis:\n" + (winner.getGems() * 1000000) + " \uD83D\uDC8E",false)
                .setFooter("Mache eine neue Wette mit !pr [zahl]");
        channel.editMessageById(mesId, eb.build()).queue();
        playerRouletteTask.clear();
    }

    public void helper (TextChannel channel) {
        EmbedBuilder eb = new EmbedBuilder()
                .setColor(Color.WHITE)
                .setTitle("CaptCommunity DPRH")
                .setThumbnail("https://img.icons8.com/color/452/american-roulette.png")
                .setDescription("Willkommen zum PlayerRoulette. Über dieses Minigame können Member die in Idlegame erspielte Währung \uD83D\uDC8E vergambeln")
                .addField("!pr [zahl]","```cs\nSetzt dein Einsatz für die aktuelle Runde. Hierbei werden die \uD83D\uDC8E umgerechnet (1.000.000:1)\n```",false)
                .addField("Start:", "Ab dem moment wo sich zwei spieler im Roulette befinden wird ein 60 Sekunden timer gestartet",false)
                .addField("Tickets", "Deine Gewinnchance steigt um so mehr du in den Pool zahlst",false)
                .setFooter("Made by ShuraBlack - Head of Server");
        channel.sendMessage(eb.build()).queue();
    }

    @Override
    public void performSlashCommand(SlashCommandEvent event) {

    }
}
