package listener;

import model.sql.LoadDriver;
import model.sql.SQLUtil;
import startup.DiscordBot;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ReactionListener extends ListenerAdapter {

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        if(event.getMember().getUser().equals(event.getJDA().getSelfUser())) {
            return;
        }

        LoadDriver ld = new LoadDriver();
        ResultSet rs = ld.executeSQL(SQLUtil.SELECTIDTOCOMMAND(event.getMessageId()), SQLUtil.SELECTREQUESTTYPE);
        String command = null;
        try {
            if (rs.next()) {
                command = rs.getString(1);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        ld.close();
        if (command != null) {
            DiscordBot.INSTANCE.getCManager()
                    .reactperform(command, event.getMember(), event.getTextChannel(), event.getMessageId()
                            , event.getReactionEmote().getName(), event);
        } else {
            if (event.getTextChannel().getId().equals("799449909090713631") || event.getTextChannel().getId().equals("799449315793174598")) {// roles
                if (event.getMessageId().equals("804077314023227392")) {
                    DiscordBot.INSTANCE.getCManager()
                            .reactperform("!ruler", event.getMember(), event.getTextChannel(), event.getMessageId()
                                    , event.getReactionEmote().getName(), event);
                } else if (event.getMessageId().equals("815242575644196865")) {
                    DiscordBot.INSTANCE.getCManager()
                            .reactperform("!news", event.getMember(), event.getTextChannel(), event.getMessageId()
                                    , event.getReactionEmote().getName(), event);
                }
            }/* else if (event.getTextChannel().getId().equals("804762124349997078")) {
                DiscordBot.INSTANCE.getCManager()
                        .reactperform("!idle", event.getMember(), event.getTextChannel(), event.getMessageId()
                                , event.getReactionEmote().getName(), event);
            } else if (event.getTextChannel().getId().equals("818607332555489340")) {
                DiscordBot.INSTANCE.getCManager()
                        .reactperform("!dice", event.getMember(), event.getTextChannel(), event.getMessageId()
                                , event.getReactionEmote().getName(), event);
            }*/
            else if (event.getTextChannel().getId().equals("804125567388483624")) {
                DiscordBot.INSTANCE.getCManager()
                        .reactperform("!player", event.getMember(), event.getTextChannel(), event.getMessageId()
                                , event.getReactionEmote().getName(), event);
            }
        }
    }
}
