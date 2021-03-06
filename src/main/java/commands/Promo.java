package commands;

import model.sql.LoadDriver;
import model.util.ChannelUtil;
import model.util.SQLUtil;
import commands.types.ServerCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

public class Promo implements ServerCommand {

    private String mesID = "821085661498703953";

    @Override
    public void performCommand(Member m, TextChannel channel, Message message) {
        String[] args = message.getContentDisplay().split(" ");
        message.delete().queue();

        if (!channel.getId().equals(ChannelUtil.PROMO)) {
            return;
        }

        if (args.length == 1) {
            if (!m.hasPermission(Permission.ADMINISTRATOR)) {
                return;
            }
            EmbedBuilder eb = createMessage(channel);
            if (mesID.equals("")) {
                mesID = channel.sendMessage(eb.build()).complete().getId();
            } else {
                channel.editMessageById(mesID,eb.build()).queue();
            }
        } else if (args.length == 3) {
            LoadDriver ld = new LoadDriver();
            ResultSet rs = ld.executeSQL(SQLUtil.SELECTOPENPROMO(m.getId()));
            try {
                if (rs.next()) {
                    EmbedBuilder ebdeny = new EmbedBuilder()
                            .setColor(Color.RED)
                            .setTitle("Anfrage auf Promotion ABGELEHNT")
                            .setDescription(m.getAsMention() + ", du hast bereits einen offenen/abgelehnten Antrag!");
                    channel.sendMessage(ebdeny.build()).complete().delete().queueAfter(10, TimeUnit.SECONDS);
                    return;
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            ld.executeSQL(SQLUtil.INSERTPROMO(args[1],args[2],m.getId()));
            EmbedBuilder eballow = new EmbedBuilder()
                    .setColor(Color.YELLOW)
                    .setTitle("Anfrage auf Promotion GESTELLT")
                    .setDescription(m.getAsMention() + ", dein Antrag wurde versendet und wird in k??rze verarbeitet!");
            channel.sendMessage(eballow.build()).complete().delete().queueAfter(10, TimeUnit.SECONDS);
            ResultSet rs2 = ld.executeSQL(SQLUtil.SELECTOPENPROMO(m.getId()));
            try {
                rs2.next();
                EmbedBuilder eb = new EmbedBuilder()
                        .setTitle("Antrag auf Promotion (Twitch)")
                        .setColor(Color.WHITE)
                        .setDescription("Name: " + args[2] + "\n" + "Link: " + args[1] + "\n" + "User: " + m.getAsMention() + "\n" + "ID: " + rs2.getInt(1))
                        .setFooter("Antworte mit !promo Y/N/P id -> P f??r PREMIUM");
                channel.getGuild().retrieveOwner().complete().getUser().openPrivateChannel().complete().sendMessage(eb.build()).queue();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

            ld.close();
        }
    }

    @Override
    public void reactionperform(Member m, TextChannel channel, String mesID
            , String emote, MessageReactionAddEvent event) { throw new UnsupportedOperationException(); }

    @Override
    public void privateperform(String command, User u) {
        if (!u.getId().equals("286628057551208450")) {
            return;
        }
        String[] args = command.split(" ");
        if (args.length == 3) {
            if (args[1].equals("Y")) {
                LoadDriver ld = new LoadDriver();
                ld.executeSQL(SQLUtil.UPDATEPROMOSTATUS(args[2]));
                ResultSet rs = ld.executeSQL(SQLUtil.SELECTIDTOPROMO(args[2]));
                try {
                    rs.next();
                    EmbedBuilder eb = new EmbedBuilder()
                            .setColor(Color.GREEN)
                            .setTitle("Anfrage auf Promotion")
                            .setThumbnail(u.getJDA().getSelfUser().getAvatarUrl())
                            .setDescription("Hallo mein freundliches CaptCommunity Mitglied.\nIch freue" +
                                    " mich dir mitteilen zu d??rfen das dein Antrag angenommen worden ist!")
                            .addField("Name: " + rs.getString(2),"Link: " + rs.getString(1),false)
                            .setFooter("CaptCommunity Team");
                    sendPrivateMessage(u.getJDA().retrieveUserById(Long.parseLong(rs.getString(3))).complete(),eb);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                EmbedBuilder eb = new EmbedBuilder().setDescription("Vergiss bitte nicht \"!promo\" anzuwenden!");
                u.openPrivateChannel().complete().sendMessage(eb.build()).queue();
                ld.close();
            } else if (args[1].equals("N")) {
                LoadDriver ld = new LoadDriver();
                ResultSet rs = ld.executeSQL(SQLUtil.SELECTIDTOPROMO(args[2]));
                try {
                    rs.next();
                    EmbedBuilder eb = new EmbedBuilder()
                            .setColor(Color.RED)
                            .setTitle("Anfrage auf Promotion")
                            .setThumbnail(u.getJDA().getSelfUser().getAvatarUrl())
                            .setDescription("Hallo mein freundliches CaptCommunity Mitglied.\nEs tut mir leid" +
                                    " dir mitteilen zu m??ssen das deine Promotion abgelehnt wurde.\n Bei Fragen, melde dich bitte" +
                                    " bei dem Leiter des Servers, mit deiner eindeutigen Fall ID.")
                            .addField("Name: " + rs.getString(2),"Link: " + rs.getString(1) + "\nID: " + args[2],false)
                            .setFooter("CaptCommunity Team");
                    sendPrivateMessage(u.getJDA().retrieveUserById(Long.parseLong(rs.getString(3))).complete(),eb);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                ld.close();
            } else if (args[1].equals("P")) {
                LoadDriver ld = new LoadDriver();
                ld.executeSQL(SQLUtil.UPDATEPROMOABO(args[2]));
                ResultSet rs = ld.executeSQL(SQLUtil.SELECTIDTOPROMO(args[2]));
                try {
                    rs.next();
                    EmbedBuilder eb = new EmbedBuilder()
                            .setColor(Color.ORANGE)
                            .setThumbnail(u.getJDA().getSelfUser().getAvatarUrl())
                            .setTitle("PREMIUM Promotion")
                            .setDescription("Hallo mein freundliches CaptCommunity Mitglied.\n" +
                                    "Ich freue mich dir mitteilen zu d??rfen, das du nun eine *PREMIUM* Promotion auf dem Server hast! " +
                                    "Bei w??nschen oder Ideen kannst du gerne " + u.getJDA().retrieveUserById("286628057551208450").complete().getAsMention() +
                                    " kontaktieren, um alles weitere zu besprechen.")
                            .addField("Name: " + rs.getString(2),"Link: " + rs.getString(1),false)
                            .setFooter("CaptCommunity Team");
                    sendPrivateMessage(u.getJDA().retrieveUserById(Long.parseLong(rs.getString(3))).complete(),eb);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                EmbedBuilder eb = new EmbedBuilder().setDescription("Vergiss bitte nicht \"!promo\" anzuwenden!");
                u.openPrivateChannel().complete().sendMessage(eb.build()).queue();
                ld.close();
            }
        }
    }

    public EmbedBuilder createMessage (TextChannel channel) {
        EmbedBuilder eb = new EmbedBuilder()
                .setTitle("Partner und Promotion:")
                .setColor(Color.WHITE)
                .setThumbnail("https://s20.directupload.net/images/210317/bdx93la7.png")
                .setDescription("Hier werden alle Twitch Partner sowie Unterst??tzer des Discord angezeigt.\n" +
                        "Du m??chtest auch mit dazu geh??ren? F??r eine Partnerschaft nutze ```cs\n!promo [\"link\"] [\"name\"]\n```" +
                        "um eine Anfrage zu erstellen. Wenn du den Discord boostest kannst du ebenfalls eine Promotion erstellen, wenn der wunsch besteht.")
                .setFooter("CaptCommunity Team");
        LoadDriver ld = new LoadDriver();
        ResultSet rs = ld.executeSQL(SQLUtil.SELECTPROMO());
        try {
            while (rs.next()) {
                String version = "";
                if (rs.getString(4).equals("P")) {
                    version = "- _PREMIUM_";
                }
                eb.addField(rs.getString(2) + " " + version
                        , channel.getGuild().retrieveMemberById(rs.getString(3)).complete().getAsMention() + "\n" + rs.getString(1), false);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        ld.close();
        return eb;
    }

    public void sendPrivateMessage(User user, EmbedBuilder content) {
        user.openPrivateChannel().queue((channel) ->
        {
            channel.sendMessage(content.build()).queue();
        });
    }

    @Override
    public void performSlashCommand(SlashCommandEvent event) {

    }
}
