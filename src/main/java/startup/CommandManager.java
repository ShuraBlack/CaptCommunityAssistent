package startup;

import commands.*;
import commands.types.ServerCommand;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Controll of Commands which are Available
 */
public class CommandManager {

    public ConcurrentHashMap<String, ServerCommand> commands;

    public CommandManager() {
        // Create a HashMap
        this.commands = new ConcurrentHashMap<>();

        // Adds all Commands

        /*-- Managment Commands --------------------------------------------------------------------------------------*/
        // Clearing messages of a channel
        this.commands.put("!clear", new Clear());
        // Moves messages from one channel into another
        this.commands.put("!copy", new Copy());
        // Send a message with all user commands
        this.commands.put("!help", new Help());
        // Version of the Bot
        this.commands.put("!version", new Version());
        // Load all Member into MySQL DB
        this.commands.put("!loadmember", new Loadmember());
        /*------------------------------------------------------------------------------------------------------------*/

        /*-- Randomizer ----------------------------------------------------------------------------------------------*/
        // Flip a coin
        this.commands.put("!coinflip", new Coinflip());
        // Throw a dice
        this.commands.put("!dice", new Dice());
        // Throw dices
        this.commands.put("!mdice",new Mdice());
        /*------------------------------------------------------------------------------------------------------------*/

        /*-- Server Messages and User Assistent ----------------------------------------------------------------------*/
        // Manage Userhelp for getting roles
        this.commands.put("!ruler", new Ruler());
        // Message with Serverrules
        this.commands.put("!rules", new Rules());
        // Manage News and Partner channels abo
        this.commands.put("!news", new News());
        // Create Infoevent Message
        this.commands.put("!infoevent", new Infoevent());
        // Promotion for users
        this.commands.put("!promo", new Promo());
        // Create a message if you search user for a game
        this.commands.put("!search", new Search());
        /*------------------------------------------------------------------------------------------------------------*/

        /*-- Fun -----------------------------------------------------------------------------------------------------*/
        // Luxas special message
        this.commands.put("!kaetzchen", new Kaetzchen());
        // For Idle Discord game
        this.commands.put("!idle", new Idle());
        // RGB Owner Role
        this.commands.put("!rainbow", new Rainbow());
        /*------------------------------------------------------------------------------------------------------------*/

        /*-- Music ---------------------------------------------------------------------------------------------------*/
        // Control music player
        this.commands.put("!player", new Player());
        /*------------------------------------------------------------------------------------------------------------*/

        /*-- Playlist ------------------------------------------------------------------------------------------------*/
        // SQL function, create playlists and show them
        this.commands.put("!playlist", new Playlist());
        /*------------------------------------------------------------------------------------------------------------*/
    }

    /**
     * Checks if the Command is in the Map
     * @param command Prefix for activation
     * @param m Member which use it
     * @param channel in which it got called
     * @param message the entire Member Message
     * @return true if available, false if it isnt
     */
    public boolean perform(String command, Member m, TextChannel channel, Message message) {
        if (m.getUser().isBot()) {
            return false;
        }
        ServerCommand cmd;
        if((cmd = this.commands.get(command.toLowerCase())) != null) {
            cmd.performCommand(m, channel, message);
            return true;
        }
        return false;
    }

    public void reactperform(String command, Member m, TextChannel channel, String mesID, String emote, MessageReactionAddEvent event) {
        if (m.getUser().isBot()) {
            return;
        }
        ServerCommand cmd;
        if((cmd = this.commands.get(command.toLowerCase())) != null) {
            cmd.reactionperform(m,channel,mesID,emote,event);
            return;
        }
        return;
    }

    public boolean privateperform(String command, User u) {
        if (u.isBot()) {
            return false;
        }
        ServerCommand cmd;
        String[] args = command.split(" ");
        if((cmd = this.commands.get(args[0].toLowerCase())) != null) {
            cmd.privateperform(command,u);
            return true;
        }
        return false;
    }

}
