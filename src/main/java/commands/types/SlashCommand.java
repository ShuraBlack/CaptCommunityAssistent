package commands.types;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public interface SlashCommand {

    public void performSlashCommand(SlashCommandEvent event);
}
