package commands;

import startup.DiscordBot;
import startup.MusicManager;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import commands.types.ServerCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.managers.AudioManager;

import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class Play implements ServerCommand {

    @Override
    public void performCommand(Member m, TextChannel channel, Message message) {
        if (message.getContentRaw().endsWith("XESCAPEX")) {
            String fackMessage = message.getContentRaw().replace("XESCAPE","");
            message = new MessageBuilder().append(fackMessage).build();
        } else {
            message.delete().queue();
        }
        String[] args = message.getContentRaw().split(" ");

        MusicManager musicManager = DiscordBot.INSTANCE.getAudioPlayer();
        if (!channel.getId().equals("804125567388483624")) {
            EmbedBuilder eb = new EmbedBuilder()
                    .setColor(Color.WHITE)
                    .setDescription("Füge Lieder im " + channel.getGuild().getTextChannelById("804125567388483624")
                            .getAsMention() + " TextChannel hinzu");
            channel.sendMessage(eb.build()).complete().delete().queueAfter(5, TimeUnit.SECONDS);
            return;
        }

        if (args.length == 1) {
            if (!m.hasPermission(channel, Permission.ADMINISTRATOR)) {
                return;
            }
            createHelpMessage(channel);
            createTemplateMessage(channel);
            createQueueMessage(channel);
            //addReactions(channel);
        } else if (args.length == 2 && args[1].equals("next")) {
            skipTrack();
        } else if (args.length == 2 && args[1].equals("stop")) {
            musicManager.player.stopTrack();
            musicManager.scheduler.clearQueue();
            musicManager.scheduler.clearMessages();
            channel.getGuild().getAudioManager().closeAudioConnection();
        } else if (args.length == 2 && args[1].equals("pause")) {
            musicManager.player.setPaused(true);
        } else if (args.length == 2 && args[1].equals("resume")) {
            musicManager.player.setPaused(false);
        } else if (args.length == 3 && args[1].equals("volume")) {
            int volume = Integer.parseInt(args[2]);
            if (volume > 100 || volume < 0) {
                return;
            }
            musicManager.player.setVolume(Integer.parseInt(args[2]));
        } else {
            DiscordBot.INSTANCE.getPlayerManager().loadItemOrdered(musicManager, args[1], new AudioLoadResultHandler() {
                @Override
                public void trackLoaded(AudioTrack track) {
                    EmbedBuilder eb = new EmbedBuilder().setDescription("Hinzugefügt zur Warteschlange " + track.getInfo().title);
                    channel.sendMessage(eb.build()).complete().delete().queueAfter(7,TimeUnit.SECONDS);

                    if(!play(channel.getGuild(), musicManager, track, m, channel)) return;
                    musicManager.scheduler.editQueueMessage();
                }

                @Override
                public void playlistLoaded(AudioPlaylist playlist) {
                    for (AudioTrack at : playlist.getTracks()) {
                        if(!play(channel.getGuild(), musicManager, at, m, channel)) return;
                    }
                    musicManager.scheduler.editQueueMessage();

                    EmbedBuilder eb = new EmbedBuilder().setDescription("Hinzugefügt zur Warteschlange " + playlist.getName());
                    channel.sendMessage(eb.build()).complete().delete().queueAfter(7,TimeUnit.SECONDS);
                }

                @Override
                public void noMatches() {
                    EmbedBuilder eb = new EmbedBuilder().setDescription("Kein Ergebnis für " + args[1]);
                    channel.sendMessage(eb.build()).complete().delete().queueAfter(7,TimeUnit.SECONDS);
                }

                @Override
                public void loadFailed(FriendlyException e) {
                    EmbedBuilder eb = new EmbedBuilder().setDescription("Konnte " + args[1] + " nicht abspielen");
                    channel.sendMessage(eb.build()).complete().delete().queueAfter(7,TimeUnit.SECONDS);
                }
            });
        }

    }

    @Override
    public void reactionperform(Member m, TextChannel channel, String mesID, String emote, MessageReactionAddEvent event) {

        event.getReaction().removeReaction(m.getUser()).queue();

        if (!mesID.equals("834810455431839774")) {
            return;
        }

        MusicManager musicManager = DiscordBot.INSTANCE.getAudioPlayer();
        if (!m.getGuild().getAudioManager().isConnected()) {
            return;
        } else {
            VoiceChannel vc = m.getVoiceState().getChannel();
            if (vc != null) {
                if (vc.getMembers().stream().map(ISnowflake::getId)
                        .anyMatch(id -> id.equals(DiscordBot.INSTANCE.Manager.getSelfUser().getId()))) {
                } else {
                    EmbedBuilder eb = new EmbedBuilder().setDescription("Verbinde dich zuvor mit dem aktiven VoiceChannel");
                    channel.sendMessage(eb.build()).complete().delete().queueAfter(7, TimeUnit.SECONDS);
                    return;
                }
            } else {
                EmbedBuilder eb = new EmbedBuilder().setDescription("Verbinde dich zuvor mit dem aktiven VoiceChannel");
                channel.sendMessage(eb.build()).complete().delete().queueAfter(7, TimeUnit.SECONDS);
                return;
            }
        }

        switch (emote) {
            case "⏹": // Stop
                musicManager.player.stopTrack();
                musicManager.scheduler.clearQueue();
                musicManager.scheduler.clearMessages();
                channel.getGuild().getAudioManager().closeAudioConnection();
                break;
            case "⏯": // Resume/Pause
                if (musicManager.player.isPaused()) {
                    musicManager.player.setPaused(false);
                } else {
                    musicManager.player.setPaused(true);
                }
                break;
            case "⏩": // Next
                skipTrack();
                break;
            case "\uD83D\uDD00": //Randomize
                musicManager.scheduler.randomizeQueue();
                break;
            case "\uD83D\uDD3B": // Minus
                int volumeDown = musicManager.player.getVolume() - 10;
                if (volumeDown >= 0) {
                    musicManager.player.setVolume(volumeDown);
                }
                break;
            case "\uD83D\uDD3A": // Plus
                int volumeUp = musicManager.player.getVolume() + 10;
                if (volumeUp <= 100) {
                    musicManager.player.setVolume(volumeUp);
                }
                break;
        }
    }

    @Override
    public void privateperform(String command, User u) {
        throw new UnsupportedOperationException();
    }

    private Boolean play(Guild guild, MusicManager musicManager, AudioTrack track, Member m, TextChannel channel) {
        if (connectToVoiceChannel(guild.getAudioManager(), m, channel)) {
            musicManager.scheduler.queue(track);
            return true;
        }
        return false;
    }

    private void skipTrack() {
        MusicManager musicManager = DiscordBot.INSTANCE.getAudioPlayer();
        musicManager.scheduler.nextTrack();
    }

    private static Boolean connectToVoiceChannel(AudioManager audioManager, Member m, TextChannel channel) {
        if (!audioManager.isConnected()) {
            VoiceChannel vc = m.getVoiceState().getChannel();
            if (vc != null) {
                    final int period = 60000*10; // repeat every 60*10 sec.
                    Timer timer = new Timer();
                    timer.scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {
                            if (audioManager.isConnected() && vc.getMembers().size() == 1) {
                                MusicManager musicManager = DiscordBot.INSTANCE.getAudioPlayer();
                                musicManager.player.stopTrack();
                                musicManager.scheduler.clearQueue();
                                musicManager.scheduler.clearMessages();
                                channel.getGuild().getAudioManager().closeAudioConnection();
                                this.cancel();
                            }
                        }
                    }, 100, period);
                audioManager.openAudioConnection(vc);
                return true;
            } else {
                EmbedBuilder eb = new EmbedBuilder().setDescription("Verbinde dich zuvor mit einem VoiceChannel");
                channel.sendMessage(eb.build()).complete().delete().queueAfter(7, TimeUnit.SECONDS);
                return false;
            }
        } else {
            VoiceChannel vc = m.getVoiceState().getChannel();
            if (vc != null) {
                if (!vc.getMembers().stream().map(ISnowflake::getId)
                        .anyMatch(id -> id.equals(DiscordBot.INSTANCE.Manager.getSelfUser().getId()))) {
                    EmbedBuilder eb = new EmbedBuilder().setDescription("Verbinde dich zuvor mit dem aktiven VoiceChannel");
                    channel.sendMessage(eb.build()).complete().delete().queueAfter(7, TimeUnit.SECONDS);
                    return false;
                }
                return true;
            } else {
                EmbedBuilder eb = new EmbedBuilder().setDescription("Verbinde dich zuvor mit dem aktiven VoiceChannel");
                channel.sendMessage(eb.build()).complete().delete().queueAfter(7, TimeUnit.SECONDS);
                return false;
            }
        }
    }

    private void createHelpMessage (TextChannel channel) {
        EmbedBuilder eb = new EmbedBuilder()
                .setThumbnail("https://s20.directupload.net/images/210422/zpyz5gkv.png")
                .setTitle("CaptCommunity MusicPlayer")
                .setDescription("Eine Anleitung um den MusicPlayer über Befehle zu steuern")
                .addField("__Befehle:__", "\n" +
                        "**!player [url]** ```cs\n" +
                        "fügt ein Lied hinzu\n" +
                        "```\n" +
                        "**!player stop**```cs\n" +
                        "beendet den player\n" +
                        "```\n" +
                        "**!player pause**```cs\n" +
                        "pausiert das aktuelle Lied\n" +
                        "```\n" +
                        "**!player resume**```cs\n" +
                        "führt das aktuelle Lied fort\n" +
                        "```\n" +
                        "**!player volume [0-100]**```cs\n" +
                        "verändert die Lautstärke\n" +
                        "```\n",false)
                .setFooter("Made by ShuraBlack - Head of Server");
        channel.sendMessage(eb.build()).queue();
    }

    private void createTemplateMessage (TextChannel channel) {
        EmbedBuilder eb = new EmbedBuilder()
                .setTitle("CaptCom - MusicPlayer")
                .addField("Author","Titel",false)
                .addField("Dauer:","00h 00m 00s",false);
        channel.sendMessage(eb.build()).queue();
    }

    private void createQueueMessage (TextChannel channel) {
        EmbedBuilder eb = new EmbedBuilder()
                .setTitle("Warteschlange:");
        channel.sendMessage(eb.build()).queue();
    }

    public void editHelp (TextChannel channel) {
        EmbedBuilder eb = new EmbedBuilder()
                .setThumbnail("https://s20.directupload.net/images/210422/zpyz5gkv.png")
                .setTitle("CaptCommunity MusicPlayer")
                .setDescription("Die meisten aktionen lassen sich mit den Reactions ausführen, jedoch sind folgende " +
                        "")
                .addField("__Befehle:__", "\n" +
                        "**[url]** ```cs\n" +
                        "fügt ein Lied hinzu\n" +
                        "```\n" +
                        "**!player stop**```cs\n" +
                        "beendet den player\n" +
                        "```\n" +
                        "**!player pause**```cs\n" +
                        "pausiert das aktuelle Lied\n" +
                        "```\n" +
                        "**!player resume**```cs\n" +
                        "führt das aktuelle Lied fort\n" +
                        "```\n" +
                        "**!player volume [0-100]**```cs\n" +
                        "verändert die Lautstärke\n" +
                        "```\n",false)
                .setFooter("Made by ShuraBlack - Head of Server");
        channel.sendMessage(eb.build()).queue();
    }

    public void addReactions (TextChannel channel) {
        long mesID = 834810455431839774L;

        channel.addReactionById(mesID,"⏹").queue();
        channel.addReactionById(mesID,"⏯").queue();
        channel.addReactionById(mesID,"⏩").queue();
        channel.addReactionById(mesID,"\uD83D\uDD00").queue();
        channel.addReactionById(mesID,"\uD83D\uDD3B").queue();
        channel.addReactionById(mesID,"\uD83D\uDD3A").queue();
    }

}
