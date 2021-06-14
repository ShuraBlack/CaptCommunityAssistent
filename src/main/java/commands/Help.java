package commands;

import commands.types.ServerCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

import java.awt.*;

public class Help implements ServerCommand {

    String mesId = "818784902601768972";

    @Override
    public void performCommand(Member m, TextChannel channel, Message message) {
        message.delete().queue();

        if(!m.hasPermission(channel, Permission.ADMINISTRATOR)) {
            return;
        }
        if (!message.getContentDisplay().equals("!help")) {
            return;
        }
        if (mesId.equals("")) {
            mesId = channel.sendMessage(createMessage().build()).complete().getId();
            channel.editMessageById(mesId,createMessage().build()).complete().pin().queue();
        } else {
            channel.editMessageById(mesId,createMessage().build()).queue();
        }
    }

    @Override
    public void reactionperform(Member m, TextChannel channel, String mesID
            , String emote, MessageReactionAddEvent event) { throw new UnsupportedOperationException(); }

    @Override
    public void privateperform(String command, User u) { throw new UnsupportedOperationException(); }

    public EmbedBuilder createMessage () {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.WHITE);
        eb.setThumbnail("https://s8.directupload.net/images/210309/lev2q2ma.png");
        eb.setTitle("Befehlsliste: ");
        eb.setDescription("Hier kannst du sehen welche Befehle du als normaler Nutzer ausführen darfst");
        eb.addField("__Zufallsbefehle:__","\n" +
                "**!dice** ```cs\n" +
                "wirft einen 6 seitigen Würfel\n" +
                "```\n" +
                "**!dice [zahl]**```cs\n" +
                "wirft einen [\"zahl\"] seitigen Würfel\n" +
                "```\n" +
                "**!dice match** ```cs\n" +
                "startet ein Würfelspiel, beidem ein anderer Nutzer gegen dich spielen kann\n" +
                "# Andere Nutzer können über Reaction beitreten\n" +
                "```\n" +
                "**!dice invitematch [userID]**```cs\n" +
                "Läd einen Nutzer zu einem aktiven *Dice Match* von dir ein\n" +
                "# Der Nutzer erhält ein Link zum Match\n" +
                "```\n" +
                "**!coinflip**```cs\n" +
                "wirft eine Münze mit Kopf oder Zahl\n" +
                "```\n" +
                "**!mdice [zahl]**```cs\n" +
                "wirft [\"zahl\"] an 6 seitgen Würfeln\n" +
                "```\n" +
                "**!mdice [zahlA] [zahlB]**```cs\n" +
                "wirft [\"zahlA\"] an [\"zahlB\"] seitigen Würfeln\n" +
                "```",false);
        eb.addField("__Playlist:__","\n" +
                "**!playlist add [playlistname] [link]**```cs\n" +
                "Fügt [\"link\"] in deine [\"playlistname\"] ein\n" +
                "```\n" +
                "**!playlist remove [ID]**```cs\n" +
                "Entfernt link mit [\"ID\"]\n" +
                "Wenn als [\"ID\"] \"all\" eingetragen wird, werden alle deine Lieder gelöscht\n" +
                "```\n" +
                "**!playlist show**```cs\n" +
                "Zeigt alle deine Lieder (playlistübergreifend)\n" +
                "```\n" +
                "**!playlist show [playlistname]**```cs\n" +
                "Zeigt alle Lieder aus [\"playlistname\"]\n" +
                "```\n" +
                "**!playlist list**```cs\n" +
                "Zeigt all deine Playlists\n```",false);
        eb.addField("__Temporäre VoiceChannel:__","Wenn du```cs\n" +
                "\"Create T:Voice\" beitritts, wird automatisch ein neuer VoiceChannel erstellt," +
                " mit der gleichen Maximalanzahl und zusätzlichen Rechten für den Ersteller\n```",false);
        return eb;
    }

    @Override
    public void performSlashCommand(SlashCommandEvent event) {

    }
}
