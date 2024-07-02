package com.github.calamari34.mantaflipbeta.features;

import com.github.calamari34.mantaflipbeta.MantaFlip;
import com.github.calamari34.mantaflipbeta.utils.InventoryUtils;
import com.github.calamari34.mantaflipbeta.utils.ReflectionUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.init.Items;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.play.client.C12PacketUpdateSign;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.github.calamari34.mantaflipbeta.utils.Utils.sendMessage;


public class PacketListener {

    private static ScheduledExecutorService scheduler;
    private static boolean slotClicked = false;
    public static Boolean relisting = false;
    public static long endTime;
    public static String isbBed;
    public static long startTime;
    private ScheduledExecutorService windowTitleChecker;

    private boolean guiNeedsProcessing = false;
    private GuiChest pendingGuiChest = null;

    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event) {
        if (event.gui == null) return;

        if (event.gui instanceof GuiChest) {
            pendingGuiChest = (GuiChest) event.gui;
            guiNeedsProcessing = true;
            processGuiChest(pendingGuiChest); // Immediate processing
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) return;

        if (guiNeedsProcessing && pendingGuiChest != null) {
            processGuiChest(pendingGuiChest); // Verification processing
            pendingGuiChest = null;
            guiNeedsProcessing = false;
        }
    }

    private void processGuiChest(GuiChest guiChest) {
        try {


            String windowTitle = getWindowTitle(guiChest);

            if (windowTitle == null) {

                return;
            }

            System.out.println("Current window title: " + windowTitle);

            resetScheduler();

            switch (windowTitle) {
                case "BIN Auction View":
                    handleBinAuctionView(guiChest);
                    break;
                case "Confirm Purchase":
                    handleConfirmPurchase(guiChest);
                    break;
                case "Co-op Auction House":
                case "Auction House":
                    // handleAuctionHouse();
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getWindowTitle(GuiChest guiChest) {
        System.out.println("getWindowTitle called...");
        if (Minecraft.getMinecraft().thePlayer.openContainer instanceof ContainerChest) {
            ContainerChest chest = (ContainerChest) Minecraft.getMinecraft().thePlayer.openContainer;
            IInventory inv = chest.getLowerChestInventory();
            String title = inv.hasCustomName() ? inv.getName() : null;
            System.out.println("Retrieved lower chest inventory: " + inv.getName());
            System.out.println("Retrieved title: " + title);
            return title;
        } else {
            System.out.println("Current container is not a chest.");
            return null;
        }

    }
    private void handleBinAuctionView(GuiChest guiChest) {
        System.out.println("BIN Auction View");

        int slot = 31;
        final boolean[] isBed = {false};
        final int flipActionDelay = 100;
        final int multipleBedClicksDelay = 50;
        scheduler.scheduleAtFixedRate(() -> {
            try {
                ItemStack itemStack = InventoryUtils.getStackInOpenContainerSlot(slot);
                if (itemStack != null) {
                    if (itemStack.getItem() == Items.gold_nugget) {
                        isbBed = "False";
                        // Perform action for gold nugget
                        checkAndClickSlot(guiChest, slot, Items.gold_nugget);
                    } else if (itemStack.getItem() == Items.potato) {
                        isbBed = "False";
                        Minecraft.getMinecraft().displayGuiScreen(null);
                    } else if (itemStack.getItem() == Items.bed) {
                        isbBed = "True";

                        isBed[0] = true;
                        try {
                            Thread.sleep(flipActionDelay);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (multipleBedClicksDelay > 0) {
                            for (int i = 0; i < 3; i++) {
                                clickWindowSlot(slot);
                                try {
                                    Thread.sleep(multipleBedClicksDelay);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {

                            clickWindowSlot(slot);
                        }
                    } else {
                        isbBed = "false";
                        clickWindowSlot(slot);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                scheduler.shutdown();
            }
        }, 0, 10, TimeUnit.MILLISECONDS);
    }

    private void handleConfirmPurchase(GuiChest guiChest) {
        System.out.println("Handle Confirm Purchase");

        int slot = 11;
        final boolean[] itemClicked = {false};

        scheduler.scheduleAtFixedRate(() -> {
            try {
                if (!itemClicked[0]) {
                    ItemStack itemStack = InventoryUtils.getStackInOpenContainerSlot(slot);
                    if (itemStack != null) {
                        Item item = Item.getItemById(159);
                        if (itemStack.getItem() == item) {
                            clickWindowSlot(11);
                            Minecraft.getMinecraft().displayGuiScreen(null);
                            itemClicked[0] = true;
                            endTime = System.currentTimeMillis(); // Ensure endTime is correctly set
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                scheduler.shutdown();
            }
        }, 0, 10, TimeUnit.MILLISECONDS);
    }

    public static void claimAuction(String item) {
        relisting = true;
        System.out.println("Claiming");

        // Send the chat message to open the auction house
        Minecraft.getMinecraft().thePlayer.sendChatMessage("/ah");
        System.out.println("Visit the ah!");

        // Schedule the task to click slot 13 after 2 seconds
        scheduler.schedule(() -> {
            // Check if the current screen is indeed the GUI we expect
            if (Minecraft.getMinecraft().currentScreen instanceof GuiChest) {
                clickWindowSlot(13);
                System.out.println("Clicked slot 13");

                // Schedule the next task to find and click the item after 2 more seconds
                scheduler.schedule(() -> {
                    if (Minecraft.getMinecraft().currentScreen instanceof GuiChest) {
                        int slot = findSlotWithItemInOpenGui(item);

                        if (slot != -1) {
                            System.out.println("Found item in slot " + slot);
                            clickWindowSlot(slot);
                        } else {
                            System.out.println("Item " + item + " not found in the open GUI");
                        }
                    } else {
                        System.out.println("Current screen is not GuiChest, cannot find items");
                    }
                }, 2, TimeUnit.SECONDS);
            } else {
                System.out.println("Current screen is not GuiChest, cannot find items");
            }
        }, 2, TimeUnit.SECONDS);
    }

    private static int findSlotWithItemInOpenGui(String itemName) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.currentScreen instanceof GuiChest) {
            GuiChest guiChest = (GuiChest) mc.currentScreen;
            ContainerChest chest = (ContainerChest) guiChest.inventorySlots;
            IInventory lowerChestInventory = chest.getLowerChestInventory();

            // Now you can use lowerChestInventory


            System.out.println("Scanning inventory slots...");
            for (int i = 0; i < lowerChestInventory.getSizeInventory(); i++) {
                ItemStack stackInSlot = lowerChestInventory.getStackInSlot(i);
                if (stackInSlot != null) {
                    System.out.println("Slot " + i + ": " + stackInSlot.getDisplayName());
                    if (stackInSlot.getDisplayName().contains(itemName)) {
                        return i;
                    }
                } else {
                    System.out.println("Slot " + i + " is empty.");
                }
            }
        } else {
            System.out.println("Current screen is not GuiChest.");
        }
        return -1;
    }

    private static boolean isItemInSlot(GuiChest guiChest, int slot, Item item) {
        if (guiChest.inventorySlots instanceof ContainerChest) {
            ContainerChest chest = (ContainerChest) guiChest.inventorySlots;
            IInventory lowerChestInventory = chest.getLowerChestInventory();
            ItemStack itemStack = lowerChestInventory.getStackInSlot(slot);
            return itemStack != null && itemStack.getItem() == item;
        }
        return false;
    }

    private static void checkAndClickSlot(GuiChest guiChest, int slot, Item item) {
        if (isItemInSlot(guiChest, slot, item) && !slotClicked) {
            clickWindowSlot(slot);
            slotClicked = true;
            scheduler.shutdown();
        }
    }

    private static void clickWindowSlot(int slot) {
        Minecraft mc = Minecraft.getMinecraft();
        int windowId = mc.thePlayer.openContainer.windowId;
        int mouseButton = 2; // Middle click
        int mode = 3; // Mode for middle click
        short actionNumber = 0; // This can be incremented or left as 0

        Packet<?> packet = new C0EPacketClickWindow(windowId, slot, mouseButton, mode, mc.thePlayer.inventory.getItemStack(), actionNumber);
        mc.thePlayer.sendQueue.addToSendQueue(packet);
    }

    private void resetScheduler() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
        scheduler = Executors.newScheduledThreadPool(1);
        slotClicked = false;
    }

    public static void claimAuctions(GuiChest guiChest) {
        Minecraft.getMinecraft().thePlayer.sendChatMessage("/ah");
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        clickWindowSlot(15);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        System.out.println("Running through the slots");
        String item = "Claim All";

        scheduler.schedule(() -> {
            if (Minecraft.getMinecraft().currentScreen instanceof GuiChest) {
                int slot = findSlotWithItemInOpenGui(item);

                if (slot != -1) {
                    System.out.println("Found item in slot " + slot);
                    clickWindowSlot(slot);
                    MantaFlip.shouldRun = true;
                } else {
                    System.out.println("Item " + item + " not found in the open GUI");
                    Minecraft.getMinecraft().displayGuiScreen(null);
                    sendMessage("No items to claim");
                    MantaFlip.shouldRun = true;
                }
            } else {
                Minecraft.getMinecraft().displayGuiScreen(null);
                System.out.println("Current screen is not GuiChest, cannot find items");
                MantaFlip.shouldRun = true;
            }
        }, 2, TimeUnit.SECONDS);
    }










    private static void slotLeftClick(int windowID, int slotID) {
        Minecraft mc = Minecraft.getMinecraft();
        System.out.println("Left clicking slot " + slotID + " in window " + windowID);

        mc.playerController.windowClick(windowID, slotID, 2, 3, mc.thePlayer);
    }

    public static void relistAuction(String item, int price, int initial) {
        Minecraft.getMinecraft().thePlayer.sendChatMessage("/ah");
        int targetPrice = MantaFlip.getTargetPrice(item);
        System.out.println("Listing " + item + " for " + price + " coins");

        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

//        executorService.schedule(() -> {
//            clickWindowSlot(15);
//            System.out.println("Clicked on slot 15");
//        }, 1, TimeUnit.SECONDS);


        ////Find the gold horse armour and click it





        executorService.schedule(() -> {
            int slota = findSlotWithItem(item);

            executorService.schedule(() -> {
                if (slota != -1) {
                    System.out.println("Found " + item + " in slot " + slota);
                    clickWindowSlot(31);

                    executorService.schedule(() -> {
                        Minecraft mc = Minecraft.getMinecraft();
                        if (mc.currentScreen == null || !(mc.currentScreen instanceof GuiEditSign)) {
                            return;
                        }

                        GuiEditSign gui = (GuiEditSign) mc.currentScreen;
                        TileEntitySign tileSign = (TileEntitySign) ReflectionUtils.field(gui, "tileSign");
                        if (tileSign == null) {
                            tileSign = (TileEntitySign) ReflectionUtils.field(gui, "field_146848_f");
                        }
                        if (tileSign != null) {
                            String priceStr = String.valueOf(price);
                            tileSign.signText[0] = new ChatComponentText(priceStr);
                            Packet<?> packet = new C12PacketUpdateSign(tileSign.getPos(), tileSign.signText);
                            mc.thePlayer.sendQueue.addToSendQueue(packet);
                            System.out.println("Typed " + priceStr + " in the sign");

                            executorService.schedule(() -> {
                                clickWindowSlot(33);
                                executorService.schedule(() -> {
                                    clickWindowSlot(14);
                                    executorService.schedule(() -> {
                                        clickWindowSlot(29);
                                        executorService.schedule(() -> {
                                            clickWindowSlot(11);
                                            executorService.schedule(() -> {
                                                Minecraft.getMinecraft().displayGuiScreen(null);
                                                relisting = false;


                                                executorService.schedule(() -> {
                                                    try {
                                                        WebhookSend.sendListedEmbed(item, price, initial);
                                                    } catch (IOException e) {
                                                        throw new RuntimeException(e);
                                                    }
                                                }, 500, TimeUnit.MILLISECONDS);

                                            }, 500, TimeUnit.MILLISECONDS);
                                        }, 500, TimeUnit.MILLISECONDS);
                                    }, 500, TimeUnit.MILLISECONDS);
                                }, 500, TimeUnit.MILLISECONDS);
                            }, 500, TimeUnit.MILLISECONDS);
                        } else {
                            System.out.println("TileEntitySign is null, cannot set price");
                        }
                    }, 1, TimeUnit.SECONDS);
                } else {
                    System.out.println("Did not find " + item + " in inventory");
                }
            }, 1, TimeUnit.SECONDS);
        }, 1, TimeUnit.SECONDS);

//        }, 2, TimeUnit.SECONDS);

//        executorService.schedule(() -> {
//            if (Minecraft.getMinecraft().currentScreen instanceof GuiEditSign) {
//                GuiEditSign gui = (GuiEditSign) Minecraft.getMinecraft().currentScreen;
//                TileEntitySign tileSign = (TileEntitySign) ReflectionUtils.getFieldValue(gui, "tileSign");
//                if (tileSign == null) {
//                    tileSign = (TileEntitySign) ReflectionUtils.getFieldValue(gui, "field_146848_f");
//                }
//                if (tileSign != null) {
//                    String priceStr = String.valueOf(price);
//                    tileSign.signText[0] = new ChatComponentText(priceStr);
//                    Packet<?> packet = new C12PacketUpdateSign(tileSign.getPos(), tileSign.signText);
//                    Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueue(packet);
//                    System.out.println("Typed " + targetPrice + " in the sign");
//                } else {
//                    System.out.println("TileEntitySign is null, cannot set price");
//                }
//            } else {
//                System.out.println("Not in sign editing GUI");
//            }
//        }, 4, TimeUnit.SECONDS);
    }



    private static void clickInventorySlot(int slot) {
        Minecraft mc = Minecraft.getMinecraft();
        int windowId = mc.thePlayer.openContainer.windowId;
        int mouseButton = 0; // Left click
        int mode = 0; // Click mode
        short actionNumber = mc.thePlayer.openContainer.getNextTransactionID(mc.thePlayer.inventory);

        // Calculate the correct slot index for the player's inventory while a container is open
        int containerSlotCount = mc.thePlayer.openContainer.inventorySlots.size() - 36; // Subtracting the 36 slots of the player's inventory
        int inventorySlot = containerSlotCount + slot;

        ItemStack stack = mc.thePlayer.inventory.getStackInSlot(slot);
        System.out.println("Slot " + slot + " contains " + stack.getDisplayName());

        if (stack != null) {
            Packet<?> packet = new C0EPacketClickWindow(windowId, inventorySlot, mouseButton, mode, stack, actionNumber);
            mc.thePlayer.sendQueue.addToSendQueue(packet);
            System.out.println("Clicked inventory slot " + slot + " (adjusted to " + inventorySlot + ") in window ID " + windowId);
        } else {
            System.out.println("No item in slot " + slot);
        }
    }


    private static int findSlotWithItem(String itemName) {
        // Get the player's inventory
        IInventory inventory = Minecraft.getMinecraft().thePlayer.inventory;

        // Get the window ID
        int windowId = Minecraft.getMinecraft().thePlayer.openContainer.windowId;

        // Iterate over all slots in the inventory
        System.out.println("Scanning inventory slots in window ID: " + windowId);
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            // Get the current slot
            ItemStack stackInSlot = inventory.getStackInSlot(i);

            // Check if the slot is not null
            if (stackInSlot != null) {
                // Get the item name in the slot and remove color codes
                String slotItemName = stackInSlot.getDisplayName().replaceAll("§.", "");

                // Print out the contents of the slot
                System.out.println("Slot " + i + ": " + slotItemName);

                // Check if the slot contains an item with the specified name
                if (slotItemName.contains(itemName)) {
                    System.out.println("Found item: " + slotItemName + " in slot " + i + " of window ID: " + windowId);
                    slotLeftClick(windowId, i+27);
                    // Return the slot index
                    return i;
                }
            } else {
                System.out.println("Slot " + i + ": empty");
            }
        }

        // Return -1 if no slot with the specified item was found
        return -1;
    }



}
