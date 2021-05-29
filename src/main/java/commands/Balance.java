package commands;

import commands.types.ServerCommand;
import model.sql.LoadDriver;
import model.util.ChannelUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.concurrent.TimeUnit;

import static model.util.SQLUtil.SELECTGEMSOFMEMBER;
import static model.util.SQLUtil.UPDATEGEMSOFMEMBER;
import static model.util.ChannelUtil.*;

public class Balance implements ServerCommand {
    @Override
    public void performCommand(Member m, TextChannel channel, Message message) {
        message.delete().queue();
        String[] args = message.getContentDisplay().split(" ");

        if (message.getContentDisplay().equals("!balance")) {
            if (!channel.getId().equals(IDLEGAME)
                && !channel.getId().equals(BLACKJACK)
                && !channel.getId().equals(SLOTMACHINE)
                && !channel.getId().equals(PROULETTE)) {
                EmbedBuilder eb = new EmbedBuilder()
                        .setColor(Color.WHITE)
                        .setDescription(m.getAsMention() + ", du kannst diesen command nur in den game Channels nutzen");
                channel.sendMessage(eb.build()).complete().delete().queueAfter(5, TimeUnit.SECONDS);
                return;
            }

            LoadDriver ld = new LoadDriver();
            ResultSet rs = ld.executeSQL(SELECTGEMSOFMEMBER(m.getId()));
            try {
                if (rs.next()) {
                    NumberFormat numFormat = new DecimalFormat();
                    long rawBalance = rs.getLong(1);
                    String balance = numFormat.format(rawBalance);
                    String prefix = "+";
                    if (rawBalance < 0) {
                        prefix = "-";
                    }
                    EmbedBuilder eb = new EmbedBuilder()
                            .setColor(Color.WHITE)
                            .setAuthor(m.getEffectiveName(), m.getUser().getEffectiveAvatarUrl(), m.getUser().getEffectiveAvatarUrl())
                            .addField("Kontostand:", "```diff\n" + prefix + " " + balance + "\uD83D\uDC8E \n```", false);
                    channel.sendMessage(eb.build()).complete().delete().queueAfter(10, TimeUnit.SECONDS);
                } else {
                    EmbedBuilder eb = new EmbedBuilder()
                            .setColor(Color.WHITE)
                            .setDescription(m.getAsMention() + ", du besitzen noch kein Konto.\n" +
                                    "Besuchen sie " + channel.getGuild().getTextChannelById(IDLEGAME).getAsMention() + " und " +
                                    "erspielen sie regelmäßig \uD83D\uDC8E");
                    channel.sendMessage(eb.build()).complete().delete().queueAfter(10, TimeUnit.SECONDS);
                }
            } catch (SQLException throwables) {
                ld.close();
                throwables.printStackTrace();
            }
            ld.close();
        } else if (args.length == 3 & args[1].equals("add") && m.hasPermission(Permission.ADMINISTRATOR)) {
            LoadDriver ld = new LoadDriver();
            ResultSet rs = ld.executeSQL(SELECTGEMSOFMEMBER(m.getId()));
            try {
                rs.next();
                long currentBalance = rs.getLong(1);
                long addBalance = Long.parseLong(args[2]) * 1000000;
                ld.executeSQL(UPDATEGEMSOFMEMBER(m.getId(), currentBalance + addBalance));
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            ld.close();
        }
    }

    @Override
    public void reactionperform(Member m, TextChannel channel, String mesID, String emote, MessageReactionAddEvent event) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void privateperform(String command, User u) {
        throw new UnsupportedOperationException();
    }
}
