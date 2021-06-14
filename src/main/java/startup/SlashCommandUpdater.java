package startup;

import com.mysql.cj.log.Slf4JLogger;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

import static net.dv8tion.jda.api.interactions.commands.OptionType.INTEGER;
import static net.dv8tion.jda.api.interactions.commands.OptionType.STRING;
import static startup.DiscordBot.INSTANCE;

public class SlashCommandUpdater {

    public static void updateSlashCommands() {
        // These commands take up to an hour to be activated after creation/update/delete
        CommandListUpdateAction commands = INSTANCE.Manager.updateCommands();

        commands.addCommands(
                        new CommandData("test", "Der Bot wiederholt deine Worte")
                        .addOptions(new OptionData(STRING, "content", "Die zuwiederholenen Worte")
                                .setRequired(true)))

                .addCommands(new CommandData("balance", "Zeigt dein aktuelles Guthaben an"))

                .addCommands(new CommandData("play", "Spielt Track oder Playlist ab")
                        .addOptions(new OptionData(STRING, "url","YouTube URL").setRequired(true)))

                .addCommands(new CommandData("load", "Lädt angegebene Playlist")
                        .addOptions(new OptionData(STRING, "playlist","Deine Bot Playlist").setRequired(true)))

                .addCommands(new CommandData("save", "Speichert aktuellen Track in Playlist")
                        .addOptions(new OptionData(STRING,"playlist","In welche Playlist gespeichert wird").setRequired(true)))

                .addCommands(new CommandData("volume", "Verändert die Lautstärke")
                        .addOptions(new OptionData(INTEGER,"value","Lautstärke zwischen 0-100").setRequired(true)))

                .addCommands(new CommandData("remove", "Entfernt Track mit der angegebenen Nummer")
                        .addOptions(new OptionData(INTEGER,"trackNumber","Die Nummer des Tracks").setRequired(true)))

                .addCommands(new CommandData("start", "Startet Track mit der angegebenen Nummer")
                        .addOptions(new OptionData(INTEGER,"trackNumber","Die Nummer des Tracks").setRequired(true)))

                .queue();
        Slf4JLogger logger = new Slf4JLogger("SlashCommandUpdater.update");
        logger.logError("Sucessfully updated the slash commands");
    }
}
