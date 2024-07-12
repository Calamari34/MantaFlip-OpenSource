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
import java.util.Objects;
import java.time.Instant;
import java.time.LocalDateTime;

import static com.github.calamari34.mantaflipbeta.MantaFlip.*;

public class CommandListener extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("statistics")) {
            event.deferReply().queue();
            ArrayList<HashMap<String, String>> sold_items = MantaFlip.cofl.sold_items;
            ArrayList<HashMap<String, String>> bought_items = MantaFlip.cofl.bought_items;
            EmbedBuilder embed = new EmbedBuilder();
            embed.setAuthor("MantaFlip", "https://cdn.discordapp.com/attachments/1245870773919420456/1258971208855060511/MantaFlip.jpg?ex=6689fbce&is=6688aa4e&hm=03cd035c16fd85bf089e59bf90c9022b5db90b3d49acd7cf309d793b2d6142f2&");
            embed.setTitle("Current Session Statistics");
            embed.addField("Auctions sold", String.valueOf(sold_items.size()), true);
            embed.addField("Auctions bought", String.valueOf(bought_items.size()), true);
            embed.setTimestamp(event.getTimeCreated());
            embed.setImage("attachment://image.png");
            embed.setFooter("Made by Calamari", "https://cdn.discordapp.com/attachments/1242759092645138474/1261441636123152599/Untitled-1.png?ex=6692f892&is=6691a712&hm=7eccfbe0b4b3d9f7c6a0c4feb9726de761986b1df89c9696826be1b8cfbc86ea&");
            FileUpload screenshot = FileUpload.fromData(new File(Objects.requireNonNull(BotUtils.takeScreenShot())), "image.png");
            event.getHook().editOriginalAttachments(screenshot).queue();
            event.getHook().editOriginalEmbeds(embed.build()).queue();

