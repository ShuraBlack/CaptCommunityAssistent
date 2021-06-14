package commands;

import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import model.sql.LoadDriver;
import model.util.ChannelUtil;
import model.util.SQLUtil;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class Player implements ServerCommand {

    @Override
    public void performCommand(Member m, TextChannel channel, Message message) {
        if (message.getContentRaw().endsWith("XESCAPEX")) {
            String fakeMessage = message.getContentRaw().replace("XESCAPEX","");
            message = new MessageBuilder().append(fakeMessage).build();
        } else {
            message.delete().queue();
        }
        String[] args = message.getContentRaw().split(" ");

        MusicManager musicManager = DiscordBot.INSTANCE.getAudioPlayer();
        if (!channel.getId().equals(ChannelUtil.MUSIC)) {
            EmbedBuilder eb = new EmbedBuilder()
                    .setColor(Color.WHITE)
                    .setDescription("Füge Lieder im " + channel.getGuild().getTextChannelById(ChannelUtil.MUSIC)
                            .getAsMention() + " TextChannel hinzu");
            channel.sendMessage(eb.build()).complete().delete().queueAfter(5, TimeUnit.SECONDS);
            return;
        }

        if (args.length == 1) {
            
            if (!m.hasPermission(channel, Permission.ADMINISTRATOR)) {
                return;
            }
            //editChannelMessage(channel);
            editHelp(channel);
            //createHelpMessage(channel);
            //createTemplateMessage(channel);
            //createQueueMessage(channel);
            //addReactions(channel);

        } else if (args.length == 3 && args[1].equals("volume")) {

            int volume = Integer.parseInt(args[2]);
            if (volume > 100 || volume < 0) {
                EmbedBuilder eb = new EmbedBuilder().setDescription(m.getAsMention() + ", Lautstärke ist außerhalb des Rahmens (0-100)");
                channel.sendMessage(eb.build()).complete().delete().queueAfter(7, TimeUnit.SECONDS);
                return;
            }
            musicManager.player.setVolume(Integer.parseInt(args[2]));

        } else if (args.length == 3 && args[1].equals("load")) {
            LoadDriver ld = new LoadDriver();
            ResultSet rs = ld.executeSQL(SQLUtil.SELECTSONGSOFPLAYLIST(m.getId(), args[2]));
            try {
                if (rs.next()) {
                    DiscordBot.INSTANCE.getPlayerManager().loadItemOrdered(musicManager
                            , rs.getString(1), new LoadResultHandler(musicManager, m, channel, args, true));
                    while (rs.next()) {
                        DiscordBot.INSTANCE.getPlayerManager().loadItemOrdered(musicManager
                                , rs.getString(1), new LoadResultHandler(musicManager, m, channel, args, true));
                    }
                } else {
                    EmbedBuilder eb = new EmbedBuilder().setDescription(m.getAsMention() + ", du hast keine Playlist mit dem Namen " + args[2]);
                    channel.sendMessage(eb.build()).complete().delete().queueAfter(7, TimeUnit.SECONDS);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            musicManager.scheduler.makeLoadQueueMessage();
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    musicManager.scheduler.editQueueMessage();
                }
            },10000);
            //musicManager.scheduler.editQueueMessage();
            ld.close();
        } else if (args.length == 3 && args[1].equals("save")) {

            if (musicManager.player.getPlayingTrack() == null) {
                EmbedBuilder eb = new EmbedBuilder().setDescription(m.getAsMention() + ", zum abspeichern muss ein Song bereits abgespielt werden");
                channel.sendMessage(eb.build()).complete().delete().queueAfter(7, TimeUnit.SECONDS);
                return;
            }
            LoadDriver ld = new LoadDriver();
            AudioTrackInfo info = musicManager.player.getPlayingTrack().getInfo();
            ld.executeSQL(SQLUtil.INSERTSONG(m.getId(), args[2], info.uri));
            EmbedBuilder eb = new EmbedBuilder().setDescription(m.getAsMention() + ", Song **" + info.title + "** wurde in der Playlist **" + args[2] + "** gespeichert");
            channel.sendMessage(eb.build()).complete().delete().queueAfter(7, TimeUnit.SECONDS);
            ld.close();
        } else if (args.length == 3 && args[1].equals("remove")) {
            try {
                int trackNumber = Integer.parseInt(args[2]);
                if (!musicManager.scheduler.removeTrack(trackNumber)) {
                    EmbedBuilder eb = new EmbedBuilder().setDescription(m.getAsMention() + ", die Playlist ist leer oder die Zahl ist außerhalb der Track Nummern");
                    channel.sendMessage(eb.build()).complete().delete().queueAfter(7, TimeUnit.SECONDS);
                }
            } catch (NumberFormatException nfe) {
                EmbedBuilder eb = new EmbedBuilder().setDescription(m.getAsMention() + ", **" + args[2] + "** ist keine gültige Zahl");
                channel.sendMessage(eb.build()).complete().delete().queueAfter(7, TimeUnit.SECONDS);
            }
        } else if (args.length == 3 && args[1].equals("start")) {
            try {
                int trackNumber = Integer.parseInt(args[2]);
                if (!musicManager.scheduler.playTrack(trackNumber)) {
                    EmbedBuilder eb = new EmbedBuilder().setDescription(m.getAsMention() + ", die Playlist ist leer oder die Zahl ist außerhalb der Track Nummern");
                    channel.sendMessage(eb.build()).complete().delete().queueAfter(7, TimeUnit.SECONDS);
                }
            } catch (NumberFormatException nfe) {
                EmbedBuilder eb = new EmbedBuilder().setDescription(m.getAsMention() + ", **" + args[2] + "** ist keine gültige Zahl");
                channel.sendMessage(eb.build()).complete().delete().queueAfter(7, TimeUnit.SECONDS);
            }
        } else {
            DiscordBot.INSTANCE.getPlayerManager().loadItemOrdered(musicManager
                    , args[1], new LoadResultHandler(musicManager,m,channel,args,false));
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
                } else if (m.hasPermission(Permission.ADMINISTRATOR)) {

                } else {
                    EmbedBuilder eb = new EmbedBuilder().setDescription(m.getAsMention() + ", verbinde dich zuvor mit dem aktiven VoiceChannel");
                    channel.sendMessage(eb.build()).complete().delete().queueAfter(7, TimeUnit.SECONDS);
                    return;
                }
            } else if (m.hasPermission(Permission.ADMINISTRATOR)) {

            } else {
                EmbedBuilder eb = new EmbedBuilder().setDescription(m.getAsMention() + ", verbinde dich zuvor mit dem aktiven VoiceChannel");
                channel.sendMessage(eb.build()).complete().delete().queueAfter(7, TimeUnit.SECONDS);
                return;
            }
        }

        switch (emote) {
            case "⏹": // Stop
                musicManager.player.stopTrack();
                musicManager.scheduler.clear();
                musicManager.scheduler.clearMessages();
                musicManager.player.setVolume(20);
                musicManager.player.setPaused(false);
                musicManager.scheduler.setRepeatQueue(false);

                channel.getGuild().getAudioManager().closeAudioConnection();
                break;
            case "⏯": // Resume/Pause
                musicManager.player.setPaused(!musicManager.player.isPaused());
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
                    EmbedBuilder eb = new EmbedBuilder()
                            .setDescription(m.getAsMention() + ", Lautstärke von " + (volumeDown+10) + " auf " + volumeDown);
                    channel.sendMessage(eb.build()).complete().delete().queueAfter(7, TimeUnit.SECONDS);
                    return;
                }
                EmbedBuilder eb = new EmbedBuilder()
                        .setDescription(m.getAsMention() + ", Lautstärke ist bereits auf 0");
                channel.sendMessage(eb.build()).complete().delete().queueAfter(7, TimeUnit.SECONDS);
                break;
            case "\uD83D\uDD3A": // Plus
                int volumeUp = musicManager.player.getVolume() + 10;
                if (volumeUp <= 100) {
                    musicManager.player.setVolume(volumeUp);
                    EmbedBuilder eb4 = new EmbedBuilder()
                            .setDescription(m.getAsMention() + ", Lautstärke von " + (volumeUp-10) + " auf " + volumeUp);
                    channel.sendMessage(eb4.build()).complete().delete().queueAfter(7, TimeUnit.SECONDS);
                    return;
                }
                EmbedBuilder eb5 = new EmbedBuilder()
                        .setDescription(m.getAsMention() + ", Lautstärke ist bereits auf 100");
                channel.sendMessage(eb5.build()).complete().delete().queueAfter(7, TimeUnit.SECONDS);
                break;
            case "\uD83D\uDD02": // Repeat Track
                if (musicManager.player.getPlayingTrack() != null) {
                    if (musicManager.scheduler.getAudioTrack() == null) {
                        musicManager.scheduler.setAudioTrack(musicManager.player.getPlayingTrack().makeClone());
                        EmbedBuilder eb2 = new EmbedBuilder().setDescription(m.getAsMention() + ", Track wiederholen aktiviert");
                        channel.sendMessage(eb2.build()).complete().delete().queueAfter(7, TimeUnit.SECONDS);
                    } else {
                        musicManager.scheduler.setAudioTrack(null);
                        EmbedBuilder eb3 = new EmbedBuilder().setDescription(m.getAsMention() + ", Track wiederholen deaktiviert");
                        channel.sendMessage(eb3.build()).complete().delete().queueAfter(7, TimeUnit.SECONDS);
                    }
                }
                break;
            case "\uD83D\uDD01": // Repeat Queue
                if (musicManager.player.getPlayingTrack() != null) {
                    if (musicManager.scheduler.isRepeatQueue()) {
                        musicManager.scheduler.setRepeatQueue(false);
                        EmbedBuilder eb2 = new EmbedBuilder().setDescription(m.getAsMention() + ", Queue wiederholen deaktiviert");
                        channel.sendMessage(eb2.build()).complete().delete().queueAfter(7, TimeUnit.SECONDS);
                    } else {
                        musicManager.scheduler.setRepeatQueue(true);
                        EmbedBuilder eb3 = new EmbedBuilder().setDescription(m.getAsMention() + ", Queue wiederholen aktiviert");
                        channel.sendMessage(eb3.build()).complete().delete().queueAfter(7, TimeUnit.SECONDS);
                    }
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
                                musicManager.scheduler.clear();
                                musicManager.scheduler.clearMessages();
                                musicManager.player.setVolume(20);
                                musicManager.player.setPaused(false);
                                musicManager.scheduler.setRepeatQueue(false);
                                channel.getGuild().getAudioManager().closeAudioConnection();
                                this.cancel();
                            }
                        }
                    }, 100, period);
                audioManager.openAudioConnection(vc);
                return true;
            } else {
                EmbedBuilder eb = new EmbedBuilder().setDescription(m.getAsMention() + ", verbinde dich zuvor mit einem VoiceChannel");
                channel.sendMessage(eb.build()).complete().delete().queueAfter(7, TimeUnit.SECONDS);
                return false;
            }
        } else {
            VoiceChannel vc = m.getVoiceState().getChannel();
            if (vc != null) {
                if (vc.getMembers().stream().map(ISnowflake::getId)
                        .noneMatch(id -> id.equals(DiscordBot.INSTANCE.Manager.getSelfUser().getId()))) {
                    EmbedBuilder eb = new EmbedBuilder().setDescription(m.getAsMention() + ", verbinde dich zuvor mit dem aktiven VoiceChannel");
                    channel.sendMessage(eb.build()).complete().delete().queueAfter(7, TimeUnit.SECONDS);
                    return false;
                }
                return true;
            } else {
                EmbedBuilder eb = new EmbedBuilder().setDescription(m.getAsMention() + ", verbinde dich zuvor mit dem aktiven VoiceChannel");
                channel.sendMessage(eb.build()).complete().delete().queueAfter(7, TimeUnit.SECONDS);
                return false;
            }
        }
    }

    public void editChannelMessage (TextChannel channel) {
        String topic = "⏹ Stop/Leave — ⏯ Pause/Resume - ⏩ Skip" +
                " - \uD83D\uDD02 Repeat Current Track — \uD83D\uDD3A \uD83D\uDD3B Volume Up/Down 10";
        channel.getManager().setTopic(topic).queue();
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
                        "über einen Befehl nutzbar")
                .addField("[url]","```cs\nLädt Track/Playlist (YT)\n```",true)
                .addField("!player load [playlist]","```cs\nLädt deine Bot Playlist\n```",true)
                .addField("!player save [playlist]","```cs\nSpeichert auf eine Bot Playlist\n```",true)
                .addField("!player volume\n[0-100]","```cs\nVerändert die Lautstärke\n```",true)
                .addField("!player remove [trackNo.]","```cs\nEntfernt den Track mit der Nummer\n```",true)
                .addField("!player start [trackNo.]","```cs\nStartet den Track mit der Nummer\n```",true)
                .setFooter("Made by ShuraBlack - Head of Server");
        channel.editMessageById("834810454517612574", eb.build()).queue();
    }

    public void addReactions (TextChannel channel) {
        long mesID = 834810455431839774L;

        channel.retrieveMessageById(mesID).complete().clearReactions().queue();
        channel.addReactionById(mesID,"⏹").queue();
        channel.addReactionById(mesID,"⏯").queue();
        channel.addReactionById(mesID,"⏩").queue();
        channel.addReactionById(mesID,"\uD83D\uDD00").queue();
        channel.addReactionById(mesID,"\uD83D\uDD02").queue();
        channel.addReactionById(mesID, "\uD83D\uDD01").queue();
        channel.addReactionById(mesID,"\uD83D\uDD3B").queue();
        channel.addReactionById(mesID,"\uD83D\uDD3A").queue();
    }

    @Override
    public void performSlashCommand(SlashCommandEvent event) {
        TextChannel channel = event.getTextChannel();

        MusicManager musicManager = DiscordBot.INSTANCE.getAudioPlayer();
        if (!channel.getId().equals(ChannelUtil.MUSIC)) {
            EmbedBuilder eb = new EmbedBuilder()
                    .setColor(Color.WHITE)
                    .setDescription("Füge Lieder im " + channel.getGuild().getTextChannelById(ChannelUtil.MUSIC)
                            .getAsMention() + " TextChannel hinzu");
            event.replyEmbeds(eb.build()).setEphemeral(true).queue();
            return;
        }

        if (event.getName().equals("volume")) {

            int volume = Integer.parseInt(event.getOption("value").getAsString());
            if (volume > 100 || volume < 0) {
                EmbedBuilder eb = new EmbedBuilder().setDescription("Lautstärke ist außerhalb des Rahmens (0-100)");
                event.replyEmbeds(eb.build()).queue();
                return;
            }
            musicManager.player.setVolume(volume);
        } else if (event.getName().equals("play")) {
            String url = event.getOption("url").getAsString();
            DiscordBot.INSTANCE.getPlayerManager().loadItemOrdered(musicManager
                    , url, new LoadResultHandler(musicManager,event.getMember(),channel,url.split(""),false));
        } else if (event.getName().equals("load")) {
            LoadDriver ld = new LoadDriver();
            String id = event.getUser().getId();
            String playlist = event.getOption("playlist").getAsString();
            ResultSet rs = ld.executeSQL(SQLUtil.SELECTSONGSOFPLAYLIST(id, playlist));
            try {
                if (rs.next()) {
                    DiscordBot.INSTANCE.getPlayerManager().loadItemOrdered(musicManager
                            , rs.getString(1), new LoadResultHandler(musicManager, event.getMember(), channel, playlist.split(""), true));
                    while (rs.next()) {
                        DiscordBot.INSTANCE.getPlayerManager().loadItemOrdered(musicManager
                                , rs.getString(1), new LoadResultHandler(musicManager, event.getMember(), channel, playlist.split(""), true));
                    }
                    EmbedBuilder eb = new EmbedBuilder().setDescription("Playlist wurde gefunden und wird geladen");
                    event.replyEmbeds(eb.build()).setEphemeral(true).queue();
                } else {
                    EmbedBuilder eb = new EmbedBuilder().setDescription("Du hast keine Playlist mit dem Namen " + playlist);
                    event.replyEmbeds(eb.build()).setEphemeral(true).queue();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            musicManager.scheduler.makeLoadQueueMessage();
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    musicManager.scheduler.editQueueMessage();
                }
            },10000);
            ld.close();
        } else if (event.getName().equals("save")) {

            if (musicManager.player.getPlayingTrack() == null) {
                EmbedBuilder eb = new EmbedBuilder().setDescription("Zum abspeichern muss ein Song bereits abgespielt werden");
                event.replyEmbeds(eb.build()).setEphemeral(true).queue();
                return;
            }
            LoadDriver ld = new LoadDriver();
            String id = event.getUser().getId();
            String playlist = event.getOption("playlist").getAsString();
            AudioTrackInfo info = musicManager.player.getPlayingTrack().getInfo();
            ld.executeSQL(SQLUtil.INSERTSONG(id, playlist, info.uri));
            EmbedBuilder eb = new EmbedBuilder().setDescription("Song **" + info.title + "** wurde in der Playlist **" + playlist + "** gespeichert");
            event.replyEmbeds(eb.build()).setEphemeral(true).queue();
            ld.close();
        } else if (event.getName().equals("remove")) {
            String id = event.getUser().getId();
            int trackNumber = Integer.parseInt(event.getOption("trackNumber").getAsString());
            if (!musicManager.scheduler.removeTrack(trackNumber)) {
                EmbedBuilder eb = new EmbedBuilder().setDescription("Die Playlist ist leer oder die Zahl ist außerhalb der Track Nummern");
                event.replyEmbeds(eb.build()).setEphemeral(true).queue();
            } else {
                EmbedBuilder eb = new EmbedBuilder().setDescription("Track wurde erfolgreich entfernt");
                event.replyEmbeds(eb.build()).setEphemeral(true).queue();
            }
        } else if (event.getName().equals("start")) {
            String id = event.getUser().getId();
            int trackNumber = Integer.parseInt(event.getOption("trackNumber").getAsString());
            if (!musicManager.scheduler.playTrack(trackNumber)) {
                EmbedBuilder eb = new EmbedBuilder().setDescription("Die Playlist ist leer oder die Zahl ist außerhalb der Track Nummern");
                event.replyEmbeds(eb.build()).setEphemeral(true).queue();
            } else {
                EmbedBuilder eb = new EmbedBuilder().setDescription("Track wurde erfolgreich gestartet");
                event.replyEmbeds(eb.build()).queue();
            }
        }

    }

    private class LoadResultHandler implements AudioLoadResultHandler {

        private final MusicManager musicManager;
        private final Member m;
        private final TextChannel channel;
        private final String[] args;
        private final boolean load;

        public LoadResultHandler(MusicManager musicManager, Member m, TextChannel channel, String[] args, boolean load) {
            this.musicManager = musicManager;
            this.m = m;
            this.channel = channel;
            this.args = args;
            this.load = load;
        }

        @Override
        public void trackLoaded(AudioTrack track) {
            if (!load) {
                EmbedBuilder eb = new EmbedBuilder().setDescription("Hinzugefügt zur Warteschlange **" + track.getInfo().title + "**");
                channel.sendMessage(eb.build()).complete().delete().queueAfter(7, TimeUnit.SECONDS);
            }

            if(!play(channel.getGuild(), musicManager, track, m, channel)) return;
            if (!load) {
                musicManager.scheduler.editQueueMessage();
            }
        }

        @Override
        public void playlistLoaded(AudioPlaylist playlist) {
            for (AudioTrack at : playlist.getTracks()) {
                if(!play(channel.getGuild(), musicManager, at, m, channel)) return;
            }

            if (!load) {
                musicManager.scheduler.editQueueMessage();
                EmbedBuilder eb = new EmbedBuilder().setDescription("Hinzugefügt zur Warteschlange **" + playlist.getName() + "**");
                channel.sendMessage(eb.build()).complete().delete().queueAfter(7, TimeUnit.SECONDS);
            }
        }

        @Override
        public void noMatches() {
            EmbedBuilder eb = new EmbedBuilder().setDescription("Kein Ergebnis für **" + args[1] + "**");
            channel.sendMessage(eb.build()).complete().delete().queueAfter(7,TimeUnit.SECONDS);
        }

        @Override
        public void loadFailed(FriendlyException e) {
            EmbedBuilder eb = new EmbedBuilder().setDescription("Konnte **" + args[1] + "** nicht abspielen");
            channel.sendMessage(eb.build()).complete().delete().queueAfter(7,TimeUnit.SECONDS);
        }
    }

}
