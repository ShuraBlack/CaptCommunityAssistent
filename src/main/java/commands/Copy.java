package commands;

import commands.types.ServerCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

import java.io.File;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Copy implements ServerCommand {
    @Override
    public void performCommand(Member m, TextChannel channel, Message message) {
        message.delete().queue();

        if(!m.hasPermission(channel, Permission.MESSAGE_MANAGE)) {
            return;
        }

        String[] args = message.getContentDisplay().split(" ");

        try {
            int amount = Integer.parseInt(args[2]);
            TextChannel newChannel = channel.getGuild().getTextChannelById(args[1]);
            assert newChannel != null;
            if(!m.hasPermission(newChannel, Permission.MESSAGE_MANAGE)) {
                return;
            }
            List<Message> messages = get(channel, amount);
            Collections.reverse(messages);
            for (Message mes : messages) {
                EmbedBuilder eb = new EmbedBuilder()
                        .setAuthor(mes.getAuthor().getName())
                        .setFooter(timeconvert(mes.getTimeCreated()))
                        .setDescription(mes.getContentDisplay())
                        .setThumbnail(mes.getAuthor().getAvatarUrl());
                if (!mes.getAttachments().isEmpty()) {
                    List<Message.Attachment> attach = mes.getAttachments();
                    for (Message.Attachment ma : attach) {
                        eb.setImage(ma.getUrl());
                        newChannel.sendMessage(eb.build()).queue();
                    }
                } else {
                    if (mes.getContentDisplay().contains("https")) {
                        String[] parts = mes.getContentDisplay().split(" ");
                        if (parts.length != 1) {
                            return;
                        }
                        for (String s : parts) {
                            if (s.contains("http")) {
                                eb.setImage(s);
                                break;
                            }
                        }
                    }
                    newChannel.sendMessage(eb.build()).queue();
                }
            }
            //channel.purgeMessages(messages);
            channel.sendMessage(amount + " Nachrichten wurden verschoben!")
                    .complete().delete().queueAfter(3, TimeUnit.SECONDS);

        } catch (NumberFormatException ignored) { }
    }

    @Override
    public void reactionperform(Member m, TextChannel channel, String mesID, String emote, MessageReactionAddEvent event) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void privateperform(String command, User u) {
        throw new UnsupportedOperationException();
    }

    private List<Message> get(MessageChannel channel, int amount) {
        List<Message> mes = new ArrayList<>();
        int i = amount;
        boolean first = true;
        for (Message message : channel.getIterableHistory().cache(false)) {
            if (first) {
                first = false;
                continue;
            }
            if(!message.isPinned()) {
                mes.add(message);
                if(--i <= 0) {
                    break;
                }
            }
        }
        return mes;
    }

    private String timeconvert (OffsetDateTime time) {

        String s = time.getDayOfMonth() + "." +
                time.getMonth() + "." +
                time.getYear() + " - " +
                time.getHour() + ":" +
                time.getMinute() + ":" +
                time.getSecond();
        return s;
    }
}
