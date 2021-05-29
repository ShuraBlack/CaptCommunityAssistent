package commands;

import commands.types.ServerCommand;
import model.util.ChannelUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;


import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Search implements ServerCommand {

    private final List<String> open = new ArrayList<>();

    @Override
    public void performCommand(Member m, TextChannel channel, Message message) {

        String[] args = message.getContentDisplay().split(" ");
        message.delete().queue();

        if (!channel.getId().equals(ChannelUtil.SEARCH)) {
            return;
        }
        if (open.contains(m.getId())) {
            EmbedBuilder eb = new EmbedBuilder()
                    .setDescription(m.getAsMention() + ", du hast bereits eine offene Anfrage!\nWarte bis diese ausläuft");
            channel.sendMessage(eb.build()).queue(mes -> mes.delete().queueAfter(7, TimeUnit.SECONDS));
        }
        // command | game | maxplayer
        if (args.length == 4) {

            EmbedBuilder eb = new EmbedBuilder()
                    .setTitle("Suche Mitspieler...")
                    .setColor(Color.GREEN).setFooter("Nach 5min wird diese Nachricht gelöscht");

            if (args[1].equals("BTD6")) {
                eb.setThumbnail("https://s20.directupload.net/images/210321/xgk3rg9r.png")
                        .setDescription("**Game:** Bloons Tower Defends VI\n" +
                                "**Offene Plätze:** " + args[2] + "\n" +
                                "**Code:** " + args[3]);

            } else if (args[1].equals("AU")) {
                eb.setThumbnail("https://s16.directupload.net/images/210321/37dctj2p.png")
                        .setDescription("**Game:** Among Us\n" +
                                "**Offene Plätze:** " + args[2] + "\n" +
                                "**Code:** " + args[3]);

            } else {
                eb.setThumbnail("https://s8.directupload.net/images/210321/2utqgrd4.png")
                        .setDescription("**Game:**" + args[1] + "\n" +
                                "**Offene Plätze:** " + args[2] + "\n" +
                                "**Code:** " + args[3]);

            }

            channel.sendMessage(eb.build()).queue(mes -> {
                open.add(m.getId());
                mes.delete().queueAfter(5, TimeUnit.MINUTES, d -> open.remove(m.getId()));
            });

        } else if (args.length == 2 && args[1].equals("help")) {
            if (m.hasPermission(Permission.ADMINISTRATOR)) {
                EmbedBuilder eb = new EmbedBuilder()
                        .setColor(Color.WHITE)
                        .setTitle("Befehlsliste:")
                        .setDescription("Wenn du jemanden zum spielen suchst, kannst du diesen Channel dafür nutzen")
                        .addField("__Spielersuche:__","!search [gametitel] [anzahl von offenen plätzen] [lobbycode]" +
                                "```cs\nEs wird eine Nachricht erstellt, welche für 5min lang mit deinen Informationen bestehen bleibt\n" +
                                "Es gibt spezielle Icons bei [\"gametitel\"]:\n# BTD6 -> Bloons TD 6, AU -> Among US\n```",false);
                channel.sendMessage(eb.build()).queue();
            }
        }
    }

    @Override
    public void reactionperform(Member m, TextChannel channel, String mesID
            , String emote, MessageReactionAddEvent event) { throw new UnsupportedOperationException();}

    @Override
    public void privateperform(String command, User u) { throw new UnsupportedOperationException(); }

}
