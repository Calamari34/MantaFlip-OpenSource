package com.github.calamari34.mantaflipbeta.utils;

import com.github.calamari34.mantaflipbeta.MantaFlip;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;

public class InventoryUtils {

    /**
     * Gets the ItemStack in the specified slot of the currently open container.
     *
     * @param slot the slot index
     * @return the ItemStack in the specified slot, or null if the slot is empty
     */
    public static ItemStack getStackInOpenContainerSlot(GuiContainer guiContainer, int slot) {
        Container container = guiContainer.inventorySlots;
        if (container != null && slot >= 0 && slot < container.inventorySlots.size()) {
            Slot containerSlot = container.getSlot(slot);
            if (containerSlot != null && containerSlot.getHasStack()) {
                return containerSlot.getStack();
            }
        }
        return null;
    }

   

    public static boolean inventoryNameStartsWith(String startsWithString) {
        return getInventoryName() != null && getInventoryName().startsWith(startsWithString);
    }

    public static boolean inventoryNameContains(String startsWithString) {
        return getInventoryName() != null && getInventoryName().contains(startsWithString);
    }

    public static String getInventoryName() {
        if (MantaFlip.mc.currentScreen instanceof GuiChest) {
            final ContainerChest chest = (ContainerChest) MantaFlip.mc.thePlayer.openContainer;
            final IInventory inv = chest.getLowerChestInventory();
            return inv.hasCustomName() ? inv.getName() : null;
        }
        return null;
    }

    public static void clickOpenContainerSlot(final int slot, final int button, final int clickType) {
        MantaFlip.mc.playerController.windowClick(MantaFlip.mc.thePlayer.openContainer.windowId, slot, button, clickType, MantaFlip.mc.thePlayer);
    }

    public static void clickOpenContainerSlot(final int slot) {
        clickOpenContainerSlot(slot, 2, 3);
    }

    public static void clickWindow2(int window, int slot) {
        MantaFlip.mc.playerController.windowClick(window, slot, 2, 3, MantaFlip.mc.thePlayer);
    }

    public static NBTTagList getLore(ItemStack item) {
        if (item == null) {
            throw new NullPointerException("The item cannot be null!");
        }
        if (!item.hasTagCompound()) {
            return null;
        }

        return item.getSubCompound("display", false).getTagList("Lore", 8);
    }
}
