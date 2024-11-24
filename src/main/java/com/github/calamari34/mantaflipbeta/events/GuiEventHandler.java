package com.github.calamari34.mantaflipbeta.events;

import com.github.calamari34.mantaflipbeta.MantaFlip;
import com.github.calamari34.mantaflipbeta.features.PacketListener;
import com.github.calamari34.mantaflipbeta.utils.InventoryUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.github.calamari34.mantaflipbeta.MantaFlip.cofl;
import static com.github.calamari34.mantaflipbeta.config.AHConfig.BED_SPAM_DELAY;
import static com.github.calamari34.mantaflipbeta.features.PacketListener.clickWindowSlot;
import static com.github.calamari34.mantaflipbeta.utils.Utils.sendMessage;
import static com.github.calamari34.mantaflipbeta.utils.Utils.setTimeout;


public class GuiEventHandler {
    private int lastAuctionBought = 0;
    private static boolean bedStarted = false;

    public static Thread bedThread = null;
    public static Thread nuggetThread = null;
    private static ScheduledExecutorService scheduler;
    public static long auctionHouseOpenTime = 0;



    public static void handleBuyFinished() {
        bedStarted = false;
        if (bedThread != null && bedThread.isAlive()) {
            try {
                bedThread.interrupt();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        MantaFlip.getInstance().getQueue().setRunning(false);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onInventoryRendering(GuiScreenEvent.DrawScreenEvent.Post post) {
        if ((post.gui instanceof GuiChest)) {
            ContainerChest chest = (ContainerChest) ((GuiChest) post.gui).inventorySlots;
            if (chest != null) {
                String name = chest.getLowerChestInventory().getName();
                if (name.contains("BIN Auction View")) {

                    ItemStack stack = chest.getSlot(31).getStack();

                    if (stack != null) {
                        if (Items.feather != stack.getItem()) {
                            PacketListener.isbBed = "false";
                            if (Items.potato == stack.getItem()) {
                                new Thread(() -> {
                                    sendMessage("Someone bought the auction already, skipping...");

                                    try {
                                        Thread.sleep(50);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    Minecraft.getMinecraft().thePlayer.closeScreen();
                                }).start();
                            } else if (Items.bed == stack.getItem() && !bedStarted) {
                                PacketListener.isbBed = "true";
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
                                setTimeout(GuiEventHandler::handleBuyFinished, 750);
                            } else if (Items.gold_nugget == stack.getItem() || Item.getItemFromBlock(Blocks.gold_block) == stack.getItem()) {
                                clickWindowSlot(31);

                            }
                        }
                    }
                } else if (name.contains("Confirm Purchase")) {
                    if (chest.windowId != this.lastAuctionBought) {
                        clickWindowSlot(11);
                        this.lastAuctionBought = chest.windowId;
                        MantaFlip.mc.thePlayer.closeScreen();
                    }
                }
            }
        }
    }

    private void handleBinAuctionView(GuiContainer guiContainer, String name) {

        long purchaseAt = System.currentTimeMillis();
        long waitTime = 15;

        long currentTime = System.currentTimeMillis();
        long ending = purchaseAt;

        AtomicInteger totalClicks = new AtomicInteger(0);
        int slot = 31;
        final boolean[] isBed = {false};


        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            try {
                // Ensure this is run on the main thread
                Minecraft.getMinecraft().addScheduledTask(() -> {
                    try {
                        if (!"BIN Auction View".equals(name)) {
                            cofl.customQueue.setRunning(false);
                            sendMessage("Current screen is not 'BIN Auction View', stopping scheduled action.");
                            scheduler.shutdown();
                            return;
                        }
                        PacketListener.isbBed = "false";

                        ItemStack itemStack = InventoryUtils.getStackInOpenContainerSlot(guiContainer, slot);
                        if (itemStack != null) {
                            Item item = itemStack.getItem();
                            if (item == Items.gold_nugget) {



                                clickWindowSlot(slot);
                                scheduler.shutdown();
                            } else if (item == Items.potato) {
                                cofl.customQueue.setRunning(false);
                                MantaFlip.mc.thePlayer.closeScreen();

                                scheduler.shutdown();
                            } else if (item == Items.bed) {
                                sendMessage("beds temporarily disabled");
                                MantaFlip.mc.thePlayer.closeScreen();
                                scheduler.shutdown();
//
                        }
                    }
                    } catch (Exception e) {
                        e.printStackTrace();
                        scheduler.shutdown();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                scheduler.shutdown();
            }
        }, 0, 10, TimeUnit.MILLISECONDS);
    }

    private void handleConfirmPurchase(GuiContainer guiContainer, String name) {
        int slot = 11;
        final boolean[] itemClicked = {false};

        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            try {
                Minecraft.getMinecraft().addScheduledTask(() -> {
                    try {
                        if (!itemClicked[0]) {
                            ItemStack itemStack = InventoryUtils.getStackInOpenContainerSlot(guiContainer, slot);
                            if (itemStack != null) {
                                Item item = Item.getItemById(159);
                                if (itemStack.getItem() == item) {
                                    clickWindowSlot(11);
                                    cofl.customQueue.setRunning(false);
                                    MantaFlip.mc.thePlayer.closeScreen();

                                    itemClicked[0] = true;
                                    scheduler.shutdown();  // Ensure the scheduler is shutdown after clicking the item
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        scheduler.shutdown();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                scheduler.shutdown();
            }
        }, 0, 10, TimeUnit.MILLISECONDS);
    }

    public static String getInventoryName(GuiContainer guiContainer) {
        if (guiContainer instanceof GuiChest) {
            ContainerChest chest = (ContainerChest) guiContainer.inventorySlots;
            IInventory inv = chest.getLowerChestInventory();
            return inv.hasCustomName() ? inv.getName() : null;
        }
        return null;
    }
}
