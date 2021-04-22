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

public class Mdice implements ServerCommand {
    @Override
    public void performCommand(Member m, TextChannel channel, Message message) {

        message.delete().queue();
        Random rand = new Random();

        String[] args = message.getContentDisplay().split(" ");

        if (args.length == 2) {
            int total = 0;
            int dices = 0;
            StringBuilder s = new StringBuilder();
            try {
                dices = Integer.parseInt(args[1]);
                for (int i = 0; i < dices ; i++) {
                    int value = rand.nextInt((6 - 1) + 1) + 1;
                    s.append(value).append(", ");
                    total += value;
                }
            } catch (NumberFormatException ignored) { }

            EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(Color.WHITE);
            eb.setTitle("Würfel fallen für");
            eb.setDescription(m.getAsMention() + " auf:");
            eb.addField("",s.toString() + "\nTotal: " + total + "/" + (6 * dices),false);

            channel.sendMessage(eb.build())
                    .complete().delete().queueAfter(15, TimeUnit.SECONDS);
        }

        if (args.length == 3) {
            int total = 0;
            int dices = 0;
            int max = 0;
            StringBuilder s = new StringBuilder();
            try {
                dices = Integer.parseInt(args[1]);
                max = Integer.parseInt(args[2]);
                for (int i = 0; i < dices ; i++) {
                    int value = rand.nextInt((max - 1) + 1) + 1;
                    s.append(value).append(", ");
                    total += value;
                }
            } catch (NumberFormatException ignored) { }

            EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(Color.WHITE);
            eb.setTitle("Würfel fallen für");
            eb.setDescription(m.getAsMention() + " auf:");
            eb.addField("",s.toString() + "\nTotal: " + total + "/" + (max * dices) + "\nWerte zwischen 1 bis " + max,false);

            channel.sendMessage(eb.build())
                    .complete().delete().queueAfter(20, TimeUnit.SECONDS);
        }
    }

    @Override
    public void reactionperform(Member m, TextChannel channel, String mesID
            , String emote, MessageReactionAddEvent event) { throw new UnsupportedOperationException(); }

    @Override
    public void privateperform(String command, User u) { throw new UnsupportedOperationException(); }
}
