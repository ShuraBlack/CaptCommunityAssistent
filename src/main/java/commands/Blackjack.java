package commands;

import commands.types.ServerCommand;
import model.blackjack.BlackJackTask;
import model.sql.LoadDriver;
import model.util.ChannelUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import static model.util.ChannelUtil.IDLEGAME;
import static model.util.SQLUtil.*;

public class Blackjack implements ServerCommand {

    private BlackJackTask game = null;
    private String mesId;

    @Override
    public void performCommand(Member m, TextChannel channel, Message message) {

        if (!channel.getId().equals(ChannelUtil.BLACKJACK)) {
            EmbedBuilder eb = new EmbedBuilder()
                    .setColor(Color.WHITE)
                    .setDescription("Starte das Idlegame im " + channel.getGuild().getTextChannelById("804762124349997078").getAsMention() + " TextChannel");
            channel.sendMessage(eb.build()).complete().delete().queueAfter(5, TimeUnit.SECONDS);
            return;
        }

        message.delete().queue();
        String[] args = message.getContentDisplay().split(" ");

        if (message.getContentDisplay().equals("!blackjack") && game == null) {
            game = new BlackJackTask(m.getId());
            createStartMessage(m, channel);
        } else if (message.getContentDisplay().equals("!blackjack stop") && game != null) {
            if (m.getId().equals(game.getIniziator().getUserid())
                    || m.getId().equals(game.getOpponent().getUserid())) {
                clear();
            }
        } else if (message.getContentDisplay().equals("!blackjack help")
                && m.hasPermission(Permission.ADMINISTRATOR)) {
            EmbedBuilder eb = new EmbedBuilder()
                    .setColor(Color.WHITE)
                    .setThumbnail("https://s20.directupload.net/images/210519/ckhgzphs.png")
                    .setTitle("CaptCommunity DBJH")
                    .setDescription("Über diesen Channel kann jeder member+ Simply Blackjack spielen.\n" +
                            "Achte darauf das keine weitere Instanz offen ist")
                    .addField("!blackjack","```cs\nÖffnet eine freie Runde\n```",false)
                    .addField("!blackjack [zahl]","```cs\nÖffnet eine Runde mit Wetteinsatz (100.000:1)\n```",false)
                    .addField("!blackjack stop","```cs\nErlaubt es (beide) die aktive Runde zu schließen\n```",false)
                    .addField("How to Play?","Nach dem Rundenstart können beide Spieler mit\n" +
                            "\uD83C\uDCCF eine weitere Karte ziehen\n" +
                            "\uD83D\uDD12 die aktuelle hand einlocken\n" +
                            "Sind beide Hände gelocked, wird bestimmt welcher spieler näher an 21 dran liegt.\n" +
                            "Wenn du beim Karten ziehen über 21 kommen solltest, gewinnt dein Mitspieler direkt",false)
                    .setFooter("Made by ShuraBlack - Head of Server");
            //channel.sendMessage(eb.build()).complete().pin().queue();
            channel.editMessageById("844907739318845450", eb.build()).queue();
        } else if (args.length == 2) {
            try {
                long bet = Long.parseLong(args[1]);
                if (bet <= 0) {
                    EmbedBuilder eb = new EmbedBuilder()
                            .setColor(Color.WHITE)
                            .setDescription(m.getAsMention() + ", deine angegebene Wette ist zu klein [>0]");
                    channel.sendMessage(eb.build()).complete().delete().queueAfter(7, TimeUnit.SECONDS);
                    return;
                }
                LoadDriver ld = new LoadDriver();
                ResultSet rs = ld.executeSQL(SELECTGEMSOFMEMBER(m.getId()));
                if (!rs.next()) {
                    EmbedBuilder eb = new EmbedBuilder()
                            .setColor(Color.WHITE)
                            .setDescription(m.getAsMention() + ", du besitzen noch kein Konto.\n" +
                                    "Besuche " + channel.getGuild().getTextChannelById(IDLEGAME).getAsMention() + " und " +
                                    "erspielen dir regelmäßig \uD83D\uDC8E");
                    channel.sendMessage(eb.build()).complete().delete().queueAfter(10, TimeUnit.SECONDS);
                }
                if (rs.getLong(1) < (bet*100000)) {
                    EmbedBuilder eb = new EmbedBuilder()
                            .setColor(Color.WHITE)
                            .setDescription(m.getAsMention() + ", du besitzen nicht genügend \uD83D\uDC8E auf deinem Konto.\n" +
                                    "Besuchen sie " + channel.getGuild().getTextChannelById(IDLEGAME).getAsMention());
                    channel.sendMessage(eb.build()).complete().delete().queueAfter(10, TimeUnit.SECONDS);
                }
                game = new BlackJackTask(m.getId(), bet);
                createStartMessage(m, channel);
            } catch (NumberFormatException | SQLException nfe) {
                EmbedBuilder eb = new EmbedBuilder()
                        .setColor(Color.WHITE)
                        .setDescription(m.getAsMention() + ", **" + args[1] + "** ist keine erlaubte Zahl");
                channel.sendMessage(eb.build()).complete().delete().queueAfter(7, TimeUnit.SECONDS);
            }
        }
    }

