package commands;

import Model.sql.LoadDriver;
import Model.sql.SQLRequests;
import commands.types.ServerCommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

public class Loadmember implements ServerCommand {
    @Override
    public void performCommand(Member m, TextChannel channel, Message message) {
        message.delete().queue();

        if(!m.hasPermission(channel, Permission.ADMINISTRATOR)) {
            return;
        }

        LoadDriver ld = new LoadDriver();
        for (Member mem : channel.getGuild().getMembers() ) {
            ld.executeSQL(SQLRequests.INSERTMEMBER(mem.getId(),mem.getEffectiveName()),SQLRequests.INSERTREQUESTTYPE);
        }
        ld.close();
    }

    @Override
    public void reactionperform(Member m, TextChannel channel, String mesID
            , String emote, MessageReactionAddEvent event) { throw new UnsupportedOperationException(); }

    @Override
    public void privateperform(String command, User u) { throw new UnsupportedOperationException(); }
}
