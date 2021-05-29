package commands;

import commands.types.ServerCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

import java.awt.*;
import java.io.File;

import static model.util.ChannelUtil.*;

public class Guide implements ServerCommand {

    private boolean activ = false;

    @Override
    public void performCommand(Member m, TextChannel channel, Message message) {
        message.delete().queue();

        if (!activ) {
            EmbedBuilder eb = new EmbedBuilder()
                    .setColor(new Color(230,50,70))
                    .setTitle(m.getAsMention() + ", dieser Command ist aktuell nicht aktiv");
            channel.sendMessage(eb.build()).queue();
            return;
        }

        /*
        if (!m.hasPermission(Permission.ADMINISTRATOR)) {
            return;
        }
         */

        if (!channel.getId().equals("847235301956517948")) {
            return;
        }
        guideOverViewEdit(channel);
        //guideOverView(channel);
        //guides(channel);
        //creator(channel);
        //lastWord(channel);
    }

    @Override
    public void reactionperform(Member m, TextChannel channel, String mesID, String emote, MessageReactionAddEvent event) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void privateperform(String command, User u) {
        throw new UnsupportedOperationException();
    }

    public void lastWord(TextChannel channel) {
        EmbedBuilder eb = new EmbedBuilder()
                .setColor(new Color(230,50,70))
                .setTitle("If you like what you see, leave me a \uD83D\uDC96");
        channel.sendMessage(eb.build()).queue();
    }

    public void guideOverView(TextChannel channel) {
        EmbedBuilder eb = new EmbedBuilder()
                .setColor(new Color(230,50,70))
                .setTitle("Guides - Overview");

        channel.sendMessage(eb.build()).queue();
    }

    public void guideOverViewEdit(TextChannel channel) {
        EmbedBuilder eb = new EmbedBuilder()
                .setColor(new Color(230,50,70))
                .setImage("https://s20.directupload.net/images/210526/is7xvgpt.png")
                .setDescription("1. [Basic](https://discord.com/channels/466348212077199380/847235301956517948/847236972224446464)\n" +
                        "2. [Relics](https://discord.com/channels/466348212077199380/847235301956517948/847237025378598922)\n" +
                        "3. [Fishing](https://discord.com/channels/466348212077199380/847235301956517948/847237036213796894)\n" +
                        "4. [Rivens](https://discord.com/channels/466348212077199380/847235301956517948/847237116882059335)\n" +
                        "5. [Railjack](https://discord.com/channels/466348212077199380/847235301956517948/847237147265204284)\n" +
                        "6. [Builds](https://discord.com/channels/466348212077199380/847235301956517948/847237203364937779)\n" +
                        "7. [Trading](https://discord.com/channels/466348212077199380/847235301956517948/847237213388275762)\n" +
                        "8. [Sites](https://discord.com/channels/466348212077199380/847235301956517948/847237223949795368)\n" +
                        "9. [Creator](https://discord.com/channels/466348212077199380/847235301956517948/847237420633161768)\n");
        channel.editMessageById("847236748283084871",eb.build()).queue();
    }

    public void creator(TextChannel channel) {
        Member shura = channel.getGuild().retrieveMemberById("286628057551208450").complete();


        EmbedBuilder me = new EmbedBuilder()
                .setAuthor(shura.getUser().getAsTag(),"https://i.giphy.com/media/QbumCX9HFFDQA/giphy.webp",shura.getUser().getAvatarUrl())
                .setDescription(shura.getAsMention() + " Author\n**Data on Creation:**\nIGN: ShuraBlack\nMR: 30\nTime: 4.310h");

        channel.sendMessage(me.build()).queue();

        EmbedBuilder back = new EmbedBuilder()
                .setDescription("[Back to Overview](https://discord.com/channels/466348212077199380/847235301956517948/847236748283084871)");

        channel.sendMessage(back.build()).queue();
    }

