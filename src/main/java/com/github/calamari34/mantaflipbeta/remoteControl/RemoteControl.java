package com.github.calamari34.mantaflipbeta.remoteControl;

import com.github.calamari34.mantaflipbeta.MantaFlip;
import com.github.calamari34.mantaflipbeta.remoteControl.events.CommandListener;
import com.github.calamari34.mantaflipbeta.remoteControl.events.ReadyListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class RemoteControl {
    public JDA bot;

    public RemoteControl() {
        init();
    }

    public void init() {
        boolean remoteControl = MantaFlip.configHandler.getBoolean("RemoteControl");
        String token = MantaFlip.configHandler.getString("BotToken");
        if (!remoteControl) {
            System.out.println("[MantaFlip Remote Control] Remote control disabled, skipping");
            return;
        }
        if (token == null || token.equals("")) {
            System.out.println("[MantaFlip Remote Control] Bot token not set, disabling remote control");
            return;
        }
        System.out.println("[MantaFlip Remote Control] Enabling remote control bot");
        JDABuilder jda = JDABuilder.createDefault(token);
        MantaFlip mantaFlip = new MantaFlip();
        jda.addEventListeners(new ReadyListener(), new CommandListener(mantaFlip));
        jda.setActivity(Activity.watching("your coins"));
        jda.setStatus(OnlineStatus.ONLINE);
        bot = jda.build();

        bot.updateCommands().addCommands(
                Commands.slash("profit", "Analysis of profit in a set session").setGuildOnly(true),
                Commands.slash("statistics", "Statistics of current session").setGuildOnly(true),
                Commands.slash("enable", "Enable a feature of the mod").setGuildOnly(true)
                        .addOptions(new OptionData(OptionType.STRING, "type", "The type of feature to turn on").addChoice("Auto Claimer", "claimer").addChoice("COFL Macro", "macro").addChoice("Auto Relister", "relister")),
                Commands.slash("command", "Runs a command on client side").setGuildOnly(true).addOptions(new OptionData(OptionType.STRING, "command", "The command to run", true)),
                Commands.slash("disconnect", "Disconnect from server").setGuildOnly(true),
                Commands.slash("disable", "Enable a feature of the mod").setGuildOnly(true)
                        .addOptions(new OptionData(OptionType.STRING, "type", "The type of feature to turn off").addChoice("Auto Claimer", "claimer").addChoice("COFL Macro", "macro").addChoice("Auto Relister", "relister"))


                ).queue();
    }
}