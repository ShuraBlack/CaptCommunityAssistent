package listener;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import startup.DiscordBot;

import java.util.ArrayList;
import java.util.List;

public class SlashListener extends ListenerAdapter {

    private final List<String> player = playerSlashCommands();

    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent event) {
        if (event.getGuild() == null) {
            return;
        }
        if (player.contains(event.getName())) {
            DiscordBot.INSTANCE.getCManager().performSlash("!player", event);
        } else {
            DiscordBot.INSTANCE.getCManager().performSlash("!" + event.getName(), event);
        }
    }

    @Override
    public void onButtonClick(ButtonClickEvent event)
    {
        // users can spoof this id so be careful what you do with this
        String[] id = event.getComponentId().split(":"); // this is the custom id we specified in our button
        String authorId = id[0];
        String type = id[1];
        // When storing state like this is it is highly recommended to do some kind of verification that it was generated by you, for instance a signature or local cache
        if (!authorId.equals(event.getUser().getId()))
            return;
        event.deferEdit().queue(); // acknowledge the button was clicked, otherwise the interaction will fail

        MessageChannel channel = event.getChannel();
        switch (type)
        {
            case "prune":
                int amount = Integer.parseInt(id[2]);
                event.getChannel().getIterableHistory()
                        .skipTo(event.getMessageIdLong())
                        .takeAsync(amount)
                        .thenAccept(channel::purgeMessages);
                // fallthrough delete the prompt message with our buttons
            case "delete":
                event.getHook().deleteOriginal().queue();
        }
    }

    public void say(SlashCommandEvent event, String content)
    {
        event.reply(content).setEphemeral(true).queue(); // This requires no permissions!
    }

    public List<String> playerSlashCommands () {
        List<String> player = new ArrayList<>();
        player.add("play");
        player.add("load");
        player.add("save");
        player.add("volume");
        player.add("remove");
        player.add("start");

        return player;
    }
}