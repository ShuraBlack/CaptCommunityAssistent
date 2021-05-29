package listener;

import model.sql.LoadDriver;
import model.util.ChannelUtil;
import model.util.SQLUtil;
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
        if (!event.getGuild().getId().equals("286628427140825088")) {
            return;
        }

        LoadDriver ld = new LoadDriver();
        ResultSet rs = ld.executeSQL(SQLUtil.SELECTIDTOCOMMAND(event.getMessageId()));
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
                if (event.getMessageId().equals("804077314023227392")
                        && event.getTextChannel().getId().equals(ChannelUtil.ROLES)) {
                    DiscordBot.INSTANCE.getCManager()
                            .reactperform("!ruler", event.getMember(), event.getTextChannel(), event.getMessageId()
                                    , event.getReactionEmote().getName(), event);
                } else if (event.getMessageId().equals("844332779176984586")
                    && event.getTextChannel().getId().equals(ChannelUtil.SUBS)) {
                    DiscordBot.INSTANCE.getCManager()
                            .reactperform("!news", event.getMember(), event.getTextChannel(), event.getMessageId()
                                    , event.getReactionEmote().getName(), event);
                } else if (event.getTextChannel().getId().equals(ChannelUtil.IDLEGAME)) {
                    DiscordBot.INSTANCE.getCManager()
                        .reactperform("!idle", event.getMember(), event.getTextChannel(), event.getMessageId()
                                , event.getReactionEmote().getName(), event);
                } else if (event.getTextChannel().getId().equals(ChannelUtil.BLACKJACK)) {
                    DiscordBot.INSTANCE.getCManager()
                            .reactperform("!blackjack", event.getMember(), event.getTextChannel(), event.getMessageId()
                                    , event.getReactionEmote().getName(), event);
                } else if (event.getTextChannel().getId().equals(ChannelUtil.MUSIC)) {
                    DiscordBot.INSTANCE.getCManager()
                        .reactperform("!player", event.getMember(), event.getTextChannel(), event.getMessageId()
                                , event.getReactionEmote().getName(), event);
                } else if (event.getTextChannel().getId().equals(ChannelUtil.SLOTMACHINE)) {
                    DiscordBot.INSTANCE.getCManager()
                            .reactperform("!sm", event.getMember(), event.getTextChannel(), event.getMessageId()
                                    , event.getReactionEmote().getName(), event);
                }
        }
    }
}
