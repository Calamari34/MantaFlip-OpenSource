package com.github.calamari34.mantaflipbeta.events;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static com.github.calamari34.mantaflipbeta.utils.InventoryUtils.getInventoryName;
import static com.github.calamari34.mantaflipbeta.utils.Utils.sendMessage;

public class GuiEventHandler {

    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event) {
        if (event.gui instanceof GuiContainer) {
            sendMessage("Gui opened");
            GuiContainer guiContainer = (GuiContainer) event.gui;
            String windowName = getInventoryName(guiContainer);

            if ("BIN Auction View".equals(windowName)) {
                sendMessage("BIN Auction View opened");
            } else if ("Confirm Purchase".equals(windowName)) {
                sendMessage("BIN Auction View opened");
            }
        }
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