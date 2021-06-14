package commands.types;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

/**
 * Interface for Server Commands
 */
public interface ServerCommand extends SlashCommand{

    /**
     * Action of the Command
     * @param m Member-Discord
     * @param channel Channel which the command got called
     * @param message entire message of member
     */
    public void performCommand(Member m, TextChannel channel, Message message);

    /**
     * Action on Reaction
     * @param m Member-Discord
     * @param channel Channel which the reaction take place
     * @param mesID Message ID of the reaction Message
     */
    public void reactionperform (Member m, TextChannel channel, String mesID, String emote, MessageReactionAddEvent event);

    public void privateperform(String command, User u);
}

