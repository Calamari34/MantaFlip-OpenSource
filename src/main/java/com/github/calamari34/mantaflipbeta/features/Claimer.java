package com.github.calamari34.mantaflipbeta.features;

import com.github.calamari34.mantaflipbeta.MantaFlip;
import com.github.calamari34.mantaflipbeta.utils.DiscordWebhook;
import com.github.calamari34.mantaflipbeta.utils.InventoryUtils;
import com.github.calamari34.mantaflipbeta.utils.ScoreboardUtils;
import com.github.calamari34.mantaflipbeta.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import org.lwjgl.Sys;

import java.awt.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.github.calamari34.mantaflipbeta.config.AHConfig.SEND_MESSAGE;
import static com.github.calamari34.mantaflipbeta.utils.InventoryUtils.clickWindow2;
import static com.github.calamari34.mantaflipbeta.utils.Utils.sendMessage;


public class Claimer {
    public static final List<Integer> toClaim = new ArrayList<>();

    public static void open() throws InterruptedException {
        System.out.println("Opening AH");
        Minecraft.getMinecraft().thePlayer.sendChatMessage("/ah");
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.schedule(() -> {
            sendMessage("Current screen = " + InventoryUtils.getInventoryName());
            if (InventoryUtils.inventoryNameContains("Auction House")) {
                InventoryUtils.clickOpenContainerSlot(15);

                executorService.schedule(() -> {
                    claim();
                }, 3, TimeUnit.SECONDS);
            }



            System.out.println("Clicked on slot 15");
        }, 3, TimeUnit.SECONDS);


    }

    public static void claim() {
        if (InventoryUtils.inventoryNameContains("Manage Auctions")) {
            long claiming = MantaFlip.mc.thePlayer.openContainer.inventorySlots
                    .stream()
                    .filter(slot -> slot.getStack() != null)
                    .filter(slot -> InventoryUtils.getLore(slot.getStack()) != null)
                    .filter(slot -> ScoreboardUtils.cleanSB(Objects.requireNonNull(InventoryUtils.getLore(slot.getStack())).toString()).contains("Status: Sold")).count();
            System.out.println(claiming + " items to claim");

            if (claiming == 0) {
                toClaim.clear();
                Minecraft.getMinecraft().displayGuiScreen(null);
                return;

            }
            for (int i = 10; i <= 25; i++) {
                if (i == 17 || i == 18) continue;
                ItemStack is = MantaFlip.mc.thePlayer.openContainer.getSlot(i).getStack();
                if (is == null) continue;
                Item item = is.getItem();
                if (item == Items.golden_horse_armor || item == Items.arrow || item == Item.getItemFromBlock(Blocks.hopper) || item == Item.getItemFromBlock(Blocks.stained_glass_pane) || item == Item.getItemFromBlock(Blocks.cauldron))
                    continue;
                NBTTagList list = InventoryUtils.getLore(is);
                if (list != null) {
                    System.out.println("Item NBT: " + ScoreboardUtils.cleanSB(list.toString()));
                    if (ScoreboardUtils.cleanSB(list.toString()).contains("Status: Sold")) {
                        toClaim.add(i);
                        System.out.println("Adding item to claim list: " + i);
                        try {
                            Thread.sleep(400 + new Random().nextInt(100));
                            clickWindow2(MantaFlip.mc.thePlayer.openContainer.windowId, i);
                            Thread.sleep(500 + new Random().nextInt(100));
                            clickWindow2(MantaFlip.mc.thePlayer.openContainer.windowId, 31);
                            if (SEND_MESSAGE) {
                                try {
                                    NumberFormat format = NumberFormat.getInstance();
                                    DiscordWebhook webhook = new DiscordWebhook(MantaFlip.configHandler.getString("Webhook"));
                                    webhook.setUsername("MantaFlip");
                                    DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject();
                                    embed.setTitle("Just claimed an item!")
                                            .setFooter("Purse: " + format.format(Utils.getPurse()))
                                            .addField("Item:", ScoreboardUtils.cleanSB(is.getDisplayName()), true)
                                            .setColor(new Color(0x055F9A));
                                    webhook.addEmbed(embed);
                                    webhook.execute();

                                } catch (Exception e) {
                                    e.printStackTrace();
                                    System.out.println("Failed to send webhook");
                                }
                            }
                            Thread.sleep(200 + new Random().nextInt(100));
                            open();
                            break;
                        } catch (InterruptedException ignore) {
                        }
                    }
                }
            }

        }
    }
}