            if (sold_items.size() > 0) {
                EmbedBuilder sold = new EmbedBuilder();
                sold.setAuthor("MantaFlip", "https://cdn.discordapp.com/attachments/1245870773919420456/1258971208855060511/MantaFlip.jpg?ex=6689fbce&is=6688aa4e&hm=03cd035c16fd85bf089e59bf90c9022b5db90b3d49acd7cf309d793b2d6142f2&");
                sold.setTitle("Sold Items");
                sold.setFooter("Made by Calamari", "https://cdn.discordapp.com/attachments/1242759092645138474/1261441636123152599/Untitled-1.png?ex=6692f892&is=6691a712&hm=7eccfbe0b4b3d9f7c6a0c4feb9726de761986b1df89c9696826be1b8cfbc86ea&");
                sold.setTimestamp(event.getTimeCreated());
                for (HashMap<String, String> sold_item : sold_items) {
                    sold.addField(sold_item.get("item"), "Price: " + sold_item.get("price"), true);
                }
                event.getHook().getInteraction().getMessageChannel().sendMessageEmbeds(sold.build()).queue();
            }
            if (bought_items.size() > 0) {
                EmbedBuilder bought = new EmbedBuilder();
                bought.setAuthor("MantaFlip", "https://cdn.discordapp.com/attachments/1245870773919420456/1258971208855060511/MantaFlip.jpg?ex=6689fbce&is=6688aa4e&hm=03cd035c16fd85bf089e59bf90c9022b5db90b3d49acd7cf309d793b2d6142f2&");
                bought.setTitle("Bought Items");
                bought.setFooter("Made by Calamari", "https://cdn.discordapp.com/attachments/1242759092645138474/1261441636123152599/Untitled-1.png?ex=6692f892&is=6691a712&hm=7eccfbe0b4b3d9f7c6a0c4feb9726de761986b1df89c9696826be1b8cfbc86ea&");
                bought.setTimestamp(event.getTimeCreated());
                for (HashMap<String, String> bought_item : bought_items) {
                    bought.addField(bought_item.get("name"), "Price: " + bought_item.get("price"), true);
                }
                event.getHook().getInteraction().getMessageChannel().sendMessageEmbeds(bought.build()).queue();
            }

        }
        if (event.getName().equals("screenshot")) {
            event.deferReply().queue();
            FileUpload screenshot = FileUpload.fromData(new File(Objects.requireNonNull(BotUtils.takeScreenShot())), "image.png");
            event.getHook().editOriginalAttachments(screenshot).queue();
        }
        if (event.getName().equals("profit")) {
            event.deferReply().queue();
            File profitChart = ChartUtils.createProfitGraph(MantaFlip.timeIntervals, MantaFlip.profitValues);
            FileUpload chartUpload = FileUpload.fromData(profitChart, "profit_chart.png");
            event.getHook().editOriginalAttachments(chartUpload).queue();
        }
        if (event.getName().equals("enable")) {
            Instant startTime = Instant.now();
            String type = Objects.requireNonNull(event.getOption("type")).getAsString();
            if (type.equals("claimer")) {
                event.deferReply().queue();
                if (!ToggleClaim)
                {
                    if (!PacketListener.relisting) {
                        ToggleClaim = true;





                        EmbedBuilder embed = new EmbedBuilder();
                        embed.setTitle("Enabled");
                        embed.setTimestamp(Instant.now());
                        embed.setDescription("Claimer has been enabled.");
                        embed.setThumbnail("https://minotar.net/helm/" + Minecraft.getMinecraft().getSession().getUsername() + "/600.png");
                        embed.setFooter("Remote Session • MantaFlip ", "https://cdn.discordapp.com/attachments/1242759092645138474/1261447373503205506/Untitled-2.png?ex=6692fdea&is=6691ac6a&hm=e0b1e050dfd4a457c903bbba8605745d4dd013a78848d98a4f180608f462849c&");
                        embed.setColor(new Color(0x1ED55F));  // Set the color to green, you can use any color

// Send the embed
                        event.getHook().sendMessageEmbeds(embed.build()).queue();
                    } else {
                        EmbedBuilder embed = new EmbedBuilder();
                        embed.setTitle("Action Delayed");
                        embed.setTimestamp(Instant.now());
                        embed.setDescription("Currently relisting, wait a second and retry");
                        embed.setThumbnail("https://minotar.net/helm/" + Minecraft.getMinecraft().getSession().getUsername() + "/600.png");
                        embed.setFooter("Remote Session • MantaFlip ", "https://cdn.discordapp.com/attachments/1242759092645138474/1261447373503205506/Untitled-2.png?ex=6692fdea&is=6691ac6a&hm=e0b1e050dfd4a457c903bbba8605745d4dd013a78848d98a4f180608f462849c&");
                        embed.setColor(new Color(0xFFA500)); // Using orange color to indicate a warning or delay

// Send the embed
                        event.getHook().sendMessageEmbeds(embed.build()).queue();
                    }
                }
                else
                {
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setTitle("Already Enabled");
                    embed.setTimestamp(Instant.now());
                    embed.setDescription("The claimer is already enabled. No further action is required.");
                    embed.setThumbnail("https://minotar.net/helm/" + Minecraft.getMinecraft().getSession().getUsername() + "/600.png");
                    embed.setFooter("Remote Session • MantaFlip ", "https://cdn.discordapp.com/attachments/1242759092645138474/1261447373503205506/Untitled-2.png?ex=6692fdea&is=6691ac6a&hm=e0b1e050dfd4a457c903bbba8605745d4dd013a78848d98a4f180608f462849c&");
                    embed.setColor(new Color(0x1ED55F));
                }

            }
            if (type.equals("macro")) {
                event.deferReply().queue();
                if (!shouldRun)
                {
                    if (!PacketListener.relisting) {
                        shouldRun = true;
                        EmbedBuilder embed = new EmbedBuilder();
                        embed.setTitle("Enabled");
                        embed.setTimestamp(Instant.now());
                        embed.setDescription("Macro enabled.");
                        embed.setThumbnail("https://minotar.net/helm/" + Minecraft.getMinecraft().getSession().getUsername() + "/600.png");
                        embed.setFooter("Remote Session • MantaFlip ", "https://cdn.discordapp.com/attachments/1242759092645138474/1261447373503205506/Untitled-2.png?ex=6692fdea&is=6691ac6a&hm=e0b1e050dfd4a457c903bbba8605745d4dd013a78848d98a4f180608f462849c&");
                        embed.setColor(new Color(0x1ED55F));  // Set the color to green, you can use any color
                        event.getHook().sendMessageEmbeds(embed.build()).queue();
                    } else {
                        EmbedBuilder embed = new EmbedBuilder();
                        embed.setTitle("Action Delayed");
                        embed.setTimestamp(Instant.now());
                        embed.setDescription("Currently relisting, wait a second and retry");
                        embed.setThumbnail("https://minotar.net/helm/" + Minecraft.getMinecraft().getSession().getUsername() + "/600.png");
                        embed.setFooter("Remote Session • MantaFlip ", "https://cdn.discordapp.com/attachments/1242759092645138474/1261447373503205506/Untitled-2.png?ex=6692fdea&is=6691ac6a&hm=e0b1e050dfd4a457c903bbba8605745d4dd013a78848d98a4f180608f462849c&");
                        embed.setColor(new Color(0xFFA500)); // Using orange color to indicate a warning or delay

// Send the embed
                        event.getHook().sendMessageEmbeds(embed.build()).queue();
                    }

                }
                else
                {
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setTitle("Already Enabled");
                    embed.setTimestamp(Instant.now());
                    embed.setDescription("The macro is already enabled. No further action is required.");
                    embed.setThumbnail("https://minotar.net/helm/" + Minecraft.getMinecraft().getSession().getUsername() + "/600.png");
                    embed.setFooter("Remote Session • MantaFlip ", "https://cdn.discordapp.com/attachments/1242759092645138474/1261447373503205506/Untitled-2.png?ex=6692fdea&is=6691ac6a&hm=e0b1e050dfd4a457c903bbba8605745d4dd013a78848d98a4f180608f462849c&");
                    embed.setColor(new Color(0x1ED55F));
                }

            }
            if (type.equals("relister")) {
                event.deferReply().queue();
                if (!ToggleRelist)
                {
                    if (!PacketListener.relisting) {
                        ToggleRelist = true;
                        EmbedBuilder embed = new EmbedBuilder();
                        embed.setTitle("Enabled");
                        embed.setTimestamp(Instant.now());
                        embed.setDescription("Relister has been enabled.");
                        embed.setThumbnail("https://minotar.net/helm/" + Minecraft.getMinecraft().getSession().getUsername() + "/600.png");
                        embed.setFooter("Remote Session • MantaFlip ", "https://cdn.discordapp.com/attachments/1242759092645138474/1261447373503205506/Untitled-2.png?ex=6692fdea&is=6691ac6a&hm=e0b1e050dfd4a457c903bbba8605745d4dd013a78848d98a4f180608f462849c&");
                        embed.setColor(new Color(0x1ED55F)); // Using green color to indicate the feature is enabled

                        event.getHook().sendMessageEmbeds(embed.build()).queue();
                    } else {
                        EmbedBuilder embed = new EmbedBuilder();
                        embed.setTitle("Action Delayed");
                        embed.setTimestamp(Instant.now());
                        embed.setDescription("Currently relisting, wait a second and retry");
                        embed.setThumbnail("https://minotar.net/helm/" + Minecraft.getMinecraft().getSession().getUsername() + "/600.png");
                        embed.setFooter("Remote Session • MantaFlip ", "https://cdn.discordapp.com/attachments/1242759092645138474/1261447373503205506/Untitled-2.png?ex=6692fdea&is=6691ac6a&hm=e0b1e050dfd4a457c903bbba8605745d4dd013a78848d98a4f180608f462849c&");
                        embed.setColor(new Color(0xFFA500)); // Using orange color to indicate a warning or delay
                    }

                }
                else
                {
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setTitle("Already Enabled");
                    embed.setTimestamp(Instant.now());
                    embed.setDescription("The relister is already enabled. No further action is required.");
                    embed.setThumbnail("https://minotar.net/helm/" + Minecraft.getMinecraft().getSession().getUsername() + "/600.png");
                    embed.setFooter("Remote Session • MantaFlip ", "https://cdn.discordapp.com/attachments/1242759092645138474/1261447373503205506/Untitled-2.png?ex=6692fdea&is=6691ac6a&hm=e0b1e050dfd4a457c903bbba8605745d4dd013a78848d98a4f180608f462849c&");
                    embed.setColor(new Color(0x1ED55F));

                }

            }
        }
        if (event.getName().equals("disable")) {
            Instant startTime = Instant.now();
            String type = Objects.requireNonNull(event.getOption("type")).getAsString();
            if (type.equals("claimer")) {
                event.deferReply().queue();
                if (ToggleClaim)
                {
                    if (!PacketListener.relisting) {
                        ToggleClaim = false;





                        EmbedBuilder embed = new EmbedBuilder();
                        embed.setTitle("Disabled");
                        embed.setTimestamp(Instant.now());
                        embed.setDescription("Claimer has been disabled.");
                        embed.setThumbnail("https://minotar.net/helm/" + Minecraft.getMinecraft().getSession().getUsername() + "/600.png");
                        embed.setFooter("Remote Session • MantaFlip ", "https://cdn.discordapp.com/attachments/1242759092645138474/1261447373503205506/Untitled-2.png?ex=6692fdea&is=6691ac6a&hm=e0b1e050dfd4a457c903bbba8605745d4dd013a78848d98a4f180608f462849c&");
                        embed.setColor(new Color(0xC31E42));  // Set the color to green, you can use any color

// Send the embed
                        event.getHook().sendMessageEmbeds(embed.build()).queue();
                    } else {
                        EmbedBuilder embed = new EmbedBuilder();
                        embed.setTitle("Action Delayed");
                        embed.setTimestamp(Instant.now());
                        embed.setDescription("Currently relisting, wait a second and retry");
                        embed.setThumbnail("https://minotar.net/helm/" + Minecraft.getMinecraft().getSession().getUsername() + "/600.png");
                        embed.setFooter("Remote Session • MantaFlip ", "https://cdn.discordapp.com/attachments/1242759092645138474/1261447373503205506/Untitled-2.png?ex=6692fdea&is=6691ac6a&hm=e0b1e050dfd4a457c903bbba8605745d4dd013a78848d98a4f180608f462849c&");
                        embed.setColor(new Color(0xFFA500)); // Using orange color to indicate a warning or delay

// Send the embed
                        event.getHook().sendMessageEmbeds(embed.build()).queue();
                    }

                }
                else
                {
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setTitle("Already Disabled");
                    embed.setTimestamp(Instant.now());
                    embed.setDescription("The claimer is already disabled. No further action is required.");
                    embed.setThumbnail("https://minotar.net/helm/" + Minecraft.getMinecraft().getSession().getUsername() + "/600.png");
                    embed.setFooter("Remote Session • MantaFlip ", "https://cdn.discordapp.com/attachments/1242759092645138474/1261447373503205506/Untitled-2.png?ex=6692fdea&is=6691ac6a&hm=e0b1e050dfd4a457c903bbba8605745d4dd013a78848d98a4f180608f462849c&");
                    embed.setColor(new Color(0xC31E42));
                }

            }
            if (type.equals("macro")) {
                event.deferReply().queue();
                if (shouldRun)
                {
                    if (!PacketListener.relisting) {
                        shouldRun = false;
                        EmbedBuilder embed = new EmbedBuilder();
                        embed.setTitle("Disabled");
                        embed.setTimestamp(Instant.now());
                        embed.setDescription("Macro disabled.");
                        embed.setThumbnail("https://minotar.net/helm/" + Minecraft.getMinecraft().getSession().getUsername() + "/600.png");
                        embed.setFooter("Remote Session • MantaFlip ", "https://cdn.discordapp.com/attachments/1242759092645138474/1261447373503205506/Untitled-2.png?ex=6692fdea&is=6691ac6a&hm=e0b1e050dfd4a457c903bbba8605745d4dd013a78848d98a4f180608f462849c&");
                        embed.setColor(new Color(0xC31E42));  // Set the color to green, you can use any color
                        event.getHook().sendMessageEmbeds(embed.build()).queue();
                    } else {
                        EmbedBuilder embed = new EmbedBuilder();
                        embed.setTitle("Action Delayed");
                        embed.setTimestamp(Instant.now());
                        embed.setDescription("Currently relisting, wait a second and retry");
                        embed.setThumbnail("https://minotar.net/helm/" + Minecraft.getMinecraft().getSession().getUsername() + "/600.png");
                        embed.setFooter("Remote Session • MantaFlip ", "https://cdn.discordapp.com/attachments/1242759092645138474/1261447373503205506/Untitled-2.png?ex=6692fdea&is=6691ac6a&hm=e0b1e050dfd4a457c903bbba8605745d4dd013a78848d98a4f180608f462849c&");
                        embed.setColor(new Color(0xFFA500)); // Using orange color to indicate a warning or delay

// Send the embed
                        event.getHook().sendMessageEmbeds(embed.build()).queue();
                    }

                }
                else
                {
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setTitle("Already Disabled");
                    embed.setTimestamp(Instant.now());
                    embed.setDescription("The macro is already disabled. No further action is required.");
                    embed.setThumbnail("https://minotar.net/helm/" + Minecraft.getMinecraft().getSession().getUsername() + "/600.png");
                    embed.setFooter("Remote Session • MantaFlip ", "https://cdn.discordapp.com/attachments/1242759092645138474/1261447373503205506/Untitled-2.png?ex=6692fdea&is=6691ac6a&hm=e0b1e050dfd4a457c903bbba8605745d4dd013a78848d98a4f180608f462849c&");
                    embed.setColor(new Color(0xC31E42));
                }

            }
            if (type.equals("relister")) {
                event.deferReply().queue();
                if (ToggleRelist)
                {
                    if (!PacketListener.relisting) {
                        ToggleRelist = false;
                        EmbedBuilder embed = new EmbedBuilder();
                        embed.setTitle("Disabled");
                        embed.setTimestamp(Instant.now());
                        embed.setDescription("Relister has been disabled.");
                        embed.setThumbnail("https://minotar.net/helm/" + Minecraft.getMinecraft().getSession().getUsername() + "/600.png");
                        embed.setFooter("Remote Session • MantaFlip ", "https://cdn.discordapp.com/attachments/1242759092645138474/1261447373503205506/Untitled-2.png?ex=6692fdea&is=6691ac6a&hm=e0b1e050dfd4a457c903bbba8605745d4dd013a78848d98a4f180608f462849c&");
                        embed.setColor(new Color(0xC31E42)); // Using green color to indicate the feature is enabled

                        event.getHook().sendMessageEmbeds(embed.build()).queue();
                    } else {
                        EmbedBuilder embed = new EmbedBuilder();
                        embed.setTitle("Action Delayed");
                        embed.setTimestamp(Instant.now());
                        embed.setDescription("Currently relisting, wait a second and retry");
                        embed.setThumbnail("https://minotar.net/helm/" + Minecraft.getMinecraft().getSession().getUsername() + "/600.png");
                        embed.setFooter("Remote Session • MantaFlip ", "https://cdn.discordapp.com/attachments/1242759092645138474/1261447373503205506/Untitled-2.png?ex=6692fdea&is=6691ac6a&hm=e0b1e050dfd4a457c903bbba8605745d4dd013a78848d98a4f180608f462849c&");
                        embed.setColor(new Color(0xFFA500)); // Using orange color to indicate a warning or delay
                    }

                }
                else
                {
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setTitle("Already Disabled");
                    embed.setTimestamp(Instant.now());
                    embed.setDescription("The relister is already disabled. No further action is required.");
                    embed.setThumbnail("https://minotar.net/helm/" + Minecraft.getMinecraft().getSession().getUsername() + "/600.png");
                    embed.setFooter("Remote Session • MantaFlip ", "https://cdn.discordapp.com/attachments/1242759092645138474/1261447373503205506/Untitled-2.png?ex=6692fdea&is=6691ac6a&hm=e0b1e050dfd4a457c903bbba8605745d4dd013a78848d98a4f180608f462849c&");
                    embed.setColor(new Color(0xC31E42));

                }

            }
        }
        if (event.getName().equals("command")) {
            String command = Objects.requireNonNull(event.getOption("command")).getAsString();
            Utils.sendServerMessage("/" + command);
            event.reply("Command sent").queue();
        }
        if (event.getName().equals("disconnect")) {
            Minecraft.getMinecraft().theWorld.sendQuittingDisconnectingPacket();
            event.reply("Disconnected").queue();
        }
    }
}