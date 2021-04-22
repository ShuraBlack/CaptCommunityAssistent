package commands;

import commands.types.ServerCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

import java.awt.*;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

public class Infoevent implements ServerCommand {

    Map<String, EventMessage> messages = new TreeMap<>();

    @Override
    public void performCommand(Member m, TextChannel channel, Message message) {
        message.delete().queue();

        if(!m.hasPermission(channel, Permission.ADMINISTRATOR)) {
            return;
        }

        String command = message.getContentDisplay();
        String xcommand = command.replaceFirst(" ","§");
        String[] split = xcommand.split("§");
        String[] parts = split[1].split("\n");
        String[] change =command.split(" ");
        EventMessage em;
        if (parts.length == 3) {

            em = new EventMessage(parts[0],parts[1].replaceAll("\\$","\n")
                    ,parts[2].replaceAll("\\$","\n"),m.getId());

        } else if (parts.length == 2) {

            em = new EventMessage(parts[0], parts[1].replaceAll("\\$","\n"), m.getId());

        } else if (change.length == 2) {

            em = messages.get(change[1]);
            if (em == null) {
                return;
            }
            if (!em.authorID.equals(m.getId())) {
                return;
            }

            em.status = !em.status;
        } else if (change.length == 3 && change[1].equals("remove")) {

            em = messages.get(change[2]);
            if (em == null) {
                return;
            }
            if (!em.authorID.equals(m.getId())) {
                return;
            }

            String mesID = em.mesID;
            messages.remove(change[2]);
            channel.deleteMessageById(mesID).queue();

            return;

        } else {
            return;
        }

        messages.put(String.valueOf(em.ID),em);
        EmbedBuilder eb = createEventMessage(em);

        if (em.mesID.equals("")) {
            em.mesID = channel.sendMessage(eb.build()).complete().getId();
        } else {
            channel.editMessageById(em.mesID, eb.build()).queue();
        }
    }

    @Override
    public void reactionperform(Member m, TextChannel channel, String mesID
            , String emote, MessageReactionAddEvent event) { throw new UnsupportedOperationException(); }

    @Override
    public void privateperform(String command, User u) { throw new UnsupportedOperationException(); }

    public EmbedBuilder createEventMessage(EventMessage em) {
        EmbedBuilder eb = new EmbedBuilder();
        if (em.status) {
            eb.setColor(Color.GREEN);
            eb.setDescription("```diff\n" +
                    "+ ONLINE \n" +
                    "```");
        } else {
            eb.setColor(Color.RED);
            eb.setDescription("```diff\n" +
                    "- OFFLINE\n" +
                    "```");
        }
        eb.setTitle(em.name);
        eb.addField("","" + em.information,false);
        if (!em.note.equals("")) {
            eb.addField("","" + em.note,false);
        }
        eb.setFooter("IeID:  " + em.ID);
        return eb;
    }

    private class EventMessage {

        private final String name;
        private boolean status = false;
        private final String information;
        private String note = "";
        private String mesID = "";
        private final String authorID;
        private int ID;

        public EventMessage (String name, String information, String note, String authorID) {
            this.name = name;
            this.information = information;
            this.note = note;
            this.authorID = authorID;
            generateID();
        }

        public EventMessage (String name, String information, String authorID) {
            this.name = name;
            this.information = information;
            this.authorID = authorID;
            generateID();
        }

        private void setMesID(String mesID) {
            if (!this.mesID.equals("")) {
                return;
            }
            this.mesID = mesID;
        }

        private void generateID () {
            Random rdm = new Random();
            this.ID = Math.abs(name.hashCode());
        }

    }
}
