package commands;

import commands.types.ServerCommand;
import model.util.ChannelUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

public class News implements ServerCommand {

    String mesID = "844332779176984586";


    @Override
    public void performCommand(Member m, TextChannel channel, Message message) {
        message.delete().queue();
        if (!m.hasPermission(Permission.ADMINISTRATOR)) {
            return;
        }
        if (!channel.getId().equals(ChannelUtil.SUBS)) {
            return;
        }
        String[] args = message.getContentDisplay().split(" ");
        if (args.length == 1) {
            final Emote league = channel.getGuild().getEmoteById("804426711479484417");
            final Emote warframe = channel.getGuild().getEmoteById("804427054015447062");
            final Emote moon = channel.getGuild().getEmoteById("804427189969747999");
            final Emote mc = channel.getGuild().getEmoteById("815238918496583710");
            final Emote sati = channel.getGuild().getEmoteById("815238883620159548");
            final Emote casino = channel.getGuild().getEmoteById("845057339145191434");
            final String ex = "❌";
            EmbedBuilder eb = createMessage(league, warframe, moon, mc, sati, casino, ex);
            //mesID = channel.sendMessage(eb.build()).complete().getId();

            channel.editMessageById(mesID, eb.build()).complete().clearReactions().queue();

            channel.editMessageById(mesID,eb.build()).complete().addReaction(league).queue();
            channel.editMessageById(mesID,eb.build()).complete().addReaction(warframe).queue();
            channel.editMessageById(mesID,eb.build()).complete().addReaction(moon).queue();
            channel.editMessageById(mesID,eb.build()).complete().addReaction(mc).queue();
            channel.editMessageById(mesID,eb.build()).complete().addReaction(sati).queue();
            channel.editMessageById(mesID,eb.build()).complete().addReaction(casino).queue();
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
                !emote.equals("casino") &&
                !emote.equals("❌")) {
            return;
        }
        Role leaguerole = channel.getGuild().getRoleById("804433890143895552");
        Role warframerole = channel.getGuild().getRoleById("804433895202226197");
        Role moonrole = channel.getGuild().getRoleById("804433899136221254");
        Role mcrole = channel.getGuild().getRoleById("815012063013109790");
        Role satirole = channel.getGuild().getRoleById("815011932934504449");
        Role casinorole = channel.getGuild().getRoleById("845055075947905043");

        Emote league = channel.getGuild().getEmoteById("804426711479484417");
        Emote warframe = channel.getGuild().getEmoteById("804427054015447062");
        Emote moon = channel.getGuild().getEmoteById("804427189969747999");
        Emote mc = channel.getGuild().getEmoteById("815238918496583710");
        Emote sati = channel.getGuild().getEmoteById("815238883620159548");
        Emote casino = channel.getGuild().getEmoteById("845057339145191434");
        String ex = "❌";

        EmbedBuilder ebx = createMessage(league, warframe, moon, mc, sati, casino, ex);

        if (emote.equals("league")) {
            event.getGuild().addRoleToMember(m,leaguerole).complete();
            ebx.addBlankField(false);
            ebx.addField("Letzter Status:",m.getEffectiveName() + " hat den League Of Legends Channel abonniert!",false);
        } else if (emote.equals("warframe")) {
            event.getGuild().addRoleToMember(m,warframerole).complete();
            ebx.addBlankField(false);
            ebx.addField("Letzter Status:",m.getEffectiveName() + " hat den Warframe Channel abonniert!",false);
        } else if (emote.equals("moon")) {
            event.getGuild().addRoleToMember(m, moonrole).complete();
            ebx.addBlankField(false);
            ebx.addField("Letzter Status:",m.getEffectiveName() + " hat den Moonstruck Channel abonniert!",false);
        } else if (emote.equals("mc")) {
            event.getGuild().addRoleToMember(m, mcrole).complete();
            ebx.addBlankField(false);
            ebx.addField("Letzter Status:",m.getEffectiveName() + " hat den Minecraft Channel abonniert!",false);
        } else if (emote.equals("sati")) {
            event.getGuild().addRoleToMember(m, satirole).complete();
            ebx.addBlankField(false);
            ebx.addField("Letzter Status:",m.getEffectiveName() + " hat den Satisfactory Channel abonniert!",false);
        } else if (emote.equals("casino")) {
            if (m.getRoles().stream().map(role -> role.getName()).noneMatch(name -> name.equals("Member")
                    || name.equals("Veteran") || name.equals("Moderator"))) {
                ebx.addField("Letzter Status:",m.getEffectiveName() + " zuweisung nicht möglich! Du bist kein Member",false);
                return;
            }
            event.getGuild().addRoleToMember(m, casinorole).complete();
            ebx.addBlankField(false);
            ebx.addField("Letzter Status:",m.getEffectiveName() + " hat die Casino Channels abonniert!",false);
        } else if (emote.equals("❌")) {
            event.getGuild().removeRoleFromMember(m,leaguerole).complete();
            event.getGuild().removeRoleFromMember(m,warframerole).complete();
            event.getGuild().removeRoleFromMember(m,moonrole).complete();
            event.getGuild().removeRoleFromMember(m,mcrole).complete();
            event.getGuild().removeRoleFromMember(m,satirole).complete();
            event.getGuild().removeRoleFromMember(m,casinorole).complete();
            ebx.addField("","",false);
            ebx.addField("Letzter Status:",m.getEffectiveName() + " hat alle News und Partner deabonniert!",false);
        } else {
            return;
        }
        channel.editMessageById(mesID,ebx.build()).queue();
    }

    @Override
    public void privateperform(String command, User u) { throw new UnsupportedOperationException(); }

    public EmbedBuilder createMessage (Emote league, Emote warframe, Emote moon, Emote mc, Emote sati, Emote casino, String ex) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("News & Partner - Abonnieren");
        eb.setDescription("Über diese Nachricht kann der Nutzer bestimmen, welchen News Channel er abonnieren/sehen möchte");
        eb.addField("","Verfügbare Channel:",false);
        eb.addField(league.getAsMention() + " League of Legends,","Updates, game changes usw. von dem eigenen League Discord Server",false);
        eb.addField(warframe.getAsMention() + " Warframe,","Updates, game changes usw. von dem eigenen Warframe Discord Server",false);
        eb.addField(moon.getAsMention() + " Moonstruck,","Information über den Streamer, sowie Ankündigungen für den Stream",false);
        eb.addField(mc.getAsMention() + " Minecraft,","Updates, game changes usw. von dem eigenen Minecraft Discord Server",false);
        eb.addField(sati.getAsMention() + " Satisfactory,","Updates, game changes usw. von dem eigenen Satisfactory Discord Server",false);
        eb.addField(casino.getAsMention() + " Games,","Schaltet alle Server privaten Games Channel frei\n" +
                "```diff\n- Du benötigst ebenfalls mindestens den Member Rang und bestätigst damit auch das du über 18 bist\n```",false);
        eb.addField(ex + " Entfernen,","Entfernt alle abonnierten Channel von dir",false);
        return eb;
    }
}
