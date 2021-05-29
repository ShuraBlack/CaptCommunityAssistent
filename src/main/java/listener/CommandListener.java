package listener;

import model.util.ChannelUtil;
import startup.DiscordBot;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.concurrent.TimeUnit;

/**
 * Checks for Commands and there Prefix
 */
public class CommandListener extends ListenerAdapter {

    /**
     * Overrride and JDA function
     * @param event action which got performed
     */
    @Override
    public void onMessageReceived (MessageReceivedEvent event) {
        if(event.getAuthor().equals(event.getJDA().getSelfUser())) {
            return;
        }

        if (!event.getGuild().getId().equals("286628427140825088")) {
            return;
        }
        String message = event.getMessage().getContentDisplay();

        if(event.isFromType(ChannelType.TEXT)) {
            String[] args = event.getMessage().getContentDisplay().split(" ");
            if(message.startsWith("!")) {
                /*------------------------------------------ Special Cases -------------------------------------------*/
                /*----------------------------------------------------------------------------------------------------*/
                if(!DiscordBot.INSTANCE.getCManager().perform(args[0], event.getMember(), event.getTextChannel(), event.getMessage())) {
                    //event.getTextChannel().deleteMessageById(event.getMessageId()).queue();
                    event.getTextChannel().sendMessage("Unbekannter Befehl!")
                            .complete().delete().queueAfter(5, TimeUnit.SECONDS);
                }
            } else if (event.getTextChannel().getId().equals(ChannelUtil.MUSIC)) {
                event.getMessage().delete().queue();
                MessageBuilder mes = new MessageBuilder().append("!player ").append(args[0]).append("XESCAPEX");
                DiscordBot.INSTANCE.getCManager().perform("!player", event.getMember(), event.getTextChannel(), mes.build());
            }
        } else {
            if(message.startsWith("!ruler")) {
                DiscordBot.INSTANCE.getCManager().privateperform(message,event.getAuthor());
            } else if (message.startsWith("!idle")) {
                DiscordBot.INSTANCE.getCManager().privateperform(message,event.getAuthor());
            } else if (message.startsWith("!promo")) {
                DiscordBot.INSTANCE.getCManager().privateperform(message, event.getAuthor());
            }
        }
    }
}