    @Override
    public void reactionperform(Member m, TextChannel channel, String mesID, String emote, MessageReactionAddEvent event) {
        if (game != null) {
            event.getReaction().removeReaction(m.getUser()).queue();
            if (game.getOpponent() == null && !m.getId().equals(game.getIniziator().getUserid())) {
                if (!balanceCheck(m.getId())) {
                    EmbedBuilder eb = new EmbedBuilder()
                            .setColor(Color.WHITE)
                            .setDescription(m.getAsMention() + ", du besitzt entweder kein Konto oder nicht genug \uD83D\uDC8E");
                    channel.sendMessage(eb.build()).complete().delete().queueAfter(10, TimeUnit.SECONDS);
                    return;
                }
                game.joinGame(m.getId());
                game.setCard(game.getIniziator().getUserid(), game.drawCard());
                game.setCard(game.getIniziator().getUserid(), game.drawCard());
                game.setCard(game.getOpponent().getUserid(), game.drawCard());
                game.setCard(game.getOpponent().getUserid(), game.drawCard());

                int valueIniz = game.getIniziator().getValue();
                int valueOppo = game.getOpponent().getValue();
                if (valueIniz == 21 || valueOppo == 21) {
                    finalMessage(channel, 0);
                } else {
                    redrawMessage(channel);
                    channel.removeReactionById(mesID, "✅").queue();
                    channel.removeReactionById(mesID, "❌").queue();
                    channel.addReactionById(mesID, "\uD83C\uDCCF").queue();
                    channel.addReactionById(mesID, "\uD83D\uDD12").queue();
                }
            } else if (game.getOpponent() == null && m.getId().equals(game.getIniziator().getUserid()) && emote.equals("❌")) {
                channel.deleteMessageById(mesID).queue();
                clear();
            } else if (game.getOpponent() != null) {
                if (emote.equals("\uD83C\uDCCF")) {
                    int overDraw;
                    if (game.getIniziator().getUserid().equals(m.getId()) && !game.isInizCheck()) {
                        overDraw = game.setCard(game.getIniziator().getUserid(), game.drawCard());
                    } else if (game.getOpponent().getUserid().equals(m.getId()) && !game.isOppoCheck()) {
                        overDraw = game.setCard(game.getOpponent().getUserid(), game.drawCard());
                    } else {
                        return;
                    }
                    if (overDraw != 0) {
                        finalMessage(channel, overDraw);
                    } else {
                        redrawMessage(channel);
                    }
                } else if (emote.equals("\uD83D\uDD12")) {
                    boolean winner;
                    if (game.getIniziator().getUserid().equals(m.getId())) {
                        winner = game.check(game.getIniziator().getUserid());
                    } else if (game.getOpponent().getUserid().equals(m.getId())) {
                        winner = game.check(game.getOpponent().getUserid());
                    } else {
                        return;
                    }
                    if (winner) {
                        finalMessage(channel, 0);
                    } else {
                        redrawMessage(channel);
                    }
                }
            }
        }
    }

    @Override
    public void privateperform(String command, User u) {
        throw new UnsupportedOperationException();
    }

    private void createStartMessage (Member m, TextChannel channel) {
        StringBuilder s = new StringBuilder();
        s.append("Warte auf mitspieler...\nIniziator: ").append(m.getAsMention()).append("\n").append("Wetteinsatz: ");

        if (game.getBet() == 0) {
            s.append("Frei");
        } else {
            s.append(game.getBet());
        }

        EmbedBuilder eb = new EmbedBuilder()
                .setColor(Color.GREEN)
                .setTitle("Simply BlackJack Lobby")
                .setDescription(s.toString())
                .setFooter("EXPERIMENTEL");
        mesId = channel.sendMessage(eb.build()).complete().getId();
        channel.editMessageById(mesId, eb.build()).complete().addReaction("✅").queue();
        channel.editMessageById(mesId, eb.build()).complete().addReaction("❌").queue();
    }

