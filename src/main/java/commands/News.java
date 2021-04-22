package commands;

import commands.types.ServerCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

public class News implements ServerCommand {

    String mesID = "815242575644196865";


    @Override
    public void performCommand(Member m, TextChannel channel, Message message) {
        message.delete().queue();
        if (!m.hasPermission(Permission.ADMINISTRATOR)) {
            return;
        }
        if (!channel.getId().equals("799449909090713631")) {
            return;
        }
        String[] args = message.getContentDisplay().split(" ");
        if (args.length == 1) {
            final Emote league = channel.getGuild().getEmoteById("804426711479484417");
            final Emote warframe = channel.getGuild().getEmoteById("804427054015447062");
            final Emote moon = channel.getGuild().getEmoteById("804427189969747999");
            final Emote mc = channel.getGuild().getEmoteById("815238918496583710");
            final Emote sati = channel.getGuild().getEmoteById("815238883620159548");
            final String ex = "❌";
            EmbedBuilder eb = createMessage(league, warframe, moon, mc, sati, ex);
            //mesID = channel.sendMessage(eb.build()).complete().getId();

            channel.editMessageById(mesID, eb.build()).complete().clearReactions().queue();

            channel.editMessageById(mesID,eb.build()).complete().addReaction(league).queue();
            channel.editMessageById(mesID,eb.build()).complete().addReaction(warframe).queue();
            channel.editMessageById(mesID,eb.build()).complete().addReaction(moon).queue();
            channel.editMessageById(mesID,eb.build()).complete().addReaction(mc).queue();
            channel.editMessageById(mesID,eb.build()).complete().addReaction(sati).queue();
            channel.editMessageById(mesID,eb.build()).complete().addReaction(ex).queue();
        }
    }

    @Override
    public void reactionperform(Member m, TextChannel channel, String mesID, String emote, MessageReactionAddEvent event) {
        event.getReaction().removeReaction(m.getUser()).queue();
        if (!emote.equals("league") &&
                !emote.equals("warframe") &&
                !emote.equals("moon") &&
                !emote.equals("mc") &&
                !emote.equals("sati") &&
                !emote.equals("❌")) {
            return;
        }
        Role leaguerole = channel.getGuild().getRoleById("804433890143895552");
        Role warframerole = channel.getGuild().getRoleById("804433895202226197");
        Role moonrole = channel.getGuild().getRoleById("804433899136221254");
        Role mcrole = channel.getGuild().getRoleById("815012063013109790");
        Role satirole = channel.getGuild().getRoleById("815011932934504449");

        Emote league = channel.getGuild().getEmoteById("804426711479484417");
        Emote warframe = channel.getGuild().getEmoteById("804427054015447062");
        Emote moon = channel.getGuild().getEmoteById("804427189969747999");
        Emote mc = channel.getGuild().getEmoteById("815238918496583710");
        Emote sati = channel.getGuild().getEmoteById("815238883620159548");
        String ex = "❌";

        EmbedBuilder ebx = createMessage(league, warframe, moon, mc, sati, ex);

        if (emote.equals("league")) {
            event.getGuild().addRoleToMember(m,leaguerole).complete();
            ebx.addField("","",false);
            ebx.addField("Letzter Status:",m.getEffectiveName() + " hat den League Of Legends Channel abonniert!",false);
        } else if (emote.equals("warframe")) {
            event.getGuild().addRoleToMember(m,warframerole).complete();
            ebx.addField("","",false);
            ebx.addField("Letzter Status:",m.getEffectiveName() + " hat den Warframe Channel abonniert!",false);
        } else if (emote.equals("moon")) {
            event.getGuild().addRoleToMember(m, moonrole).complete();
            ebx.addField("", "", false);
            ebx.addField("Letzter Status:",m.getEffectiveName() + " hat den Moonstruck Channel abonniert!",false);
        } else if (emote.equals("mc")) {
            event.getGuild().addRoleToMember(m, mcrole).complete();
            ebx.addField("", "", false);
            ebx.addField("Letzter Status:",m.getEffectiveName() + " hat den Minecraft Channel abonniert!",false);
        } else if (emote.equals("sati")) {
            event.getGuild().addRoleToMember(m, satirole).complete();
            ebx.addField("", "", false);
            ebx.addField("Letzter Status:",m.getEffectiveName() + " hat den Satisfactory Channel abonniert!",false);
        } else if (emote.equals("❌")) {
            event.getGuild().removeRoleFromMember(m,leaguerole).complete();
            event.getGuild().removeRoleFromMember(m,warframerole).complete();
            event.getGuild().removeRoleFromMember(m,moonrole).complete();
            event.getGuild().removeRoleFromMember(m,mcrole).complete();
            event.getGuild().removeRoleFromMember(m,satirole).complete();
            ebx.addField("","",false);
            ebx.addField("Letzter Status:",m.getEffectiveName() + " hat alle News und Partner deabonniert!",false);
        } else {
            return;
        }
        channel.editMessageById(mesID,ebx.build()).queue();
    }

    @Override
    public void privateperform(String command, User u) { throw new UnsupportedOperationException(); }

    public EmbedBuilder createMessage (Emote league, Emote warframe, Emote moon, Emote mc, Emote sati, String ex) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("News & Partner - Abonnieren");
        eb.setDescription("Über diese Nachricht kann der Nutzer bestimmen, welchen News Channel er abonnieren/sehen möchte");
        eb.addField("","Verfügbare Channel:",false);
        eb.addField(league.getAsMention() + " League of Legends,","Updates, game changes usw. von dem eigenen League Discord Server",false);
        eb.addField(warframe.getAsMention() + " Warframe,","Updates, game changes usw. von dem eigenen Warframe Discord Server",false);
        eb.addField(moon.getAsMention() + " Moonstruck,","Information über den Streamer, sowie Ankündigungen für den Stream",false);
        eb.addField(mc.getAsMention() + " Minecraft,","Updates, game changes usw. von dem eigenen Minecraft Discord Server",false);
        eb.addField(sati.getAsMention() + " Satisfactory,","Updates, game changes usw. von dem eigenen Satisfactory Discord Server",false);
        eb.addField(ex + " Entfernen,","Entfernt alle abonnierten Channel von dir",false);
        return eb;
    }
}
