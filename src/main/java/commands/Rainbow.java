package commands;

import commands.types.ServerCommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

import java.util.Timer;
import java.util.TimerTask;

public class Rainbow implements ServerCommand {

    private Timer timer = new Timer();
    private Role owner = null;
    //private final int[] colors = {16711680,255,16776960,16711935,65280,65535};
    private final int[] colors = {16721960,16750120,9868840,2686760,2686870,2659990,2631910,9840870,9840790};
    private int count = 0;
    @Override
    public void performCommand(Member m, TextChannel channel, Message message) {
        message.delete().queue();

        if(!m.hasPermission(channel, Permission.ADMINISTRATOR)) {
            return;
        }
        String[] args = message.getContentDisplay().split(" ");
        if (args.length == 2 && args[1].equals("on")) {
            if (owner == null) {
                owner = channel.getGuild().getRoleById("286631051244208131");
            }
            timer.scheduleAtFixedRate(new TimerTask() {
                public void run() {
                    if (count+1 == colors.length) {
                        count = 0;
                    }
                    owner.getManager().setColor(colors[count++]).queue();
                }
            }, 100,1000);
        } else if (args[1].equals("off")) {
            timer.cancel();
        }
    }

    @Override
    public void reactionperform(Member m, TextChannel channel, String mesID
            , String emote, MessageReactionAddEvent event) { throw new UnsupportedOperationException();}

    @Override
    public void privateperform(String command, User u) { throw new UnsupportedOperationException(); }
}
