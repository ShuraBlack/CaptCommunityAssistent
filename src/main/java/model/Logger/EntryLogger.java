package model.Logger;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import static model.util.LoggerUtil.type.*;

import model.util.LoggerUtil;
import net.dv8tion.jda.api.entities.User;

import java.time.OffsetDateTime;

public class EntryLogger {

    public static void logInfo (User u, String description, LoggerUtil.type type) {
        WebhookClientBuilder builder = new WebhookClientBuilder("https://discord.com/api/webhooks/842016649417719848" +
                "/ODjtTJ3eO94isXEEV0Mm3EYqt5TA737EkuOXKN_1nYdwAo1cjVKI7r8BU7faeBkp-O1g");

        builder.setThreadFactory((job) -> {
            Thread thread = new Thread(job);
            thread.setName("EntryLogger-Thread");
            thread.setDaemon(true);
            return thread;
        });

        builder.setWait(true);
        WebhookClient client = builder.build();

        WebhookEmbedBuilder eb = new WebhookEmbedBuilder()
                .setAuthor(new WebhookEmbed.EmbedAuthor(u.getAsTag(), u.getEffectiveAvatarUrl(), null))
                .setDescription(description)
                .setTimestamp(OffsetDateTime.now());

        if (type.equals(ADD)) {
            eb.setColor(4437377);
        } else if (type.equals(EDIT)) {
            eb.setColor(3375061);
        } else if (type.equals(WARNING)) {
            eb.setColor(16729871);
        }
        client.send(eb.build());
        client.close();
    }

}
