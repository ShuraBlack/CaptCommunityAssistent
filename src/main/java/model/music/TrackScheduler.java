package model.music;

import com.sedmelluq.discord.lavaplayer.track.*;
import model.util.ChannelUtil;
import startup.DiscordBot;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TrackScheduler extends AudioEventAdapter {

    private final AudioPlayer player;
    private final BlockingQueue<AudioTrack> queue;
    private final String currentTrackId = "834810455431839774";
    private final String queueId = "834810456370708520";
    private AudioTrack audioTrack = null;

    public TrackScheduler(AudioPlayer player) {
        this.player = player;
        this.queue = new LinkedBlockingQueue<>();
    }

    public void queue(AudioTrack track) {
        if (!player.startTrack(track, true)) {
            queue.offer(track);
        }
    }

    public void nextTrack() {
        if (queue.size() == 0 && audioTrack == null) {
            clearMessages();
        }
        if (audioTrack != null) {
            player.startTrack(audioTrack.makeClone(),false);
        } else {
            player.startTrack(queue.poll(), false);
            editQueueMessage();
        }
    }

    public void randomizeQueue() {
        List<AudioTrack> list = new ArrayList<>(queue);
        Collections.shuffle(list);
        queue.clear();
        queue.addAll(list);
        editQueueMessage();
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        AudioTrackInfo info = track.getInfo();
        long sec = info.length / 1000L;
        long min = sec / 60L;
        long hour = min / 60L;
        sec %= 60L;
        min %= 60L;
        hour %= 60L;
        String url = info.uri;
        EmbedBuilder eb = (new EmbedBuilder()).setColor(Color.WHITE).addField(info.author, "["
                + info.title + "](" + url + ")", false)
                .addField("Dauer: ", info.isStream ? "\uD83D\uDD34 Stream" : (hour > 0L ? hour + "h " : "")
                        + (min < 10 ? "0" + min : min) + "m "
                        + (sec < 10 ? "0" + sec : sec) + "s", true);
        if (url.startsWith("https://www.youtube.com/watch?v=")) {
            String videoID = url.replace("https://www.youtube.com/watch?v=", "");
            eb.setImage("https://img.youtube.com/vi/" + videoID + "/hqdefault.jpg");
            DiscordBot.INSTANCE.Manager.getGuildById("286628427140825088").getTextChannelById(ChannelUtil.MUSIC)
                    .editMessageById(currentTrackId,eb.build()).queue();
        } else {
            DiscordBot.INSTANCE.Manager.getGuildById("286628427140825088").getTextChannelById(ChannelUtil.MUSIC)
                    .editMessageById(currentTrackId,eb.build()).queue();
        }
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (endReason.mayStartNext) {
            nextTrack();
        }
    }

    public void editQueueMessage () {
        TextChannel musicChannel = DiscordBot.INSTANCE.Manager.getGuildById("286628427140825088")
                .getTextChannelById(ChannelUtil.MUSIC);

        EmbedBuilder eb = new EmbedBuilder();
        StringBuilder s = new StringBuilder();
        int count = 1;
        for (AudioTrack at : queue) {
            String name = at.getInfo().title;
            if ((s.toString() + name).length() > 2043) {
                s.append("...");
                break;
            }
            s.append(count + ". " + at.getInfo().title).append("\n");
            count++;
        }
        long sec = queue.stream().mapToLong(audioTrack -> audioTrack.getInfo().length).sum() / 1000L;
        long min = sec / 60L;
        long hour = min / 60L;
        sec %= 60L;
        min %= 60L;
        hour %= 60L;

        eb.setTitle("Warteschlange").setFooter("Lieder: " + queue.size() + " - Gesamtdauer: " + (hour > 0L ? hour + "h " : "")
                + (min < 10 ? "0" + min : min) + "m "
                + (sec < 10 ? "0" + sec : sec) + "s");
        eb.setDescription(s.toString());
        musicChannel.editMessageById(queueId, eb.build()).queue();
    }

    public void clearQueue () {
        queue.clear();
    }

    public void clearMessages () {
        TextChannel musicChannel = DiscordBot.INSTANCE.Manager.getGuildById("286628427140825088")
                .getTextChannelById(ChannelUtil.MUSIC);
        EmbedBuilder current = new EmbedBuilder()
                .setTitle("Aktuelles Lied")
                .addField("Author","Titel",false)
                .addField("Dauer:","00h 00m 00s",false)
                .setImage("https://s8.directupload.net/images/210422/a5ipoif9.png");
        EmbedBuilder queue = new EmbedBuilder()
                .setTitle("Warteschlange:");

        musicChannel.editMessageById(currentTrackId, current.build()).queue();
        musicChannel.editMessageById(queueId, queue.build()).queue();
    }

    public AudioTrack getAudioTrack() {
        return audioTrack;
    }

    public void setAudioTrack(AudioTrack audioTrack) {
        this.audioTrack = audioTrack;
    }

}

