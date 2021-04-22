package commands;

import Model.sql.LoadDriver;
import Model.sql.SQLRequests;
import commands.types.ServerCommand;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import org.w3c.dom.Text;


import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Playlist implements ServerCommand {
    @Override
    public void performCommand(Member m, TextChannel channel, Message message) {
        message.delete().queue();

        if(!m.hasPermission(channel, Permission.ADMINISTRATOR)) {
            return;
        }

        if (!channel.getId().equals("818607332555489340")) {
            EmbedBuilder eb = new EmbedBuilder()
                    .setColor(Color.WHITE)
                    .setDescription("Nutze den " + channel.getGuild().getTextChannelById("818607332555489340").getAsMention() + " TextChannel");
            channel.sendMessage(eb.build()).complete().delete().queueAfter(5, TimeUnit.SECONDS);
            return;
        }

        String[] args = message.getContentDisplay().split(" ");

        LoadDriver ld = new LoadDriver();
        if (args.length == 4 && args[1].equals("add")) {
            ld.executeSQL(SQLRequests.INSERTSONG(m.getId(),args[2],args[3]),SQLRequests.INSERTREQUESTTYPE);
            UserFeedback(channel, m, "Der Link " + args[3] + " wurde zur Playlist " + args[2] + " hinzugefügt", Color.GREEN);
        } else if (args.length == 3 && args[1].equals("remove") && args[2].equals("all")) {
            ld.executeSQL(SQLRequests.DELETEALLSONGS(m.getId()),SQLRequests.DELETEREQUESTTYPE);
            UserFeedback(channel, m,"Alle deine Links wurden entfernt", Color.RED);
        } else if (args.length == 3 && args[1].equals("remove")) {
            ld.executeSQL(SQLRequests.DELETESONG(m.getId(),args[2]),SQLRequests.DELETEREQUESTTYPE);
            UserFeedback(channel, m,"Der Link " + args[2] + " wurde entfernt", Color.RED);
        } else if (args.length == 2 && args[1].equals("show")) {
            ResultSet rs = ld.executeSQL(SQLRequests.SELECTALLSONGS(m.getId()),SQLRequests.SELECTREQUESTTYPE);
            try {
                Map<String, Integer> songs = new TreeMap<>();
                if (rs.next()) {
                    songs.put(rs.getString(1),rs.getInt(2));
                    while (rs.next()) {
                        songs.put(rs.getString(1),rs.getInt(2));
                    }
                }
                rs = ld.executeSQL(SQLRequests.SELECTCOUNTPLAYLIST(m.getId()),SQLRequests.SELECTREQUESTTYPE);
                rs.next();
                channel.sendMessage(createSongMessage(songs,m,rs.getInt(1)).build())
                        .complete().delete().queueAfter(3, TimeUnit.MINUTES);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else if (args.length == 3 && args[1].equals("show")) {
            ResultSet rs = ld.executeSQL(SQLRequests.SELECTSONGPLAYLIST(m.getId(),args[2]),SQLRequests.SELECTREQUESTTYPE);
            try {
                Map<String, List<SongtoID>> songs = new TreeMap<>();
                if (rs.next()) {

                    List<SongtoID> tmp = new LinkedList<>();
                    tmp.add(new SongtoID(rs.getString(2),rs.getInt(3)));
                    songs.put(rs.getString(1), tmp);
                    String playlist = rs.getString(1);

                    while (rs.next()) {
                        songs.get(playlist).add(new SongtoID(rs.getString(2),rs.getInt(3)));
                    }
                }
                channel.sendMessage(createPlaylistSongMessage(songs,m).build())
                        .complete().delete().queueAfter(3, TimeUnit.MINUTES);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else if (args.length == 3 && args[1].equals("feed")) {
            VoiceChannel vc = channel.getGuild().getVoiceChannelById("286629562098647051");
            channel.getGuild().getAudioManager().openAudioConnection(vc);
            ResultSet rs = ld.executeSQL(SQLRequests.SELECTSONGOFPLAYLIST(m.getId(),args[2]),SQLRequests.SELECTREQUESTTYPE);
            List<String> songs = new LinkedList<>();
            try {
                if (rs.next()) {
                    songs.add(rs.getString(1));
                    while (rs.next()) {
                        songs.add(rs.getString(1));
                    }
                }
                Iterator<String> list = songs.iterator();
                Timer timer = new Timer();

                timer.scheduleAtFixedRate(new TimerTask() {

                    @Override
                    public void run() {
                        channel.getGuild().getTextChannelById("804125567388483624").sendMessage(list.next()).queue();
                        if (!list.hasNext()) {
                            timer.cancel();
                        }
                    }
                },1000,1000);
                channel.getGuild().getAudioManager().closeAudioConnection();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        } else if (args.length == 2 && args[1].equals("list")) {
            ResultSet rs = ld.executeSQL(SQLRequests.SELECTPLAYLISTS(m.getId()), SQLRequests.SELECTREQUESTTYPE);
            try {
                List<String> playlists = new LinkedList<>();
                while (rs.next()) {
                    playlists.add(rs.getString(1));
                }
                channel.sendMessage(createPlaylistsMessage(playlists,m).build())
                        .complete().delete().queueAfter(3, TimeUnit.MINUTES);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        ld.close();
    }

    @Override
    public void reactionperform(Member m, TextChannel channel, String mesID
            , String emote, MessageReactionAddEvent event) { throw new UnsupportedOperationException(); }

    @Override
    public void privateperform(String command, User u) { throw new UnsupportedOperationException(); }

    public EmbedBuilder createPlaylistsMessage (List<String> playlists, Member m) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.WHITE);
        eb.setTitle(m.getEffectiveName() + "\nID: " + m.getId());
        eb.setThumbnail(m.getUser().getAvatarUrl());
        eb.setDescription("Eine Liste von all deinen gespeicherten Playlist/s");
        if (playlists.isEmpty()) {
            eb.addField("","Du hast noch keine Playlist/s eingetragen",false);
        } else {
            StringBuilder s = new StringBuilder();
            for (String playlist : playlists) {
                s.append(playlist).append("\n");
            }
            eb.addField("Playlist/s: " + playlists.size(), s.toString(),false);
        }
        return eb;
    }

    public EmbedBuilder createSongMessage (Map<String, Integer> songs, Member m, int playlistcount) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.WHITE);
        eb.setTitle(m.getEffectiveName() + "\nID: " + m.getId());
        eb.setThumbnail(m.getUser().getAvatarUrl());
        eb.setDescription("Eine Liste von all deinen gespeicherten Liedern");
        if (songs.isEmpty()) {
            eb.addField("","Du hast noch keine Lieder eingetragen",false);
        } else {
            StringBuilder s = new StringBuilder();
            for (Map.Entry<String, Integer> song : songs.entrySet()) {
                s.append(song.getKey()).append(" **[").append(song.getValue()).append("]**").append("\n");
            }
            eb.addField("Anzahl: " + songs.size() + " - Playlist/s: " + playlistcount, s.toString(),false);
        }
        eb.setFooter("Diese Nachricht wird automatisch nach 3 Minuten gelöscht");
        return eb;
    }

    public EmbedBuilder createPlaylistSongMessage (Map<String, List<SongtoID>> songs, Member m) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.WHITE);
        eb.setTitle(m.getEffectiveName() + "\nID: " + m.getId());
        eb.setThumbnail(m.getUser().getAvatarUrl());
        eb.setDescription("Eine Liste von allen Liedern in einer bestimmten Playlist");
        if (songs.isEmpty()) {
            eb.addField("","Du hast noch keine Lieder eingetragen",false);
        } else {
            for (Map.Entry<String, List<SongtoID>> entry : songs.entrySet()) {
                StringBuilder s = new StringBuilder();
                for (SongtoID song : entry.getValue()) {
                    s.append(song.song).append(" **[").append(song.id).append("]**").append("\n");
                }
                eb.addField(entry.getKey() + " - Anzahl: " + entry.getValue().size(), s.toString(), false);
            }
        }
        eb.setFooter("Diese Nachricht wird automatisch nach 3 Minuten gelöscht");
        return eb;
    }

    public void UserFeedback (TextChannel channel, Member m, String message, Color color) {
        EmbedBuilder eb = new EmbedBuilder()
                .setColor(color)
                .setDescription(m.getAsMention() + " : " + message)
                .setFooter("CaptCommunity Playlist");
        channel.sendMessage(eb.build()).queue((mes) -> mes.delete().queueAfter(5, TimeUnit.SECONDS));
    }

    private class SongtoID {

        private String song;
        private int id;

        public SongtoID(String song, int id) {
            this.song = song;
            this.id = id;
        }

    }
}