    public void guides (TextChannel channel) {
        // -------------------------------------------------------------------------------------------------------------
        EmbedBuilder basicBanner = new EmbedBuilder()
                .setDescription("[Back](https://discord.com/channels/466348212077199380/847235301956517948/847236748283084871)");

        File basic = new File("E:/FH/Aufgaben/CaptCommunityAssistent/Images/Basic.png");
        channel.sendFile(basic, "Basic.png").embed(basicBanner.build()).queue();
        // -------------------------------------------------------------------------------------------------------------
        EmbedBuilder basicText = new EmbedBuilder()
                .setColor(new Color(230,50,70))
                .setDescription("We will start of easy ^^\n\n" +
                        "[Official Warframe Page](https://www.warframe.com/)\n\n" +
                        "**Damage Types**\n" +
                        "There are many Different Damage type with which you should be familiar, but we will" +
                        "provide you with some tables you can check, or use\n\n" +
                        "[Warframe Damage type](https://warframe.fandom.com/wiki/Damage)");

        channel.sendMessage(basicText.build()).queue();

        EmbedBuilder damageS = new EmbedBuilder()
                .setColor(new Color(230,50,70))
                .setDescription("You can combine Primary Elements to get a new one with other properties")
                .setImage("attachment://DamageStrength.png");
        File damageStrength = new File("E:/FH/Aufgaben/CaptCommunityAssistent/Images/DamageStrength.png");
        channel.sendFile(damageStrength,"DamageStrength.png").embed(damageS.build()).queue();

        EmbedBuilder damageT1 = new EmbedBuilder()
                .setColor(new Color(230,50,70))
                .setDescription("Here you can see all physical damage type and primary elements, inculding there status proc effects")
                .setImage("attachment://DamageType1.png");
        File damageTypes1 = new File("E:/FH/Aufgaben/CaptCommunityAssistent/Images/DamageTypes1.png");
        channel.sendFile(damageTypes1,"DamageType1.png").embed(damageT1.build()).queue();

        EmbedBuilder damageT2 = new EmbedBuilder()
                .setColor(new Color(230,50,70))
                .setDescription("Same goes for all combined elements")
                .setImage("attachment://DamageType2.png");
        File damageTypes2 = new File("E:/FH/Aufgaben/CaptCommunityAssistent/Images/DamageTypes2.png");
        channel.sendFile(damageTypes2,"DamageType2.png").embed(damageT2.build()).queue();

        EmbedBuilder damageST = new EmbedBuilder()
                .setColor(new Color(230,50,70))
                .setDescription("Enemys are sorted in different health/armor categories and those categories have " +
                        "different strength or weaknesses against the damage types")
                .setImage("attachment://DamageStrengthTable.png");
        File damageStrengthTable = new File("E:/FH/Aufgaben/CaptCommunityAssistent/Images/DamageStrengthTable.png");
        channel.sendFile(damageStrengthTable,"DamageStrengthTable.png").embed(damageST.build()).queue();

        EmbedBuilder basicObtaining = new EmbedBuilder()
                .setColor(new Color(230,50,70))
                .setTitle("Obtaining Warframes")
                .setDescription("In Warframe you are able to obtain various none prime Warframes (sorry for the naming :,D) on missions or through Quests")
                .addField("HINT","Many Veterans dont like that recommendation, but getting " +
                        "Rhino on Venus->Fossa will help you a lot at the start",false)
                .setImage("attachment://WarframeAcquisition.png");
        File warframeAqu = new File("E:/FH/Aufgaben/CaptCommunityAssistent/Images/WarframeAcquisition.png");
        channel.sendFile(warframeAqu,"WarframeAcquisition.png").embed(basicObtaining.build()).queue();

        EmbedBuilder basicMastery = new EmbedBuilder()
                .setColor(new Color(230,50,70))
                .setTitle("Mastery Rank")
                .setDescription("As an Player you are able to level up your objects (example: Frames, Weapons, companions, etc.) to level 30 (some to 40)." +
                        " Each level on an object provide you with an defind amount of XP for your Mastery" +
                        " Rank (your superior rank) for the first time you get to this level on the object")
                .addField("HINT","Remember, before you ask an Veteran about what is good or not, try it out yourself. Its part of the game," +
                        " will build your knowlege and at the end, many things are possible",false)
                .setImage("attachment://MasterPoints.png");
        File warframeMas = new File("E:/FH/Aufgaben/CaptCommunityAssistent/Images/MasterPoints.png");
        channel.sendFile(warframeMas,"MasterPoints.png").embed(basicMastery.build()).queue();
        // -------------------------------------------------------------------------------------------------------------
        EmbedBuilder RelicsBanner = new EmbedBuilder()
                .setDescription("[Back](https://discord.com/channels/466348212077199380/847235301956517948/847236748283084871)");

        File relics = new File("E:/FH/Aufgaben/CaptCommunityAssistent/Images/Relics.png");
        channel.sendFile(relics, "Fishing.png").embed(RelicsBanner.build()).queue();

        EmbedBuilder relicsText = new EmbedBuilder()
                .setColor(new Color(230,50,70))
                .setDescription("The best locations where you can farm your Relics")
                .addField("Lith","```cs\n" +
                        "Void - Hepit (Capture) # 100% Drop Chance\n" +
                        "Tip: Use Wukong with an Duration Build" +
                        "\n```",false)
                .addField("Meso","```cs\n" +
                        "Mars - Olympus (Disruption) # 100% Drop Chance on C Rotation" +
                        "\n```",false)
                .addField("Neo","```cs\n" +
                        "Uranus - Ur (Disruption) # 94.92% Drop Chance on C Rotation" +
                        "\n```",false)
                .addField("Meso/Neo","```cs\n" +
                        "Void - Ukko (Capture) # ~50/50% Drop Chance for Meso/Neo\n" +
                        "Tip: Use Wukong with an Duration Build" +
                        "\n```",false)
                .addField("Axi","```cs\n" +
                        "Lua - Apollo (Disruption) # 86.92% Drop Chance on C Rotation\n" +
                        "Hint: B Rotation got 100% and A Rotation got 100% for Neo" +
                        "\n```",false);
        channel.sendMessage(relicsText.build()).queue();

        EmbedBuilder disruptionText = new EmbedBuilder()
                .setDescription("Disruption Table:");

        File disruption = new File("E:/FH/Aufgaben/CaptCommunityAssistent/Images/DisruptionTable.png");
        channel.sendFile(disruption,"DisruptionTable.png").embed(disruptionText.build()).queue();

        // -------------------------------------------------------------------------------------------------------------
        EmbedBuilder FishingBanner = new EmbedBuilder()
                .setDescription("[Back](https://discord.com/channels/466348212077199380/847235301956517948/847236748283084871)");

        File fishing = new File("E:/FH/Aufgaben/CaptCommunityAssistent/Images/Fishing.png");
        channel.sendFile(fishing, "Fishing.png").embed(FishingBanner.build()).queue();

        EmbedBuilder fishingText = new EmbedBuilder()
                .setColor(new Color(230,50,70))
                .setDescription("On all Open-World-Maps you are able to farm ores/gems and especially fishes.\n" +
                        "Those will be needed to build objects and so on. In every player hub exists an Fishing based NPC where you can buy " +
                        "fishing spear/s with your Standing. Following images will help you to find the correct spot for the fish you are looking for")
                .addField("Comment","Sadly there is no good map we can provide for Deimos",false);
        channel.sendMessage(fishingText.build()).queue();

        File fishingC = new File("E:/FH/Aufgaben/CaptCommunityAssistent/Images/FishingCetus.png");
        channel.sendFile(fishingC,"FishingCetus.png").queue();

        File fishingO = new File("E:/FH/Aufgaben/CaptCommunityAssistent/Images/FishingOrbVallis.png");
        channel.sendFile(fishingO,"FishingCetus.png").queue();
        EmbedBuilder fishingHint = new EmbedBuilder()
                .setColor(new Color(230,50,70))
                .setDescription("For more details about fishing, visit [Semlar](https://semlar.com/fish)" +
                        " or [Warframe Hub](https://hub.warframestat.us/#/poe/fish/howto)");
        channel.sendMessage(fishingHint.build()).queue();
        // -------------------------------------------------------------------------------------------------------------
        EmbedBuilder RivenBanner = new EmbedBuilder()
                .setDescription("[Back](https://discord.com/channels/466348212077199380/847235301956517948/847236748283084871)");

        File riven = new File("E:/FH/Aufgaben/CaptCommunityAssistent/Images/Riven.png");
        channel.sendFile(riven, "Riven.png").embed(RivenBanner.build()).queue();

        EmbedBuilder rivenText = new EmbedBuilder()
                .setColor(new Color(230,50,70))
                .setDescription("Riven are a big part of Warframe. Those Mods increase/reduce the stats of a single given Weapons.")
                .addField("How can i obtain a Riven?","There are many way you can get one, but the most easy " +
                        "one is to do your daily sortie or trade them. A new Riven will always come in a veiled state and you" +
                        " need to finish a task first while equipping the Riven in the correct weapon category",false)
                .addField("Other ways?","If you go further in Warframe, Teshin on the Relay is able to sell you one, " +
                        "the NPC Palladino on Earth->Iron Wake, Lotus Gift (Not that often), the Arbitration Honors on Relay (for Archgun Rivens), etc.",false);
        channel.sendMessage(rivenText.build()).queue();

        EmbedBuilder rivenMod = new EmbedBuilder()
                .setColor(new Color(230,50,70))
                .setDescription("An Riven mod is always for a single Weapon type and differ there values by popularity." +
                        " Those values are visible ingame with Circles (1 to 5 circles). " +
                        "Rivens are Mastery Rank locked (between 8 to 16) and always use 18 Capacity at Max Rank")
                .setImage("attachment://RivenMod.png");
        File rivenM = new File("E:/FH/Aufgaben/CaptCommunityAssistent/Images/RivenMod.png");
        channel.sendFile(rivenM,"RivenMod.png").embed(rivenMod.build()).queue();

        EmbedBuilder rivenCircle = new EmbedBuilder()
                .setColor(new Color(230,50,70))
                .setDescription("Circles result in maximum values that define how powerful a riven can get")
                .setImage("attachment://RivenCircle.png");
        File rivenC = new File("E:/FH/Aufgaben/CaptCommunityAssistent/Images/RivenCircle.png");
        channel.sendFile(rivenC,"RivenCircle.png").embed(rivenCircle.build()).queue();

        EmbedBuilder rivenDispo = new EmbedBuilder()
                .setColor(new Color(230,50,70))
                .setDescription("But dont get mislead by that. Behind the simple view of the circles are multipliers, " +
                        "which means, even though 2 rivens share the same circle amount they differ in there max values")
                .setImage("attachment://RivenDispostion.png");
        File rivenD = new File("E:/FH/Aufgaben/CaptCommunityAssistent/Images/RivenDispostion.png");
        channel.sendFile(rivenD,"RivenDispostion.png").embed(rivenDispo.build()).queue();

        EmbedBuilder rivenType = new EmbedBuilder()
                .setColor(new Color(230,50,70))
                .setDescription("On a Riven you can get different amount of + and - stats. This property change the max values as well")
                .setImage("attachment://TypesOfStats.png");
        File rivenT = new File("E:/FH/Aufgaben/CaptCommunityAssistent/Images/TypesOfStats.png");
        channel.sendFile(rivenT,"TypesOfStats.png").embed(rivenType.build()).queue();

        EmbedBuilder RivenTip = new EmbedBuilder()
                .setDescription("To finish this topic we will talk about rerolling. You are able to change the stats on a Riven with rerolling " +
                        "Orbiter-> Mod Module -> choose your riven. Every time you roll, it will consume Kuva which is quite pain in the butty to farm a lot of." +
                        " But below you can see how much it will safe you if you roll Rivens by yourself")
                .setImage("attachment://RollCosts.png");
        File rivenCost = new File("E:/FH/Aufgaben/CaptCommunityAssistent/Images/RollCosts.png");
        channel.sendFile(rivenCost, "RollCosts.png").embed(RivenTip.build()).queue();
        // -------------------------------------------------------------------------------------------------------------
        EmbedBuilder RailjackBanner = new EmbedBuilder()
                .setDescription("[Back](https://discord.com/channels/466348212077199380/847235301956517948/847236748283084871)");

        File railjack = new File("E:/FH/Aufgaben/CaptCommunityAssistent/Images/Railjack.png");
        channel.sendFile(railjack, "Railjack.png").embed(RailjackBanner.build()).queue();

        EmbedBuilder railjackText = new EmbedBuilder()
                .setColor(new Color(230,50,70))
                .setDescription("Railjack is at the moment a less important part of the game and wont provide you (at least mostly)" +
                        " with any needed resources, but none than less we will talk about that topic anyway.")
                .addField("Who need this?","This guide is more oriented on min-max player or beginner who dont know what they may need",false)
                .addField("","We will go through the different components of an Railjack",false);
        channel.sendMessage(railjackText.build()).queue();

        EmbedBuilder shieldArray = new EmbedBuilder()
                .setColor(new Color(230,50,70))
                .setDescription("As the name already tell us are Shield-Arrays for the Shield of your Railjack. " +
                        "Those are pretty dependend on your playstyle, so i dont wanne make an recommendation at this part. " +
                        "Use what ever you like the most")
                .setImage("attachment://ShieldArray.png");
        File rjsa = new File("E:/FH/Aufgaben/CaptCommunityAssistent/Images/ShieldArray.png");
        channel.sendFile(rjsa,"ShieldArray.png").embed(shieldArray.build()).queue();

        EmbedBuilder engine = new EmbedBuilder()
                .setColor(new Color(230,50,70))
                .setDescription("The engine decide how fast your Railjack can travel (regular Speed or Boosting). " +
                        "*Lavan* will give you the most speed faster they removed the fuel system out of the game and *Vidar* is the " +
                        "best mix for your needs")
                .addField("FACT","Total Base Speed = [150 × (1 + Bonus % from Conic Nozzle)] + (Additional Engine Speed) and " +
                        "Total Boost Multiplier = [1.25 × (1 + Bonus % from Ion Burn)] + (Additional Boost Multiplier)",false)
                .setImage("attachment://Engine.png");
        File rje = new File("E:/FH/Aufgaben/CaptCommunityAssistent/Images/Engine.png");
        channel.sendFile(rje,"Engine.png").embed(engine.build()).queue();

        EmbedBuilder plating = new EmbedBuilder()
                .setColor(new Color(230,50,70))
                .setDescription("The Plating change your health(hull)/armor and effective health.")
                .setImage("attachment://Plating.png");
        File rjp = new File("E:/FH/Aufgaben/CaptCommunityAssistent/Images/Plating.png");
        channel.sendFile(rjp,"Plating.png").embed(plating.build()).queue();

        EmbedBuilder platingCalculation = new EmbedBuilder()
                .setColor(new Color(230,50,70))
                .setDescription("Thankfully Hitoshi provide and Calculation for effective health" +
                        ", so no further explanation is needed")
                .setImage("attachment://PlatingCalculation.png");
        File rjpc = new File("E:/FH/Aufgaben/CaptCommunityAssistent/Images/PlatingCalculation.png");
        channel.sendFile(rjpc,"PlatingCalculation.png").embed(platingCalculation.build()).queue();

        EmbedBuilder reactor = new EmbedBuilder()
                .setColor(new Color(230,50,70))
                .setDescription("Reactors are for the 3 different combat based values of the Railjack (Duration, Range Strength)")
                .addField("Which one should i choose?","Its my personal opinion, but i would recommend *Vidar*, cause Duration and Range" +
                        " are the most useful stats you will need",false)
                .addField("What else?","The difference between *MK2* and *MK3* parts is that, *MK2* is able to " +
                        "get higher values but only adress only 1 state and *MK3* cover 2",false);
        File rjr = new File("E:/FH/Aufgaben/CaptCommunityAssistent/Images/Reactor.png");
        channel.sendFile(rjr,"Reactor.png").embed(reactor.build()).queue();

        EmbedBuilder locations = new EmbedBuilder()
                .setColor(new Color(230,50,70))
                .setDescription("Thankfully Hitoshi provide and Calculation for effective health" +
                        ", so no further explanation is needed");
        channel.sendMessage(locations.build()).queue();

        File rjd1 = new File("E:/FH/Aufgaben/CaptCommunityAssistent/Images/Drop1.png");
        channel.sendFile(rjd1,"Drop1.png").queue();
        File rjd2 = new File("E:/FH/Aufgaben/CaptCommunityAssistent/Images/Drop2.png");
        channel.sendFile(rjd2,"Drop2.png").queue();
        // -------------------------------------------------------------------------------------------------------------
        EmbedBuilder BuildBanner = new EmbedBuilder()
                .setDescription("[Back](https://discord.com/channels/466348212077199380/847235301956517948/847236748283084871)");

        File build = new File("E:/FH/Aufgaben/CaptCommunityAssistent/Images/Builds.png");
        channel.sendFile(build, "Builds.png").embed(BuildBanner.build()).queue();

        EmbedBuilder buildText = new EmbedBuilder()
                .setColor(new Color(230,50,70))
                .setDescription("There is not much to talk about beside showing two sites you may need:\n\n" +
                        "[Overframe](https://overframe.gg/)\n" +
                        "[WarframeBuilder](http://warframe-builder.com/)\n\n" +
                        "Those are the most used pages where you can share your own builds with other player");
        channel.sendMessage(buildText.build()).queue();
        // -------------------------------------------------------------------------------------------------------------
        EmbedBuilder TradingBanner = new EmbedBuilder()
                .setDescription("[Back](https://discord.com/channels/466348212077199380/847235301956517948/847236748283084871)");

        File trading = new File("E:/FH/Aufgaben/CaptCommunityAssistent/Images/Trading.png");
        channel.sendFile(trading, "Trading.png").embed(TradingBanner.build()).queue();

        EmbedBuilder tradingText = new EmbedBuilder()
                .setColor(new Color(230,50,70))
                .setDescription("One of the best things about warframe is that you can trade Platinum with other player (Real-Money currency), " +
                        "but sadly there is no ingame market. We will need to use third-party pages to help us out.\n\n" +
                        "[Warframe Market](https://warframe.market/)\n" +
                        "[Riven Market](https://riven.market/)\n\n" +
                        "are serious sites were you can put in offers or requests to other users.")
                .addField("How can i calculate the value of an Riven?","I cant tell you the magic recipe, but checking " +
                        "already existing prices on Riven market and looking up the values on semlar.com will help most of the times",false)
                .addField("Is there a tool for farming prime parts?","Yes, there is a third-party Tool you can use.\n\n" +
                        "[WFInfo](https://wfinfo.warframestat.us/)\n\nThis program will show you the prices on warframe.market while you do your Missions\n```diff\n- Use at own risk. Check the FAQ of the site for more info" +
                        "\nRequirement: You need an *warframe.market* account to make the program fully functional\n```",false);

        channel.sendMessage(tradingText.build()).queue();
        // -------------------------------------------------------------------------------------------------------------
        EmbedBuilder SiteBanner = new EmbedBuilder()
                .setDescription("[Back](https://discord.com/channels/466348212077199380/847235301956517948/847236748283084871)");

        File site = new File("E:/FH/Aufgaben/CaptCommunityAssistent/Images/Sites.png");
        channel.sendFile(site, "Sites.png").embed(SiteBanner.build()).queue();

        EmbedBuilder sitesText = new EmbedBuilder()
                .setColor(new Color(230,50,70))
                .setDescription("Some other useful sites that might help you")
                .addField("Ducats 2 Platin","[Calculator](https://tenno.zone/pricing)",false)
                .addField("Synthesis Target Location","[Target List](https://steamcommunity.com/sharedfiles/filedetails/?id=666483447)",false)
                .addField("Warframe Hub/Times","[Hub](https://hub.warframestat.us/#/)",false);
        channel.sendMessage(sitesText.build()).queue();
        // -------------------------------------------------------------------------------------------------------------
    }

}
