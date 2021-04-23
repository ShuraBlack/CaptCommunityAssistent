package commands;


import model.dice.DiceMatch;
import model.sql.LoadDriver;
import model.sql.SQLUtil;
import commands.types.ServerCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

import java.awt.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Dice implements ServerCommand {

    Map<String, DiceMatch> matches = new TreeMap<>();
    Map<String, String> initsToMes = new TreeMap<>();
    Map<String, String> initsToInvite = new TreeMap<>();

    @Override
    public void performCommand(Member m, TextChannel channel, Message message) {

        message.delete().queue();

        if (!channel.getId().equals("818607332555489340")) {
            EmbedBuilder eb = new EmbedBuilder()
                    .setColor(Color.WHITE)
                    .setDescription("Nutze den " + channel.getGuild().getTextChannelById("818607332555489340").getAsMention() + " TextChannel");
            channel.sendMessage(eb.build()).complete().delete().queueAfter(5, TimeUnit.SECONDS);
            return;
        }

        Random rand = new Random();
        int rdmNum;
        String[] args = message.getContentDisplay().split(" ");

        if(args.length == 1) {

            rdmNum = rand.nextInt((6 - 1) + 1) + 1;

            EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(Color.WHITE);
            eb.setTitle("Würfel fällt für");
            eb.setDescription(m.getAsMention() + " auf:");

            switch (rdmNum) {
                case 1:
                    eb.setThumbnail("https://s16.directupload.net/images/210307/ldj992lr.png");
                    break;
                case 2:
                    eb.setThumbnail("https://s8.directupload.net/images/210307/4ckygpdt.png");
                    break;
                case 3:
                    eb.setThumbnail("https://s8.directupload.net/images/210307/lnt4lf8y.png");
                    break;
                case 4:
                    eb.setThumbnail("https://s16.directupload.net/images/210307/5jh4ix9a.png");
                    break;
                case 5:
                    eb.setThumbnail("https://s16.directupload.net/images/210307/5gxheluz.png");
                    break;
                case 6:
                    eb.setThumbnail("https://s16.directupload.net/images/210307/pbur7ee8.png");
                    break;
            }

            channel.sendMessage(eb.build())
                    .complete().delete().queueAfter(5, TimeUnit.SECONDS);
        }

        if(args.length == 2) {
            if (args[1].equals("match")) {
                if (initsToMes.containsKey(m.getId())) {
                    return;
                }
                DiceMatch dm = new DiceMatch(m);
                EmbedBuilder eb = startMatchMessage(dm);
                String matchid = channel.sendMessage(eb.build()).complete().getId();
                channel.editMessageById(matchid,eb.build()).complete().addReaction("✅").queue();
                channel.editMessageById(matchid,eb.build()).complete().addReaction("❎").queue();
                initsToInvite.put(m.getId(),channel.editMessageById(matchid,eb.build()).complete().getJumpUrl());
                matches.put(matchid,dm);
                initsToMes.put(m.getId(),matchid);

                LoadDriver ld = new LoadDriver();
                ld.executeSQL(SQLUtil.INSERTCOMMANDMESSAGE(matchid,"!dice"), SQLUtil.INSERTREQUESTTYPE);
                ld.close();
            } else {
                try {
                    int max = Integer.parseInt(args[1]);

                    rdmNum = rand.nextInt((max - 1) + 1) + 1;

                    EmbedBuilder eb = new EmbedBuilder();
                    eb.setColor(Color.WHITE);
                    eb.setTitle("Würfel fällt für");
                    eb.setDescription(m.getAsMention() + " auf:");
                    eb.addField("" + rdmNum,"Werte zwischen 1 bis " + max,false);

                    channel.sendMessage(eb.build())
                            .complete().delete().queueAfter(5, TimeUnit.SECONDS);

                } catch (NumberFormatException ignored) { }
            }
        }

        if (args.length == 3) {
            if (!args[1].equals("invitematch")) {
                return;
            }
            if (!initsToMes.containsKey(m.getId())) {
                return;
            }
            EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(Color.WHITE);
            eb.setThumbnail("https://s8.directupload.net/images/210309/uwqklfqy.png");
            eb.setTitle("Dice Match Invite");
            eb.setDescription(channel.getGuild().getMemberById(m.getId()).getEffectiveName() + " läd dich zu einem Match auf dem CaptCommunity Server ein!");
            eb.addField("Nachrichtenlink:",initsToInvite.get(m.getId()),false);
            Objects.requireNonNull(channel.getGuild().getMemberById(args[2])).getUser()
                    .openPrivateChannel().complete().sendMessage(eb.build()).queue();

            channel.editMessageById(initsToMes.get(m.getId())
                    ,requestMatchMessage(matches.get(initsToMes.get(m.getId()))).build()).queue();
        }

    }

    @Override
    public void reactionperform(Member m, TextChannel channel, String mesID, String emote, MessageReactionAddEvent event) {
        if (!matches.containsKey(mesID)) {
            return;
        }
        if (!emote.equals("✅") && !emote.equals("❎")) {
            channel.editMessageById(mesID, startMatchMessage(matches.get(mesID)).build()).complete().removeReaction(emote,m.getUser()).queue();
            return;
        }

        DiceMatch dm = matches.get(mesID);

        if (emote.equals("✅")) {

            if (dm.getIniziator().getId().equals(m.getId())) {
                channel.editMessageById(mesID,startMatchMessage(dm).build()).complete()
                        .removeReaction("✅",m.getUser()).queue();
                return;
            }

            matches.remove(mesID);

            dm.acceptMatch(m);

            initsToMes.remove(dm.getIniziator().getId());
            initsToInvite.remove(dm.getIniziator().getId());

            channel.editMessageById(mesID,endMatchMessage(dm).build()).complete().clearReactions().queue();
        } else if (dm.getIniziator().getId().equals(m.getId())) {
            matches.remove(mesID);
            channel.deleteMessageById(mesID).queue();
        }

        LoadDriver ld = new LoadDriver();
        ld.executeSQL(SQLUtil.DELETECOMMANDMESSAGE(mesID), SQLUtil.DELETEREQUESTTYPE);
        ld.close();
    }

    @Override
    public void privateperform(String command, User u) {
        throw new UnsupportedOperationException();
    }

    public EmbedBuilder startMatchMessage (DiceMatch match) {
        EmbedBuilder eb = EBsettings(1);
        eb.addField("Initiator: " + match.getIniziator().getEffectiveName(),"Würfelzahl: ",false);
        eb.addField("Mitspieler: ","Würfelzahl: ",false);
        return eb;
    }

    public EmbedBuilder requestMatchMessage (DiceMatch match) {
        EmbedBuilder eb = EBsettings(3);
        eb.addField("Initiator: " + match.getIniziator().getEffectiveName(),"Würfelzahl: ",false);
        eb.addField("Mitspieler: ","Würfelzahl: ",false);
        return eb;
    }

    public EmbedBuilder endMatchMessage (DiceMatch match) {
        EmbedBuilder eb = EBsettings(2);
        Vector<Integer> values = match.getValues();
        eb.addField("Initiator: " + match.getIniziator().getEffectiveName(),"Würfelzahl: " + values.get(0),false);
        eb.addField("Mitspieler: " + match.getOpponent().getEffectiveName(),"Würfelzahl: " + values.get(1),false);
        Member winner = match.getWinner();
        if (winner != null) {
            eb.addField("","Gewinner ist " + winner.getAsMention(),false);
        } else {
            eb.addField("","Unentschieden. Keiner Gewinnt",false);
        }
        return eb;
    }

    public EmbedBuilder EBsettings (int mode) {
        EmbedBuilder eb = new EmbedBuilder();
        if (mode == 1) {
            eb.setDescription("Wartet auf Mitspieler...");
            eb.setColor(Color.GREEN);
        } else if (mode == 2) {
            eb.setColor(Color.WHITE);
        } else if (mode == 3) {
            eb.setColor(Color.YELLOW);
            eb.setDescription("Wartet auf Mitspieler...\n Anfrage steht aus!");
        }
        eb.setTitle("Dice Match");
        eb.setThumbnail("https://s8.directupload.net/images/210309/uwqklfqy.png");
        return eb;
    }
}
