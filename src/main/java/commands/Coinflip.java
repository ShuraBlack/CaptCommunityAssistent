package commands;

import commands.types.ServerCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

import java.awt.*;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Coinflip implements ServerCommand {

    @Override
    public void performCommand(Member m, TextChannel channel, Message message) {

        message.delete().queue();

        Random rand = new Random();
        int rdmNum = rand.nextInt(2);

        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.WHITE);
        eb.setTitle("Münze fällt für");
        eb.setDescription(m.getAsMention() + " auf:");

        if(rdmNum == 1) {
            // HEAD
            eb.setThumbnail("https://s8.directupload.net/images/210307/zsio9j9r.png");
            channel.sendMessage(eb.build())
                    .complete().delete().queueAfter(5, TimeUnit.SECONDS);
        } else {
            // TAIL
            eb.setThumbnail("https://i.ibb.co/JqgDpXk/number.png");
            channel.sendMessage(eb.build())
                    .complete().delete().queueAfter(5, TimeUnit.SECONDS);
        }
    }

    @Override
    public void reactionperform(Member m, TextChannel channel, String mesID
            , String emote, MessageReactionAddEvent event) { throw new UnsupportedOperationException(); }

    @Override
    public void privateperform(String command, User u) { throw new UnsupportedOperationException(); }
}
