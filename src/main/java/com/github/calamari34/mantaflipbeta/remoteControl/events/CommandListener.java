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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

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
            embed.setFooter("Made by Calamari", "https://cdn.discordapp.com/attachments/1242759092645138474/1258977262154420224/image.jpg?ex=668a0171&is=6688aff1&hm=3fd696a34a4d19ed1a9906a74fdf15ad0965505a615bb19fbcd414656b00e9ad&");
            FileUpload screenshot = FileUpload.fromData(new File(Objects.requireNonNull(BotUtils.takeScreenShot())), "image.png");
            event.getHook().editOriginalAttachments(screenshot).queue();
            event.getHook().editOriginalEmbeds(embed.build()).queue();

            if (sold_items.size() > 0) {
                EmbedBuilder sold = new EmbedBuilder();
                sold.setAuthor("MantaFlip", "https://cdn.discordapp.com/attachments/1245870773919420456/1258971208855060511/MantaFlip.jpg?ex=6689fbce&is=6688aa4e&hm=03cd035c16fd85bf089e59bf90c9022b5db90b3d49acd7cf309d793b2d6142f2&");
                sold.setTitle("Sold Items");
                sold.setFooter("Made by Calamari", "https://cdn.discordapp.com/attachments/1242759092645138474/1258977262154420224/image.jpg?ex=668a0171&is=6688aff1&hm=3fd696a34a4d19ed1a9906a74fdf15ad0965505a615bb19fbcd414656b00e9ad&");
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
                bought.setFooter("Made by Calamari", "https://cdn.discordapp.com/attachments/1242759092645138474/1258977262154420224/image.jpg?ex=668a0171&is=6688aff1&hm=3fd696a34a4d19ed1a9906a74fdf15ad0965505a615bb19fbcd414656b00e9ad&");
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
            String type = Objects.requireNonNull(event.getOption("type")).getAsString();
            if (type.equals("claimer")) {
                if (!PacketListener.relisting) {
                    MantaFlip.ToggleClaim = true;
                    event.reply("Claimer enabled").queue();
                } else {
                    event.getHook().editOriginal("Relister is currently running, wait a second and retry").queue();
                }
            }
            if (type.equals("macro")) {
                if (!PacketListener.relisting) {
                    MantaFlip.shouldRun = true;
                    event.reply("Macro enabled").queue();
                } else {
                    event.reply("Macro is currently running, wait a second and retry").queue();
                }
            }
            if (type.equals("relister")) {
                if (!PacketListener.relisting) {
                    MantaFlip.ToggleRelist = true;
                    event.reply("Relister enabled").queue();
                } else {
                    event.reply("Claimer is currently running, wait a second and retry").queue();
                }
            }
        }
        if (event.getName().equals("disable")) {
            String type = Objects.requireNonNull(event.getOption("type")).getAsString();
            if (type.equals("claimer")) {
                if (!PacketListener.relisting) {
                    MantaFlip.ToggleClaim = false;
                    event.reply("Claimer disabled").queue();
                } else {
                    event.getHook().editOriginal("Relister is currently running, wait a second and retry").queue();
                }
            }
            if (type.equals("macro")) {
                if (!PacketListener.relisting) {
                    MantaFlip.shouldRun = false;
                    event.reply("Macro disabled").queue();
                } else {
                    event.reply("Macro is currently running, wait a second and retry").queue();
                }
            }
            if (type.equals("relister")) {
                if (!PacketListener.relisting) {
                    MantaFlip.ToggleRelist = false;
                    event.reply("Relister disabled").queue();
                } else {
                    event.reply("Claimer is currently running, wait a second and retry").queue();
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