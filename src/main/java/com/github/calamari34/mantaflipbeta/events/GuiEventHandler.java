package com.github.calamari34.mantaflipbeta.events;

import com.github.calamari34.mantaflipbeta.MantaFlip;
import com.github.calamari34.mantaflipbeta.features.Cofl.QueueItem;
import com.github.calamari34.mantaflipbeta.features.PacketListener;
import com.github.calamari34.mantaflipbeta.utils.InventoryUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static com.github.calamari34.mantaflipbeta.MantaFlip.cofl;
import static com.github.calamari34.mantaflipbeta.config.AHConfig.BED_SPAM;
import static com.github.calamari34.mantaflipbeta.config.AHConfig.BED_SPAM_DELAY;
import static com.github.calamari34.mantaflipbeta.features.PacketListener.*;
import static com.github.calamari34.mantaflipbeta.utils.InventoryUtils.getInventoryName;
import static com.github.calamari34.mantaflipbeta.utils.Utils.sendMessage;
import static org.apache.http.conn.params.ConnManagerParams.setTimeout;

public class GuiEventHandler {
    static Thread bedThread = null;
    static boolean bedStarted = false;
    @SubscribeEvent
    public void onInventoryRendering(GuiScreenEvent.DrawScreenEvent.Post post) throws InterruptedException {
        if ((post.gui instanceof GuiChest)) {
            ContainerChest chest = (ContainerChest) ((GuiChest) post.gui).inventorySlots;
            if (chest != null) {
                String name = chest.getLowerChestInventory().getName();
                if (name.equals("BIN Auction View")) {
                    PacketListener.auctionHouseOpenTime = System.currentTimeMillis();
                    ItemStack stack = chest.getSlot(31).getStack();
                    if (stack != null) {
                        if (Items.feather != stack.getItem()) {
                            if (Items.potato == stack.getItem()) {

                                PacketListener.isbBed = "false";
                                MantaFlip.mc.thePlayer.closeScreen();
                            } else if (Items.poisonous_potato == stack.getItem()) {

                                PacketListener.isbBed = "false";
                                MantaFlip.mc.thePlayer.closeScreen();
                            } else if (Items.bed == stack.getItem()) {
                                sendMessage("Bed found: closing GUI");
                                if (BED_SPAM) {
                                    bedStarted = true;
                                    bedThread = new Thread(() -> {
                                        int loopInt = 0;
                                        try {
                                            while (loopInt < 100) {
                                                loopInt++;
                                                if (chest.getLowerChestInventory().getName().contains("BIN Auction View")) {
                                                    clickWindowSlot(31);

                                                    Thread.sleep(BED_SPAM_DELAY);
                                                }
                                            }
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    });
                                    bedThread.start();

                                } else
                                    MantaFlip.mc.thePlayer.closeScreen();

                            } else if (Items.gold_nugget == stack.getItem()) {

                                clickWindowSlot(31);
                                PacketListener.isbBed = "false";
                            }
                        }

                    }

                } else if (name.equals("Confirm Purchase")) {
                    final boolean[] itemClicked = {false};

                    if (!itemClicked[0]) {
                        ItemStack stack = chest.getSlot(11).getStack();
                        if (stack != null) {
                            Item item = Item.getItemById(159);
                            if (stack.getItem() == item) {
                                clickWindowSlot(11);
                                for (int i = 1; i < 11; i++) {
                                    if (!BED_SPAM) i = 11;
                                    Thread.sleep(30);
                                    if (name != null) {
                                        clickWindowSlot(11);
                                    } else {
                                        i = 11;
                                    }
                                }
                                MantaFlip.mc.thePlayer.closeScreen();
                                itemClicked[0] = true;
                            }
                        }
                    }
                }
            }
        }else {
            if (!cofl.queue.isEmpty() && !cofl.queue.isRunning() && (!relisting)){
                cofl.queue.setRunning(true);
                QueueItem item = cofl.queue.get();

                item.openAuction();
            }
        }
    }







//    @SubscribeEvent
//    public void onGuiOpen(GuiOpenEvent event) {
//        if (event.gui instanceof GuiContainer) {
//
//            GuiContainer guiContainer = (GuiContainer) event.gui;
//            String windowName = getInventoryName(guiContainer);
//
//
//            if ("BIN Auction View".equals(windowName)) {
//                sendMessage("BIN Auction View opened");
////                PacketListener.handleBinAuctionView(guiContainer);
//
//            } else if ("Confirm Purchase".equals(windowName)) {
////                PacketListener.handleConfirmPurchase(guiContainer);
//                sendMessage("Confirm Purchase opened");
//            }
//        }
//    }

    public static String getInventoryName(GuiContainer guiContainer) {
        if (guiContainer instanceof GuiChest) {
            ContainerChest chest = (ContainerChest) guiContainer.inventorySlots;
            IInventory inv = chest.getLowerChestInventory();
            return inv.hasCustomName() ? inv.getName() : null;
        }
        return null;
    }

}