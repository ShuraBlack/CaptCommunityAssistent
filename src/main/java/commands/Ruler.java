package commands;

import com.mysql.cj.log.Slf4JLogger;
import commands.types.ServerCommand;
import model.util.ChannelUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.*;

public class Ruler implements ServerCommand {

    private final Set<String> ruleread = new TreeSet<>();
    private final Map<String,String> request = new TreeMap<>();
    private final Member[] memberlist = new Member[10];
    String mesID = "804077314023227392";

    @Override
    public void performCommand(Member m, TextChannel channel, Message message) {
        message.delete().queue();
        if (!m.hasPermission(Permission.ADMINISTRATOR)) {
            return;
        }

        if (!channel.getId().equals(ChannelUtil.ROLES)) {
            return;
        }
        String[] args = message.getContentDisplay().split(" ");
        if (args.length == 1) {
            final Emote bronze = channel.getGuild().getEmoteById("844342554745110528");
            final Emote silver = channel.getGuild().getEmoteById("844342560309903370");
            final Emote gold = channel.getGuild().getEmoteById("844342559723880458");
            final Emote platinum = channel.getGuild().getEmoteById("844342561073659934");

            EmbedBuilder eb = createMessage(channel);

            //channel.editMessageById(mesID, eb.build()).complete().clearReactions().queue();
            //channel.editMessageById(mesID,eb.build()).complete().addReaction("📃").queue();
            //channel.editMessageById(mesID,eb.build()).complete().addReaction(bronze).queue();
            //channel.editMessageById(mesID,eb.build()).complete().addReaction(silver).queue();
            //channel.editMessageById(mesID,eb.build()).complete().addReaction(gold).queue();
            //channel.editMessageById(mesID,eb.build()).complete().addReaction(platinum).queue();
            //mesID = channel.sendMessage(eb.build()).complete().getId();
        }
    }

    @Override
    public void reactionperform(Member m, TextChannel channel, String mesID, String emote, MessageReactionAddEvent event) {
        
        final Emote bronze = channel.getGuild().getEmoteById("844342554745110528");
        final Emote silver = channel.getGuild().getEmoteById("844342560309903370");
        final Emote gold = channel.getGuild().getEmoteById("844342559723880458");
        final Emote platinum = channel.getGuild().getEmoteById("844342561073659934");

        Role guest = channel.getGuild().getRoleById("384133791595102218");
        Role member = channel.getGuild().getRoleById("286631357772201994");

        event.getReaction().removeReaction(m.getUser()).queue();
        if (emote.equals("📃")) {
            if (!ruleread.contains(m.getId())) {
                ruleread.add(m.getId());
                EmbedBuilder eb = createMessage(channel);
                eb.addField("","",false);
                eb.addField("Letzter Status:",m.getEffectiveName() + " hat die Serverregeln akzeptiert!",false);
                channel.editMessageById(mesID,eb.build()).queue();
                Slf4JLogger logger = new Slf4JLogger("Ruler.Rules");
                logger.logInfo(m.getEffectiveName() + " accepted the Server rules");
            }
            return;
        }
        if (!ruleread.contains(m.getId())) {
            return;
        }
        if (emote.equals(bronze.getName())) {
            event.getGuild().addRoleToMember(m,guest).queue();
            event.getGuild().removeRoleFromMember(m,member).queue();

            EmbedBuilder eb = createMessage(channel);
            eb.addField("","",false);
            eb.addField("Letzter Status:",m.getEffectiveName() + " wurde zum Guest ernannt!",false);
            channel.editMessageById(mesID,eb.build()).queue();

        } else if (emote.equals(silver.getName())) {
            event.getGuild().addRoleToMember(m,member).queue();
            event.getGuild().removeRoleFromMember(m,guest).queue();

            EmbedBuilder eb = createMessage(channel);
            eb.addField("","",false);
            eb.addField("Letzter Status:",m.getEffectiveName() + " wurde zum Guest ernannt!",false);
            channel.editMessageById(mesID,eb.build()).queue();

        } else if (emote.equals(gold.getName())) {
            if (request.containsKey(m.getId())) {
                return;
            }
            User creator = m.getJDA().retrieveUserById("286628057551208450").complete();
            LocalDateTime time = LocalDateTime.now();
            EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(Color.WHITE);
            eb.setTitle("Ranganfrage auf > CaptCommunity <");
            eb.addField("Member: " + m.getEffectiveName(),
                    "\nID: " + m.getId() + "\nRang: " + "Veteran " + gold.getAsMention() +
                            "\n\nAntworte mit -> !ruler Y/N ID",false);
            eb.setFooter(time.getDayOfMonth() + "." + time.getMonthValue() + "." + time.getYear() + " | "
                    + time.plusHours(2).getHour() + ":" + time.getMinute() + ":" + time.getSecond());
            assert creator != null;
            creator.openPrivateChannel().complete().sendMessage(eb.build()).queue();
            request.put(m.getId(),"286631270258180117");
            for (int i = 0; i < 10; i++) {
                if (memberlist[i] == null) {
                    memberlist[i] = m;
                }
            }

            EmbedBuilder ebx = createMessage(channel);
            ebx.addField("","",false);
            ebx.addField("Letzter Status:",m.getEffectiveName() + " hat eine Anfrage für Veteran abgegeben!",false);
            channel.editMessageById(mesID,ebx.build()).queue();
        } else if (emote.equals(platinum.getName())) {
            if (request.containsKey(m.getId())) {
                return;
            }
            User creator = m.getJDA().retrieveUserById("286628057551208450").complete();
            LocalDateTime time = LocalDateTime.now();
            EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(Color.WHITE);
            eb.setTitle("Ranganfrage auf > CaptCommunity <");
            eb.addField("Member: " + m.getEffectiveName(),
                    "\nID: " + m.getId() + "\nRang: " + "Moderator " + platinum.getAsMention() +
                    "\n\nAntworte mit -> !ruler Y/N ID",false);
            eb.setFooter(time.getDayOfMonth() + "." + time.getMonthValue() + "." + time.getYear() + " | "
                    + time.plusHours(2).getHour() + ":" + time.getMinute() + ":" + time.getSecond());
            creator.openPrivateChannel().complete().sendMessage(eb.build()).queue();
            request.put(m.getId(),"286631247315337219");
            for (int i = 0; i < 10; i++) {
                if (memberlist[i] == null) {
                    memberlist[i] = m;
                }
            }

            EmbedBuilder ebx = createMessage(channel);
            ebx.addField("","",false);
            ebx.addField("Letzter Status:",m.getEffectiveName() + " hat eine Anfrage für Moderator abgegeben!",false);
            channel.editMessageById(mesID,ebx.build()).queue();
        }
    }

