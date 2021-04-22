package listener;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import javax.annotation.Nonnull;

public class LobbyListener extends ListenerAdapter {

    @Override
    public void onGuildMemberJoin(@Nonnull GuildMemberJoinEvent event) {
        System.out.println(event.getMember() + " joined!");
        TextChannel lobby = event.getGuild().getTextChannelById("799449977394167859");
        assert lobby != null;
        lobby.sendMessage("✅" + event.getMember().getAsMention() + " (" + event.getMember().getUser().getAsTag() + "), willkommen auf dem Server!").queue();
    }

    @Override
    public void onGuildMemberRemove(@Nonnull GuildMemberRemoveEvent event) {
        System.out.println(event.getMember() + " left!");
        TextChannel lobby = event.getGuild().getTextChannelById("799449977394167859");
        assert lobby != null;
        lobby.sendMessage("❌" + event.getMember().getAsMention() + " (" + event.getMember().getUser().getAsTag() + "), auf wiedersehen!").queue();
    }
}
