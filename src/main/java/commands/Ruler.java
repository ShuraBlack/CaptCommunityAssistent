package commands;

import commands.types.ServerCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.*;

public class Ruler implements ServerCommand {

    private Set<String> ruleread = new TreeSet<>();
    private Map<String,String> request = new TreeMap<>();
    private Member[] memberlist = new Member[10];
    String mesID = "804077314023227392";

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
            String[] reactionname = {"\uD83D\uDCD3","\uD83D\uDCD2","\uD83D\uDCD9","\uD83D\uDCD7","\uD83D\uDCD6"};

            EmbedBuilder eb = createMessage();
            //mesID = channel.sendMessage(eb.build()).complete().getId();
            for (String s : reactionname) {
                channel.editMessageById(mesID,eb.build()).complete().addReaction(s).queue();
            }
        }
    }

    @Override
    public void reactionperform(Member m, TextChannel channel, String mesID, String emote, MessageReactionAddEvent event) {

        // Grey,Yellow,Orange,Green
        String[] reactionname = {"\uD83D\uDCD3","\uD83D\uDCD2","\uD83D\uDCD9","\uD83D\uDCD7"};
        Role guest = channel.getGuild().getRoleById("384133791595102218");
        Role member = channel.getGuild().getRoleById("286631357772201994");

        event.getReaction().removeReaction(m.getUser()).queue();

        if (emote.equals("\uD83D\uDCD6")) {
            if (!ruleread.contains(m.getId())) {
                ruleread.add(m.getId());
                EmbedBuilder eb = createMessage();
                eb.addField("","",false);
                eb.addField("Letzter Status:",m.getEffectiveName() + " hat die Serverregeln akzeptiert!",false);
                channel.editMessageById(mesID,eb.build()).queue();
                System.out.println(m.getEffectiveName() + " accepted the Server rules!");
            }
            return;
        }
        if (!ruleread.contains(m.getId())) {
            return;
        }
        if (emote.equals(reactionname[0])) {
            event.getGuild().addRoleToMember(m,guest).complete();
            event.getGuild().removeRoleFromMember(m,member).complete();

            EmbedBuilder eb = createMessage();
            eb.addField("","",false);
            eb.addField("Letzter Status:",m.getEffectiveName() + " wurde zum Guest ernannt!",false);
            channel.editMessageById(mesID,eb.build()).queue();

        } else if (emote.equals(reactionname[1])) {
            event.getGuild().addRoleToMember(m,member).complete();
            event.getGuild().removeRoleFromMember(m,guest).complete();

            EmbedBuilder eb = createMessage();
            eb.addField("","",false);
            eb.addField("Letzter Status:",m.getEffectiveName() + " wurde zum Guest ernannt!",false);
            channel.editMessageById(mesID,eb.build()).queue();

        } else if (emote.equals(reactionname[2])) {
            if (request.containsKey(m.getId())) {
                return;
            }
            User creator = m.getJDA().retrieveUserById("286628057551208450").complete();
            LocalDateTime time = LocalDateTime.now();
            EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(Color.WHITE);
            eb.setTitle("Ranganfrage auf > CaptCommunity <");
            eb.addField("Member: " + m.getEffectiveName(),
                    "\nID: " + m.getId() + "\nRang: " + "Veteran " + reactionname[3] +
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

            EmbedBuilder ebx = createMessage();
            ebx.addField("","",false);
            ebx.addField("Letzter Status:",m.getEffectiveName() + " hat eine Anfrage für Veteran abgegeben!",false);
            channel.editMessageById(mesID,ebx.build()).queue();
        } else if (emote.equals(reactionname[3])) {
            if (request.containsKey(m.getId())) {
                return;
            }
            User creator = m.getJDA().retrieveUserById("286628057551208450").complete();
            LocalDateTime time = LocalDateTime.now();
            EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(Color.WHITE);
            eb.setTitle("Ranganfrage auf > CaptCommunity <");
            eb.addField("Member: " + m.getEffectiveName(),
                    "\nID: " + m.getId() + "\nRang: " + "Moderator " + reactionname[3] +
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

            EmbedBuilder ebx = createMessage();
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

    public EmbedBuilder createMessage() {
        // Grey,Yellow,Orange,Green
        String[] reactionname = {"\uD83D\uDCD3","\uD83D\uDCD2","\uD83D\uDCD9","\uD83D\uDCD7"};

        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.WHITE);
        eb.setTitle("Rollenverteilung - Nutzerhilfe");
        eb.setDescription("Über den Bot ist jeder neue Nutzer des Servers in der lage sich die passende Rolle zu geben");
        eb.addField("Bitte bestätige zuvor mit " + "\uD83D\uDCD6" + "das du die Server-Regeln gelesen hast!",
                "Rollen mit Anfrage werden erst über Moderatoren geprüft bevor der Rang vergeben werden darf. Die Reaktion versendet den Antrag",false);
        eb.addField("","Offene Rollen:",false);
        eb.addField(reactionname[0] + "Guest,","Für jeden Nutzer der nur gelegentlich/einmalig auf dem" +
                "Discord Server ist. Keine Besonderen Rechte",false);
        eb.addField(reactionname[1] + "Member,","Für bekannte Mitspieler und Freunde die neuer sind",false);
        eb.addField("","Rollen mit Anfrage:",false);
        eb.addField(reactionname[2] + "Veteran,","Für Mitglieder die bereits lange Zeit zu dem Server gehören",false);
        eb.addField(reactionname[3] + "Moderator,","Für eine Anfrage zum Moderator oder das hinzufügen von Accounts als Moderator",false);
        eb.setFooter("Das Missbrauchen der Funktion führt zu einem Ausschluss");
        return eb;
    }
}
