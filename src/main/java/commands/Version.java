package commands;

import startup.DiscordBot;
import commands.types.ServerCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class Version implements ServerCommand {
    @Override
    public void performCommand(Member m, TextChannel channel, Message message) {
        message.delete().queue();
        String command = message.getContentDisplay();
        if (command.equals("!version")) {
            String version = "NOT DEFINED";
            if (DiscordBot.PROPERTIES.containsKey("version")) {
                version = DiscordBot.PROPERTIES.getProperty("version");
            }
            EmbedBuilder eb = new EmbedBuilder()
                .setColor(Color.WHITE)
                .setThumbnail("https://s16.directupload.net/images/210310/temp/od2evm2e.png")
                .setTitle("CaptCommunity Assisstent")
                .setDescription("**Version** " + version + "\n- Add Reaction Control to MusicPlayer")
                .setFooter("Made by Shurablack (You can appreciate my work through an donation if you want)");

            channel.sendMessage(eb.build()).complete().delete().queueAfter(10, TimeUnit.SECONDS);
        }
    }

    @Override
    public void reactionperform(Member m, TextChannel channel, String mesID
            , String emote, MessageReactionAddEvent event) { throw new UnsupportedOperationException(); }

    @Override
    public void privateperform(String command, User u) { throw new UnsupportedOperationException(); }
}