    @Override
    public void privateperform(String command, User u) {
        if (u.getId().equals("286628057551208450")) {
            System.out.println(u.getName() + " (" + command + ")");
            String[] parts = command.split(" ");
            // !ruler Y/N ID
            if (parts.length == 3) {
                Member m = null;
                for (Member x : memberlist) {
                    if (x == null) {
                        continue;
                    }
                    if (x.getId().equals(parts[2])) {
                        m = x;
                        for (int i = 0; i < 10; i++) {
                            if (memberlist[i].getId().equals(x.getId())) {
                                memberlist[i] = null;
                            }
                        }
                        break;
                    }
                }
                assert m != null;
                if (parts[1].equals("Y")) {
                    Role newrole = m.getGuild().getRoleById(request.get(parts[2]));
                    m.getGuild().addRoleToMember(m,newrole).queue();
                    request.remove(parts[2]);

                    EmbedBuilder eb = new EmbedBuilder()
                            .setThumbnail(u.getJDA().getSelfUser().getAvatarUrl())
                            .setColor(Color.GREEN)
                            .setTitle("CaptCommunity Server")
                            .setDescription("Antrag auf Rangerhöhung")
                            .addField("","Ihre Anfrage wurde angenommen!\n" +
                                    "Viel spaß mit dem neuen Rang. Melde dich bei Fragen an die Moderation",false);
                    LocalDateTime time = LocalDateTime.now();
                    eb.setFooter(time.getDayOfMonth() + "." + time.getMonthValue() + "." + time.getYear() + " | "
                            + time.plusHours(2).getHour() + ":" + time.getMinute() + ":" + time.getSecond() + " | "
                    + "CaptCommunity Team");
                    m.getUser().openPrivateChannel().complete().sendMessage(eb.build()).queue();
                } else if (parts[1].equals("N")) {
                    request.remove(parts[2]);

                    EmbedBuilder eb = new EmbedBuilder()
                            .setColor(Color.RED)
                            .setThumbnail(u.getJDA().getSelfUser().getAvatarUrl())
                            .setTitle("CaptCommunity Server")
                            .setDescription("Antrag auf Rangerhöhung")
                            .addField("","Ihre Anfrage wurde abgelehnt!\n" +
                                    "Du bist dadurch nicht mehr in der Lage in nächster Zeit ein " +
                                    "neuen Antrag zu erstellen. Melde dich bei Fragen an die Moderation",false);
                    LocalDateTime time = LocalDateTime.now();
                    eb.setFooter(time.getDayOfMonth() + "." + time.getMonthValue() + "." + time.getYear() + " | "
                            + time.plusHours(2).getHour() + ":" + time.getMinute() + ":" + time.getSecond() + " | "
                            + "CaptCommunity Team");
                    m.getUser().openPrivateChannel().complete().sendMessage(eb.build()).queue();
                }
            }
        }
    }

    public EmbedBuilder createMessage(TextChannel channel) {

        final Emote bronze = channel.getGuild().getEmoteById("844342554745110528");
        final Emote silver = channel.getGuild().getEmoteById("844342560309903370");
        final Emote gold = channel.getGuild().getEmoteById("844342559723880458");
        final Emote platinum = channel.getGuild().getEmoteById("844342561073659934");

        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.WHITE);
        eb.setTitle("Rollenverteilung - Nutzerhilfe");
        eb.setDescription("Über den Bot ist jeder neue Nutzer des Servers in der lage sich die passende Rolle zu geben\n" +
                "Rollen mit Anfrage werden erst über Moderatoren geprüft bevor der Rang vergeben werden darf. Die Reaktion versendet den Antrag");
        eb.addField("📃 Bestätigung (zuvor notwendig)"
                ,"```diff\n- Du musst zuvor bestätigen das du die Regeln gelesen hast\n```",false);
        eb.addField("","Offene Rollen:",false);
        eb.addField(bronze.getAsMention() + "Guest,","Für jeden Nutzer der nur gelegentlich/einmalig auf dem" +
                "Discord Server ist. Keine Besonderen Rechte",false);
        eb.addField(silver.getAsMention() + "Member,","Für bekannte Mitspieler und Freunde die neuer sind",false);
        eb.addField("","Rollen mit Anfrage:",false);
        eb.addField(gold.getAsMention() + "Veteran,","Für Mitglieder die bereits lange Zeit zu dem Server gehören",false);
        eb.addField(platinum.getAsMention() + "Moderator,","Für eine Anfrage zum Moderator oder das hinzufügen von Accounts als Moderator",false);
        eb.setFooter("Das Missbrauchen der Funktion führt zu einem Ausschluss");
        return eb;
    }
}