    private void redrawMessage (TextChannel channel) {
        String iniz = channel.getGuild().retrieveMemberById(game.getIniziator().getUserid()).complete().getEffectiveName();
        String oppo = channel.getGuild().retrieveMemberById(game.getOpponent().getUserid()).complete().getEffectiveName();
        
        String handIniz = game.getIniziator().getHand();
        String handOppo = game.getOpponent().getHand();
        if (game.isInizCheck()) {
            handIniz += "CHECKED";
        }
        if (game.isOppoCheck()) {
            handOppo += "CHECKED";
        }
        
        EmbedBuilder eb = new EmbedBuilder()
                .setColor(new Color(50,100,200))
                .setTitle("Simply BlackJack Game")
                .addField(iniz + " - " + game.getIniziator().getValue(),handIniz,true)
                .addField(oppo + " - " + game.getOpponent().getValue(),handOppo,true)
                .setFooter("EXPERIMENTEL");
        channel.editMessageById(mesId, eb.build()).queue();
    }
    
    private void finalMessage (TextChannel channel, int overDraw) {
        String iniz = channel.getGuild().retrieveMemberById(game.getIniziator().getUserid()).complete().getEffectiveName();
        String oppo = channel.getGuild().retrieveMemberById(game.getOpponent().getUserid()).complete().getEffectiveName();

        String handIniz = game.getIniziator().getHand();
        String handOppo = game.getOpponent().getHand();

        int valueIniz = game.getIniziator().getValue();
        int valueOppo = game.getOpponent().getValue();

        if (overDraw == 0) {
            if (valueIniz > valueOppo) {
                handIniz += "WINNER";
                makeTransaction(game.getIniziator().getUserid(), game.getOpponent().getUserid());
            } else if (valueIniz < valueOppo) {
                handOppo += "WINNER";
                makeTransaction(game.getOpponent().getUserid(), game.getIniziator().getUserid());
            } else {
                handIniz += "DRAW";
                handOppo += "DRAW";
            }
        } else {
            if (overDraw == 1) {
                handIniz += "OVERDRAW";
                handOppo += "WINNER";
                makeTransaction(game.getOpponent().getUserid(), game.getIniziator().getUserid());
            } else if (overDraw == -1) {
                handOppo += "OVERDRAW";
                handIniz += "WINNER";
                makeTransaction(game.getIniziator().getUserid(), game.getOpponent().getUserid());
            }
        }

        EmbedBuilder eb = new EmbedBuilder()
                .setColor(new Color(50,100,200))
                .setTitle("Simply BlackJack Game")
                .addField(iniz + " - " + game.getIniziator().getValue(),handIniz,true)
                .addField(oppo + " - " + game.getOpponent().getValue(),handOppo,true)
                .setFooter("EXPERIMENTEL - Diese Nachricht wird automatisch nach 15 Sekunden gelöscht");
        channel.editMessageById(mesId, eb.build()).complete().delete().queueAfter(15, TimeUnit.SECONDS);
        clear();
    }

    private boolean balanceCheck (String userid) {
        LoadDriver ld = new LoadDriver();
        ResultSet rs = ld.executeSQL(SELECTGEMSOFMEMBER(userid));
        try {
            if (!rs.next()) {
                return false;
            }
            if ((rs.getLong(1)*100000) < game.getBet()) {
                return false;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return true;
    }

    private void makeTransaction (String winnerId, String loserId) {
        LoadDriver ldWinner = new LoadDriver();
        ResultSet rs = ldWinner.executeSQL(SELECTGEMSOFMEMBER(winnerId));
        try {
            if (!rs.next()) {
                return;
            }
            long winnerBalance = rs.getLong(1) + game.getBet()*100000;
            ldWinner.executeSQL(UPDATEGEMSOFMEMBER(winnerId, winnerBalance));
        } catch (SQLException throwables) {
            ldWinner.close();
            throwables.printStackTrace();
        }
        ldWinner.close();

        LoadDriver ldLoser = new LoadDriver();
        ResultSet rs2 = ldLoser.executeSQL(SELECTGEMSOFMEMBER(loserId));
        try {
            if (!rs2.next()) {
                return;
            }
            long loserBalance = rs2.getLong(1) - game.getBet()*100000;
            ldLoser.executeSQL(UPDATEGEMSOFMEMBER(loserId, loserBalance));
        } catch (SQLException throwables) {
            ldLoser.close();
            throwables.printStackTrace();
        }
        ldLoser.close();
    }

    private void clear () {
        game = null;
        mesId = null;
    }

    @Override
    public void performSlashCommand(SlashCommandEvent event) {

    }
}
