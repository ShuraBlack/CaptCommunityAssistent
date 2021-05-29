package listener;

import com.mysql.cj.log.Slf4JLogger;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import static model.Logger.EntryLogger.*;
import static model.util.LoggerUtil.type.*;

public class LobbyListener extends ListenerAdapter {

    @Override
    public void onGuildMemberJoin(@Nonnull GuildMemberJoinEvent event) {
        if (!event.getGuild().getId().equals("286628427140825088")) {
            return;
        }

        Slf4JLogger logger = new Slf4JLogger("LobbyListener.Join");
        logger.logInfo(event.getMember().getEffectiveName() + " joined");
        logInfo(event.getMember().getUser()
                ,event.getMember().getAsMention() + ", willkommen auf dem Server!", ADD);
    }

    @Override
    public void onGuildMemberRemove(@Nonnull GuildMemberRemoveEvent event) {
        if (!event.getGuild().getId().equals("286628427140825088")) {
            return;
        }

        Slf4JLogger logger = new Slf4JLogger("LobbyListener.Remove");
        logger.logInfo(event.getMember().getEffectiveName() + " left");
        logInfo(event.getMember().getUser()
                ,event.getMember().getAsMention() + ", auf wiedersehen!", WARNING);
    }
}
