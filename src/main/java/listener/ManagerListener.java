package listener;

import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.events.guild.GuildUnbanEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

public class ManagerListener extends ListenerAdapter {

    private String lobbyid = "799449977394167859";

    @Override
    public void onGuildBan (@Nonnull GuildBanEvent event) {
        event.getGuild().getTextChannelById(lobbyid)
                .sendMessage("\uD83D\uDD28 " + event.getUser().getAsMention() + " (" + event.getUser().getAsTag() +
                        "), wurde des Servers verwiesen!").queue();
    }

    @Override
    public void onGuildUnban (@Nonnull GuildUnbanEvent event) {

        event.getGuild().getTextChannelById(lobbyid)
                .sendMessage("\uD83E\uDE79 " + event.getUser().getAsMention() + " (" + event.getUser().getAsTag() +
                        "), wurde eine zweite Chance ermöglicht!").queue();
    }

    @Override
    public void onReady (ReadyEvent event) {
        System.out.println("JDA connected and ready for usage!");
    }
}
