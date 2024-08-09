package com.github.calamari34.mantaflipbeta.remoteControl.events;
import com.github.calamari34.mantaflipbeta.features.PacketListener;
import com.github.calamari34.mantaflipbeta.MantaFlip;
import com.github.calamari34.mantaflipbeta.remoteControl.BotUtils;
import com.github.calamari34.mantaflipbeta.utils.ChartUtils;
import com.github.calamari34.mantaflipbeta.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.FileUpload;
import com.github.calamari34.mantaflipbeta.features.Cofl.Cofl;
import net.minecraft.client.Minecraft;
import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.time.Instant;
import java.time.LocalDateTime;

import static com.github.calamari34.mantaflipbeta.MantaFlip.*;
import static com.github.calamari34.mantaflipbeta.features.CaptchaHandler.sendOnClickActionToChat;
import static com.github.calamari34.mantaflipbeta.utils.Utils.formatNumber;
import static com.github.calamari34.mantaflipbeta.utils.Utils.formatNumbers;

public class CommandListener extends ListenerAdapter {
    private MantaFlip mantaFlip;

    public CommandListener(MantaFlip mantaFlip) {
        this.mantaFlip = mantaFlip;
    }


    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("statistics")) {
            event.deferReply().queue();
            ArrayList<HashMap<String, String>> sold_items = MantaFlip.cofl.sold_items;
            ArrayList<HashMap<String, String>> bought_items = MantaFlip.cofl.bought_items;
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("Current Session Statistics");
            embed.addField("Auctions sold", String.valueOf(sold_items.size()), true);
            embed.addField("Auctions bought", String.valueOf(bought_items.size()), true);
            embed.setTimestamp(event.getTimeCreated());
            embed.setImage("attachment://image.png");
            embed.setColor(new Color(0x9E2C7D));
            embed.setFooter("Remote Session • MantaFlip ", "https://cdn.discordapp.com/attachments/1242759092645138474/1261447373503205506/Untitled-2.png?ex=6692fdea&is=6691ac6a&hm=e0b1e050dfd4a457c903bbba8605745d4dd013a78848d98a4f180608f462849c&");
            FileUpload screenshot = FileUpload.fromData(new File(Objects.requireNonNull(BotUtils.takeScreenShot())), "image.png");
            event.getHook().editOriginalAttachments(screenshot).queue();
            event.getHook().editOriginalEmbeds(embed.build()).queue();

            if (sold_items.size() > 0) {
                EmbedBuilder sold = new EmbedBuilder();
                sold.setTitle("Sold Items");
                sold.setColor(new Color(0x9E2C7D));
                sold.setFooter("Remote Session • MantaFlip ", "https://cdn.discordapp.com/attachments/1242759092645138474/1261447373503205506/Untitled-2.png?ex=6692fdea&is=6691ac6a&hm=e0b1e050dfd4a457c903bbba8605745d4dd013a78848d98a4f180608f462849c&");
                sold.setTimestamp(event.getTimeCreated());
                for (HashMap<String, String> sold_item : sold_items) {
                    sold.addField(sold_item.get("item"), "Price: " + sold_item.get("price"), true);
                }
                event.getHook().getInteraction().getMessageChannel().sendMessageEmbeds(sold.build()).queue();
            }
            if (bought_items.size() > 0) {
                EmbedBuilder bought = new EmbedBuilder();
                bought.setTitle("Bought Items");
                bought.setColor(new Color(0x9E2C7D));
                bought.setFooter("Remote Session • MantaFlip ", "https://cdn.discordapp.com/attachments/1242759092645138474/1261447373503205506/Untitled-2.png?ex=6692fdea&is=6691ac6a&hm=e0b1e050dfd4a457c903bbba8605745d4dd013a78848d98a4f180608f462849c&");
                bought.setTimestamp(event.getTimeCreated());
                for (HashMap<String, String> bought_item : bought_items) {
                    bought.addField(bought_item.get("name"), "Price: " + bought_item.get("price"), true);
                }
                event.getHook().getInteraction().getMessageChannel().sendMessageEmbeds(bought.build()).queue();
            }

        }
        if (event.getName().equals("profit")) {
            event.deferReply().queue();
            try {
                File profitChart = ChartUtils.createProfitGraph(MantaFlip.timeIntervals, MantaFlip.profitValues);

                if (profitChart != null && profitChart.exists()) {
                    FileUpload chartUpload = FileUpload.fromData(profitChart, "profit_chart.png");
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setTitle("Session Profit");
                    embed.addField("Total Profit for **" + Minecraft.getMinecraft().getSession().getUsername() + "**", "$" + formatNumbers((int) cumulativeProfit), false);
                    embed.setTimestamp(event.getTimeCreated());
                    embed.setImage("attachment://profit_chart.png");
                    embed.setColor(new Color(0x9E2C7D));
                    embed.setFooter("Remote Session • MantaFlip", "https://cdn.discordapp.com/attachments/1242759092645138474/1261447373503205506/Untitled-2.png?ex=6692fdea&is=6691ac6a&hm=e0b1e050dfd4a457c903bbba8605745d4dd013a78848d98a4f180608f462849c&");

                    event.getHook().sendMessageEmbeds(embed.build()).addFiles(chartUpload).queue();
                } else {
                    // Handle the case where the chart file could not be created or does not exist
                    event.getHook().sendMessage("Unable to generate profit chart.").queue();
                }
            } catch (Exception e) {
                // Log the exception or handle it as needed
                event.getHook().sendMessage("An error occurred while generating the profit chart.").queue();
            }
        }

        if (event.getName().equals("enable") || event.getName().equals("disable")) {
            event.deferReply().queue();
            String type = Objects.requireNonNull(event.getOption("type")).getAsString();
            boolean isEnable = event.getName().equals("enable");

            switch (type) {
                case "claimer":
                    handleToggle(event, isEnable, "claimer");
                    break;
                case "macro":
                    handleToggle(event, isEnable, "macro");
                    break;
                case "relister":
                    handleToggle(event, isEnable, "relister");
                    break;
                default:
                    event.getHook().sendMessage("Unknown type").queue();
            }
        }

        if (event.getName().equals("command")) {
            event.deferReply().queue();
            String command = Objects.requireNonNull(event.getOption("command")).getAsString();
            Utils.sendServerMessage("/" + command);

            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("\uD83D\uDE80 Command Sent");
            embed.setTimestamp(Instant.now());
            embed.setDescription("Just sent the command. `" + command + "` to the server.");
            embed.setThumbnail("https://minotar.net/helm/" + Minecraft.getMinecraft().getSession().getUsername() + "/600.png");
            embed.setFooter("Remote Session • MantaFlip ", "https://cdn.discordapp.com/attachments/1242759092645138474/1261447373503205506/Untitled-2.png?ex=6692fdea&is=6691ac6a&hm=e0b1e050dfd4a457c903bbba8605745d4dd013a78848d98a4f180608f462849c&");
            embed.setColor(new Color(0x9E2C7D));
            event.getHook().sendMessageEmbeds(embed.build()).queue();
        }
        if (event.getName().equals("disconnect")) {
            event.deferReply().queue();
            Minecraft.getMinecraft().theWorld.sendQuittingDisconnectingPacket();
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("⚠\uFE0F Disconnected from the server");
            embed.setTimestamp(Instant.now());
            embed.setDescription("You have just disconnected your current Minecraft session.");
            embed.setThumbnail("https://minotar.net/helm/" + Minecraft.getMinecraft().getSession().getUsername() + "/600.png");
            embed.setFooter("Remote Session • MantaFlip ", "https://cdn.discordapp.com/attachments/1242759092645138474/1261447373503205506/Untitled-2.png?ex=6692fdea&is=6691ac6a&hm=e0b1e050dfd4a457c903bbba8605745d4dd013a78848d98a4f180608f462849c&");
            embed.setColor(new Color(0x9E2C7D));
            event.getHook().sendMessageEmbeds(embed.build()).queue();
        }
        if (event.getName().equals("solve")) {
            event.deferReply().queue();

            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("Attempting to solve the Captcha");
            embed.setTimestamp(Instant.now());
            embed.setDescription("Attempting " + event.getOption("line").getAsString() + " as the solution.");
            embed.setThumbnail("https://minotar.net/helm/" + Minecraft.getMinecraft().getSession().getUsername() + "/600.png");
            embed.setFooter("Remote Session • MantaFlip ", "https://cdn.discordapp.com/attachments/1242759092645138474/1261447373503205506/Untitled-2.png?ex=6692fdea&is=6691ac6a&hm=e0b1e050dfd4a457c903bbba8605745d4dd013a78848d98a4f180608f462849c&");
            embed.setColor(new Color(0x9E2C7D));
            event.getHook().sendMessageEmbeds(embed.build()).queue();
            sendOnClickActionToChat(Integer.parseInt(event.getOption("line").getAsString()));
        }
    }

    private void handleToggle(SlashCommandInteractionEvent event, boolean enable, String featureName) {
        boolean toggleState;
        String state;

        switch (featureName) {
            case "claimer":
                toggleState = mantaFlip.ToggleClaim;
                break;
            case "macro":
                toggleState = mantaFlip.shouldRun;
                break;
            case "relister":
                toggleState = mantaFlip.ToggleRelist;
                break;
            default:
                event.getHook().sendMessage("Unknown feature").queue();
                return;
        }

        if (toggleState == enable) {
            String alreadyState = enable ? "enabled" : "disabled";
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("Already " + alreadyState);
            embed.setTimestamp(Instant.now());
            embed.setDescription("The " + featureName + " is already " + alreadyState + ". No further action is required.");
            embed.setThumbnail("https://minotar.net/helm/" + Minecraft.getMinecraft().getSession().getUsername() + "/600.png");
            embed.setFooter("Remote Session • MantaFlip ", "https://cdn.discordapp.com/attachments/1242759092645138474/1261447373503205506/Untitled-2.png?ex=6692fdea&is=6691ac6a&hm=e0b1e050dfd4a457c903bbba8605745d4dd013a78848d98a4f180608f462849c&");
            embed.setColor(enable ? new Color(0x1ED55F) : new Color(0xC31E42));
            event.getHook().sendMessageEmbeds(embed.build()).queue();
        } else if (!PacketListener.relisting) {
            switch (featureName) {
                case "claimer":
                    mantaFlip.ToggleClaim = enable;
                    break;
                case "macro":
                    mantaFlip.shouldRun = enable;
                    break;
                case "relister":
                    mantaFlip.ToggleRelist = enable;
                    break;
            }
            state = enable ? "Enabled" : "Disabled";
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle(state);
            embed.setTimestamp(Instant.now());
            embed.setDescription("The " + featureName + " has been " + state.toLowerCase() + ".");
            embed.setThumbnail("https://minotar.net/helm/" + Minecraft.getMinecraft().getSession().getUsername() + "/600.png");
            embed.setFooter("Remote Session • MantaFlip ", "https://cdn.discordapp.com/attachments/1242759092645138474/1261447373503205506/Untitled-2.png?ex=6692fdea&is=6691ac6a&hm=e0b1e050dfd4a457c903bbba8605745d4dd013a78848d98a4f180608f462849c&");
            embed.setColor(enable ? new Color(0x1ED55F) : new Color(0xC31E42));
            event.getHook().sendMessageEmbeds(embed.build()).queue();
        } else {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("Action Delayed");
            embed.setTimestamp(Instant.now());
            embed.setDescription("Currently relisting, wait a second and retry");
            embed.setThumbnail("https://minotar.net/helm/" + Minecraft.getMinecraft().getSession().getUsername() + "/600.png");
            embed.setFooter("Remote Session • MantaFlip ", "https://cdn.discordapp.com/attachments/1242759092645138474/1261447373503205506/Untitled-2.png?ex=6692fdea&is=6691ac6a&hm=e0b1e050dfd4a457c903bbba8605745d4dd013a78848d98a4f180608f462849c&");
            embed.setColor(new Color(0xFFA500)); // Using orange color to indicate a warning or delay
            event.getHook().sendMessageEmbeds(embed.build()).queue();
        }
    }
}
