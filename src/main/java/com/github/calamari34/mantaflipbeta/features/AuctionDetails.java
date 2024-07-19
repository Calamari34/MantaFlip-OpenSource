package com.github.calamari34.mantaflipbeta.features;

import com.github.calamari34.mantaflipbeta.MantaFlip;
import net.minecraft.client.Minecraft;

import static com.github.calamari34.mantaflipbeta.utils.Utils.sendMessage;

public class AuctionDetails {
    private String tag;
    private String itemName;
    private int startingBid;
    private int target;
    private String auctionId;
//    private String finder;
//    private String auctioneerId;

    public AuctionDetails(String itemName, int startingBid, int target, String auctionId, String tag) {
        this.itemName = itemName;
        this.startingBid = startingBid;
        this.target = target;
        this.auctionId = auctionId;
        this.tag = tag;
//        this.finder = finder;
//        this.auctioneerId = auctioneerId;
    }

//    public String getAuctioneerId() {
//        return auctioneerId;
//    }
//
//    public String getFinder() {
//        return finder;
//    }

    public String getItemName() {
        return itemName;
    }
    public String getTag() { return tag;}

    public int getStartingBid() {
        return startingBid;
    }

    public int getTarget() {
        return target;
    }

    public String getAuctionId() {
        return auctionId;
    }

    private boolean isOpen = false;

    public boolean isOpen() {
        return isOpen;
    }

    private static long lastCalledTime = 0;

    public void sendServerMessage() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastCalledTime < 2000) {
            // Less than 2 seconds have passed since the last call, return without doing anything
            return;
        }

        lastCalledTime = currentTime;

        String windowTitle = MantaFlip.getWindow();
        System.out.println("window title: " + windowTitle);
        if (windowTitle == null) {
            Minecraft.getMinecraft().thePlayer.sendChatMessage("/viewauction " + getAuctionId());

        }
    }


}
