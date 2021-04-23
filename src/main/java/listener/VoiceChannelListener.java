package listener;

import model.sql.LoadDriver;
import model.sql.SQLUtil;
import com.mysql.cj.log.Slf4JLogger;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.channel.voice.VoiceChannelDeleteEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class VoiceChannelListener extends ListenerAdapter {

    List<String> removed = new LinkedList<>();

    @Override
    public void onGuildVoiceJoin(GuildVoiceJoinEvent event) {
        if (event.getMember().getUser().isBot()) {
            return;
        }
        String channelid = event.getChannelJoined().getId();
        if (channelid.equals("286629767346782208")) {
            LoadDriver ld = new LoadDriver();
            List<Permission> allow = new LinkedList<>();
            allow.add(Permission.MANAGE_CHANNEL);
            allow.add(Permission.KICK_MEMBERS);
            allow.add(Permission.VOICE_MOVE_OTHERS);
            allow.add(Permission.VOICE_MUTE_OTHERS);
            String newechannelid = event.getChannelJoined().createCopy()
                    .setName("T: " + event.getMember().getUser().getAsTag())
                    .setPosition(0)
                    .setParent(event.getChannelJoined().getGuild().getCategoryById("820340114538102814"))
                    .addMemberPermissionOverride(Long.parseLong(event.getMember().getId()), allow,new LinkedList<>())
                    .complete()
                    .getId();
            VoiceChannel vc = event.getGuild().getVoiceChannelById(newechannelid);
            assert vc != null;
            event.getMember().getGuild()
                    .moveVoiceMember(event.getMember(),vc).queue();
            ld.executeSQL(SQLUtil.INSERTTMPCHANNEL(newechannelid, event.getMember().getId()), SQLUtil.INSERTREQUESTTYPE);
            ld.close();
        }
    }

    @Override
    public void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {
        if (event.getMember().getUser().isBot()) {
            return;
        }
        if (event.getChannelLeft().getMembers().isEmpty()) {
            LoadDriver ld = new LoadDriver();
            String channelid = event.getChannelLeft().getId();
            ResultSet rs = ld.executeSQL(SQLUtil.SELECTTMPCHANNELS(channelid), SQLUtil.SELECTREQUESTTYPE);
            try {
                if (rs.next()) {
                    if (!event.getChannelLeft().getMembers().isEmpty()) {
                        return;
                    }
                    removed.add(event.getChannelLeft().getId());
                    event.getChannelLeft().delete().queue();
                    ld.executeSQL(SQLUtil.DELETETMPCHANNEL(channelid), SQLUtil.DELETEREQUESTTYPE);
                }
                ld.close();
            } catch (SQLException throwables) {
                Slf4JLogger logger = new Slf4JLogger("VoiceChannelListener.Leave");
                logger.logError("Error appeared in the MySQL DELETETMPCHANNEL",throwables);
            }
        }
    }

    @Override
    public void onGuildVoiceMove(GuildVoiceMoveEvent event) {
        if (event.getMember().getUser().isBot()) {
            return;
        }

        LoadDriver ld = new LoadDriver();
        String channelid = event.getChannelJoined().getId();
        if (channelid.equals("286629767346782208")) {
            List<Permission> allow = new LinkedList<>();
            allow.add(Permission.MANAGE_CHANNEL);
            allow.add(Permission.KICK_MEMBERS);
            allow.add(Permission.VOICE_MOVE_OTHERS);
            allow.add(Permission.VOICE_MUTE_OTHERS);
            String newchannelid = event.getChannelJoined().createCopy()
                    .setName("T: " + event.getMember().getUser().getAsTag())
                    .setPosition(0)
                    .setParent(event.getChannelJoined().getGuild().getCategoryById("820340114538102814"))
                    .addMemberPermissionOverride(Long.parseLong(event.getMember().getId()), allow,new LinkedList<>())
                    .complete()
                    .getId();
            VoiceChannel vc = event.getGuild().getVoiceChannelById(newchannelid);
            assert vc != null;
            event.getMember().getGuild()
                    .moveVoiceMember(event.getMember(),vc).queue();
            ld.executeSQL(SQLUtil.INSERTTMPCHANNEL(newchannelid, event.getMember().getId()), SQLUtil.INSERTREQUESTTYPE);
        }

        ResultSet rs = ld.executeSQL(SQLUtil.SELECTTMPCHANNELS(event.getChannelLeft().getId()), SQLUtil.SELECTREQUESTTYPE);
        String channelidleft = event.getChannelLeft().getId();
        try {
            if (rs.next()) {
                if (!event.getChannelLeft().getMembers().isEmpty()) {
                    return;
                }
                removed.add(event.getChannelLeft().getId());
                event.getChannelLeft().delete().queue();
                ld.executeSQL(SQLUtil.DELETETMPCHANNEL(channelidleft), SQLUtil.DELETEREQUESTTYPE);
            }
        } catch (SQLException throwables) {
            Slf4JLogger logger = new Slf4JLogger("VoiceChannelListener.Move.Left");
            logger.logError("Error appeared in the MySQL DELETETMPCHANNEL",throwables);
        }
        ld.close();
    }

    @Override
    public void onVoiceChannelDelete(VoiceChannelDeleteEvent event) {
        if (removed.remove(event.getChannel().getId())) {
            return;
        }
        LoadDriver ld = new LoadDriver();
        String channelid = event.getChannel().getId();
        ResultSet rs = ld.executeSQL(SQLUtil.SELECTTMPCHANNELS(channelid), SQLUtil.SELECTREQUESTTYPE);
        try {
            if (rs.next()) {
                ld.executeSQL(SQLUtil.DELETETMPCHANNEL(channelid), SQLUtil.DELETEREQUESTTYPE);
            }
        } catch (SQLException throwables) {
            Slf4JLogger logger = new Slf4JLogger("VoiceChannelListener.Delete");
            logger.logError("Error appeared in the MySQL DELETETMPCHANNEL",throwables);
        }
        ld.close();
    }
}
