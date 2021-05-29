package startup;

import com.mysql.cj.log.Slf4JLogger;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import commands.types.ServerCommand;
import listener.*;
import model.web.Server;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;
import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Main Class for the Discord Bot
 */
public class DiscordBot {

    // Instance of startup.DiscordBot
    public static DiscordBot INSTANCE;

    // Configs for Discord Bot and MySQL
    public static Properties PROPERTIES;

    //Manager
    public JDA Manager;
    private final CommandManager CManager;

    // AudioPlayer
    private final AudioPlayerManager playerManager;
    private final MusicManager musicManager;

    /**
     * Main for Bot Start-up
     * @param args x
     * @throws IllegalArgumentException if argument isnt allowed
     */
    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
            new DiscordBot();
        } catch (IllegalArgumentException | ClassNotFoundException |
                IllegalAccessException | InstantiationException e) {
            Slf4JLogger logger = new Slf4JLogger("JDA.Inizial");
            logger.logError("Error in initialization of the Discord bot",e);
        }
    }

    /**
     * Setup for Bot and Managers
     * @throws LoginException
     * @throws IllegalArgumentException
     */
    public DiscordBot () {

        INSTANCE = this;
        PROPERTIES = loadProps();

        this.playerManager = new DefaultAudioPlayerManager();
        this.musicManager = new MusicManager(this.playerManager);

        try {

            Manager = JDABuilder.createDefault(PROPERTIES.getProperty("token"))
                    .enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_VOICE_STATES)
                    .disableIntents(getDisabledIntents())
                    .disableCache(getDisabledCacheFlags())
                    .setMemberCachePolicy(MemberCachePolicy.ALL)
                    .addEventListeners(new ReactionListener())
                    .addEventListeners(new CommandListener())
                    .addEventListeners(new VoiceChannelListener())
                    .addEventListeners(new LobbyListener())
                    .addEventListeners(new ManagerListener())
                    .setLargeThreshold(50)
                    .setActivity(Activity.watching("The CaptCom Server"))
                    .setStatus(OnlineStatus.ONLINE)
                    .build();

        } catch (LoginException e) {
            Slf4JLogger logger = new Slf4JLogger("JDA.Inizial");
            logger.logError("Error in initialization of the Discord bot",e);
        } catch (IllegalArgumentException e) {
            Slf4JLogger logger = new Slf4JLogger("Config.Loader");
            logger.logError("The config.properties file might miss the token value",e);
        }
        this.CManager = new CommandManager();
        String version = "NOT DEFINED";
        if (PROPERTIES.containsKey("version")) {
            version = PROPERTIES.getProperty("version");
        }
        System.out.println("_________                __   _________                 \n" +
                "\\_   ___ \\_____  _______/  |_ \\_   ___ \\  ____   _____  \n" +
                "/    \\  \\/\\__  \\ \\____ \\   __\\/    \\  \\/ /  _ \\ /     \\ \n" +
                "\\     \\____/ __ \\|  |_> >  |  \\     \\___(  <_> )  Y Y  \\\n" +
                " \\______  (____  /   __/|__|   \\______  /\\____/|__|_|  /\n" +
                "        \\/     \\/|__|                 \\/             \\/\n" +
                "DISCORD_Server_Bot: Version: " + version + "       By_ShuraBlack\n");

        try {
            AudioSourceManagers.registerRemoteSources(playerManager);
            AudioSourceManagers.registerLocalSource(playerManager);
            Slf4JLogger logger = new Slf4JLogger("JDA.LavaPlayer");
            logger.logInfo("LavaPlayer got registered and handler will be set on use");
        } catch (Exception e) {
            Slf4JLogger logger = new Slf4JLogger("JDA.LavaPlayer");
            logger.logError("Error while adding the AudioPlayer to the Registry",e);
        }

        shutdown();

    }

    /**
     * shutdown controll
     */
    public void shutdown () {
        new Thread(() -> {

            String line = "";
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            try {
                System.out.println("\n====================================================\n" +
                        "Commands:\n" +
                        "modules -> shows all activ and available modules\n" +
                        "remove <modulename> -> deactivated module\n" +
                        "add <modulename> -> activate module\n" +
                        "exit -> turn off the bot" +
                        "\n====================================================\n");
                while ((line = reader.readLine()) != null) {
                    if(line.equalsIgnoreCase("exit")) {
                        if (Manager != null) {
                            reader.close();
                            Manager.getPresence().setStatus(OnlineStatus.OFFLINE);
                            Manager.shutdown();
                            System.out.println("==========================================\n" +
                                    "Successfuly went offline\n" +
                                    "==========================================");
                        }
                        reader.close();
                        System.exit(1);
                    } else if (line.equalsIgnoreCase("modules")) {
                        System.out.println("==========================================\n" +
                                "Modules: \n" +
                                "==========================================");
                        System.out.println("\nActiv:");
                        if (getCManager().commands.size() > 0) {
                            List<String> activ = getCManager().commands.keySet()
                                    .stream().sorted().collect(Collectors.toList());
                            for (String name : activ) {
                                System.out.println(name);
                            }
                        } else {
                            System.out.println("Empty");
                        }

                        System.out.println("\nAvailable:");
                        if (getCManager().deactivated.size() > 0) {
                            List<String> available = getCManager().deactivated.keySet()
                                    .stream().sorted().collect(Collectors.toList());
                            for (String name : available) {
                                System.out.println(name);
                            }
                        } else {
                            System.out.println("Empty");
                        }
                        System.out.println("==========================================");
                    } else if (line.startsWith("remove ")) {
                        String[] args = line.split(" ");
                        if (getCManager().commands.containsKey(args[1])) {
                            ServerCommand tmp = getCManager().commands.get(args[1]);
                            getCManager().deactivated.put(args[1], tmp);
                            getCManager().commands.remove(args[1]);
                            System.out.println("==========================================\n" +
                                    "Deactivated Module > " + args[1] + "\n" +
                                    "==========================================");
                        } else {
                            System.out.println("==========================================\n" +
                                    "No active Module with > " + args[1] + "\n" +
                                    "==========================================");
                        }
                    } else if (line.startsWith("add ")) {
                        String[] args = line.split(" ");
                        if (getCManager().deactivated.containsKey(args[1])) {
                            ServerCommand tmp = getCManager().deactivated.get(args[1]);
                            getCManager().commands.put(args[1], tmp);
                            getCManager().deactivated.remove(args[1]);
                            System.out.println("==========================================\n" +
                                    "Deactivated Module > " + args[1] + "\n" +
                                    "==========================================");
                        } else {
                            System.out.println("==========================================\n" +
                                    "The Module > " + args[1] + " might be already activ or doesnt exist\n" +
                                    "==========================================");
                        }
                    } else {
                        System.out.println("Use `exit` to shutdown.");
                    }
                }
            } catch (IOException ignored) { }
        }).start();
    }

   private Properties loadProps() {
        Properties prop = new Properties();

        String propFileName = "config.properties";
        File config = new File(propFileName);
        InputStream inputStream = null;
        Slf4JLogger logger = new Slf4JLogger("Config.Loader");
        try {
            inputStream = new FileInputStream(config);
        } catch (FileNotFoundException e) {
            logger.logError("Could not find the config.properties file\n" +
                    "Specify a file with token->DiscordBot | url,username,password->MySQL Database");
            System.exit(1);
        }
       try {
           prop.load(inputStream);
           logger.logInfo("Load config.properties");
       } catch (IOException e) {
           e.printStackTrace();
       }
        if (!prop.containsKey("token") ||
                !prop.containsKey("url") ||
                !prop.containsKey("username") ||
                !prop.containsKey("password")) {
            logger.logError("The config.properties file misses one of the following keys -> token,url,username,password");
            System.exit(1);
        }
        return prop;
    }

    /**
     * getManager
     * @return startup.CommandManager
     */
    public CommandManager getCManager() {
        return CManager;
    }

    public AudioPlayerManager getPlayerManager() {
        return playerManager;
    }

    public synchronized MusicManager getAudioPlayer() {
        this.Manager.getGuildById("286628427140825088").getAudioManager()
                .setSendingHandler(musicManager.getSendHandler());
        return this.musicManager;
    }

    private List<GatewayIntent> getDisabledIntents () {
        List<GatewayIntent> list = new LinkedList<>();
        list.add(GatewayIntent.DIRECT_MESSAGE_REACTIONS);
        list.add(GatewayIntent.DIRECT_MESSAGE_TYPING);
        list.add(GatewayIntent.GUILD_PRESENCES);
        return list;
    }

    private List<CacheFlag> getDisabledCacheFlags () {
        List<CacheFlag> list = new LinkedList<>();
        list.add(CacheFlag.ACTIVITY);
        list.add(CacheFlag.CLIENT_STATUS);
        return list;
    }

}
