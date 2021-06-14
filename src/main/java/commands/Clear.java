package commands;

import model.sql.LoadDriver;
import model.util.SQLUtil;
import commands.types.ServerCommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Clear implements ServerCommand {

    private List<String> activmessages = new LinkedList<>();

    /**
     * Delets Messages in an channel
     * @param m Member-Discord
     * @param channel Channel which the command got called
     * @param message entire message of member
     */
    @Override
    public void performCommand(Member m, TextChannel channel, Message message) {
        message.delete().queue();

        if(!m.hasPermission(channel, Permission.MESSAGE_MANAGE)) {
            return;
        }

        LoadDriver ld = new LoadDriver();
        ResultSet rs = ld.executeSQL(SQLUtil.SELECTMESSAGESIDS());
        try {
            while (rs.next()) {
                activmessages.add(rs.getString(1));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        String[] args = message.getContentDisplay().split(" ");
        if (args.length == 1) {
            try {
                channel.purgeMessages(get(channel));
                channel.sendMessage("Alle verfügbaren Nachrichten wurden gelöscht!")
                        .complete().delete().queueAfter(3, TimeUnit.SECONDS);
            } catch (IllegalArgumentException ignored) {
            }
        } else if (args.length == 2 && args[1].equals("reset")) {
            if (!m.getId().equals("286628057551208450")) {
                return;
            }
            channel.createCopy().setPosition(channel.getPosition()).queue();
            channel.delete().queue();
        } else if (args.length == 2) {
            try {
                int amount = Integer.parseInt(args[1]);
                channel.purgeMessages(get(channel, amount));
                channel.sendMessage(amount + " Nachrichten wurden gelöscht!")
                        .complete().delete().queueAfter(3, TimeUnit.SECONDS);
            } catch (NumberFormatException ignored) { }
        }
        activmessages.clear();
        ld.close();
    }

    @Override
    public void reactionperform(Member m, TextChannel channel, String mesID, String emote, MessageReactionAddEvent event) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void privateperform(String command, User u) {
        throw new UnsupportedOperationException();
    }

    /**
     * Create an List with the History of an channel
     * @param channel in which it got called
     * @return up to 100 entries in a List
     */
    private List<Message> get(MessageChannel channel) {
        List<Message> mes = new ArrayList<>();
        int i = 100;
        for (Message message : channel.getIterableHistory().cache(false)) {
            if(!message.isPinned()) {
                if (activmessages.contains(message.getId())) {
                    continue;
                }
                mes.add(message);
                if(--i <= 0) {
                    break;
                }
            }
        }
        return mes;
    }

    /**
     * Create an List with the History of an channel
     * @param channel in which it got called
     * @param amount of how many messages should be saved
     * @return List based on amount
     */
    private List<Message> get(MessageChannel channel, int amount) {
        List<Message> mes = new ArrayList<>();
        int i = amount + 1;
        for (Message message : channel.getIterableHistory().cache(false)) {
            if(!message.isPinned()) {
                if (activmessages.contains(message.getId())) {
                    continue;
                }
                mes.add(message);
                if(--i <= 0) {
                    break;
                }
            }
        }
        return mes;
    }

    @Override
    public void performSlashCommand(SlashCommandEvent event) {

    }
}
