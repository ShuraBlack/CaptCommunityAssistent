package listener;

import model.web.Server;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.events.guild.GuildUnbanEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

import static model.Logger.EntryLogger.logInfo;
import static model.util.LoggerUtil.type.*;

public class ManagerListener extends ListenerAdapter {

    @Override
    public void onGuildBan (@Nonnull GuildBanEvent event) {
        if (!event.getGuild().getId().equals("286628427140825088")) {
            return;
        }

        logInfo(event.getUser(),event.getUser().getName() + ", wurde des Servers verwiesen!", WARNING);
    }

    @Override
    public void onGuildUnban (@Nonnull GuildUnbanEvent event) {
        if (!event.getGuild().getId().equals("286628427140825088")) {
            return;
        }

        logInfo(event.getUser(),event.getUser().getName() + ", wurde eine zweite Chance ermöglicht!", EDIT);
    }

    @Override
    public void onReady (ReadyEvent event) {
        new Server();
        System.out.println("JDA connected and ready for use!");
    }
}
