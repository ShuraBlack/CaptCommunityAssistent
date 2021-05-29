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

public class Rules implements ServerCommand {

    @Override
    public void performCommand(Member m, TextChannel channel, Message message) {

        message.delete().queue();

        if (!m.hasPermission(Permission.ADMINISTRATOR)) {
            return;
        }

        if (!channel.getId().equals(ChannelUtil.GUIDELINES)) {
            return;
        }
        String[] args = message.getContentDisplay().split(" ");

        if (args.length == 1) {
            EmbedBuilder ebtop = new EmbedBuilder();
            ebtop.setColor(Color.WHITE);
            ebtop.setTitle("Server-Richtlinien");
            ebtop.setDescription("```INFO: Bei Missachtung der Server Regeln wird der Nutzer mit einem Kick/Bann bestraft```\n" +
                    "Folgende Handlungen sind daher unerwünscht:");
            ebtop.addField("**§1**","Beleidigungen, unhöfliches oder nervendes Benehmen, sowie Diskriminierungen",false);
            ebtop.addField("**§2**","Das verhindern jeglicher Kommunikation zu Server Moderatoren",false);
            ebtop.addField("**§3**","Provokation jeglicher Art",false);
            ebtop.addField("**§4**","Nicknames werden, wenn sie *$1* verletzen, auf dem Server verändert",false);
            ebtop.addField("**§5**","Links mit unangemessenem Inhalt (z.B. Pornografische oder Gewaltätige Darstellung, ...)",false);
            ebtop.addField("**§6**","Eigenwerbung für z.B. Youtube Kanal, Facebook- und Twitter Fanpages, sowie Klans/Teams",false);
            ebtop.addField("**§7**","Hochgeladene Daten dürfen *$1,3,5,6* nicht verletzten",false);
            ebtop.addField("**§8** ","Spam des Voice- oder TextChannels",false);
            ebtop.addField("**§9**","ungekennzeichnete Bots ohne Absprache",false);
            ebtop.addField("**§10**","Betrug jeglicher Art",false);
            ebtop.setFooter(" - Gültig ab dem 18.01.2021 | 19:50 -");

            EmbedBuilder ebbottom = new EmbedBuilder();
            ebbottom.setColor(Color.RED);
            ebbottom.addField("Meldet Probleme oder Beschwerden der Server Moderation",
                    "Wie auch beim Gesetzt: Unwissenheit schützt nicht vor Strafe und deswegen " +
                            "empfehlen wir die Regeln gut durchzulesen und zu kennen",false);

            channel.sendMessage(ebtop.build()).queue();
            channel.sendMessage(ebbottom.build()).queue();
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
}
