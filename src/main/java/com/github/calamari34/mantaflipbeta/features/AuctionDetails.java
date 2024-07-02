package com.github.calamari34.mantaflipbeta.features;

import com.github.calamari34.mantaflipbeta.MantaFlip;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.inventory.Container;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

public class AuctionDetails {
    private String itemName;
    private int startingBid;
    private int target;
    private String auctionId;
    private String finder;
    private String auctioneerId;

    public AuctionDetails(String itemName, int startingBid, int target, String auctionId, String finder, String auctioneerId) {
        this.itemName = itemName;
        this.startingBid = startingBid;
        this.target = target;
        this.auctionId = auctionId;
        this.finder = finder;
        this.auctioneerId = auctioneerId;

    }

    public String getAuctioneerId() {
        return auctioneerId;
    }

    public String getFinder() {
        return finder;
    }

    public String getItemName() {
        return itemName;
    }

    public int getStartingBid() {
        return startingBid;
    }

    public int getTarget() {
        return target;
    }

    public String getAuctionId() {
        return auctionId;
    }

    MantaFlip MantaFlip = new MantaFlip();
    String windowTitle = MantaFlip.getWindow();

    private boolean isOpen = false;

    public boolean isOpen() {
        return isOpen;
    }


    private static long lastCalledTime = 0;

    public void sendServerMessage() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastCalledTime < 2000) {
            // Less than 10 seconds have passed since the last call, return without doing anything
            return;
        }

        lastCalledTime = currentTime;

        String windowTitle = MantaFlip.getWindow();
        System.out.println("window title: " + windowTitle);
        if (windowTitle == null) {
            System.out.println("window title is null");
            Minecraft.getMinecraft().thePlayer.sendChatMessage("/viewauction " + getAuctionId());
        } else {
            System.out.println("dont open");
        }
    }


}

