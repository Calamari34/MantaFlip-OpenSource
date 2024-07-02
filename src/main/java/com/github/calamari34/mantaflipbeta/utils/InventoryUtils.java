package com.github.calamari34.mantaflipbeta.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class InventoryUtils {

    /**
     * Gets the ItemStack in the specified slot of the currently open container.
     *
     * @param slot the slot index
     * @return the ItemStack in the specified slot, or null if the slot is empty
     */
    public static ItemStack getStackInOpenContainerSlot(int slot) {
        Minecraft mc = Minecraft.getMinecraft();
        Container openContainer = mc.thePlayer.openContainer;
        if (openContainer != null && slot >= 0 && slot < openContainer.inventorySlots.size()) {
            Slot containerSlot = openContainer.getSlot(slot);
            if (containerSlot != null && containerSlot.getHasStack()) {
                return containerSlot.getStack();
            }
        }
        return null;
    }
}
