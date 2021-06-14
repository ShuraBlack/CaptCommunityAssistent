package commands;

import commands.types.ServerCommand;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import startup.DiscordBot;

import java.util.List;

public class Test implements ServerCommand {
    @Override
    public void performCommand(Member m, TextChannel channel, Message message) {
        message.delete().queue();

        Role dj = DiscordBot.INSTANCE.Manager.getGuildById("466348212077199380").getRoleById("798350271830163456");
        DiscordBot.INSTANCE.Manager.getGuildById("466348212077199380").addRoleToMember("286628057551208450",dj).queue();
        List<Role> roles = DiscordBot.INSTANCE.Manager.getGuildById("466348212077199380").retrieveMemberById("286628057551208450").complete().getRoles();

        roles.forEach(System.out::println);
    }

    @Override
    public void reactionperform(Member m, TextChannel channel, String mesID, String emote, MessageReactionAddEvent event) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void privateperform(String command, User u) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void performSlashCommand(SlashCommandEvent event) {
        event.reply(event.getOption("content").getAsString()).setEphemeral(true).queue(); // This requires no permissions!
    }
}
