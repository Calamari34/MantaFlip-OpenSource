package com.github.calamari34.mantaflipbeta.features;

import com.github.calamari34.mantaflipbeta.MantaFlip;
import net.minecraft.client.Minecraft;

import static com.github.calamari34.mantaflipbeta.utils.Utils.sendMessage;

public class AuctionDetails {
    private final String itemName;
    private final int startingBid;
    private final int targetPrice;
    private final String auctionId;
    private final String tag;

    public AuctionDetails(String itemName, int startingBid, int targetPrice, String auctionId, String tag) {
        this.itemName = itemName;
        this.startingBid = startingBid;
        this.targetPrice = targetPrice;
        this.auctionId = auctionId;
        this.tag = tag;
    }

    public String getItemName() {
        return itemName;
    }

    public int getStartingBid() {
        return startingBid;
    }

    public int getTargetPrice() {
        return targetPrice;
    }

    public String getAuctionId() {
        return auctionId;
    }

    public String getTag() {
        return tag;
    }

    public static int findTargetPriceByItemId(String itemId) {
        for (AuctionDetails auctionDetails : MantaFlip.auctionDetailsList) {
            if (auctionDetails.getAuctionId().equals(itemId)) {
                return auctionDetails.getTargetPrice();
            }
        }
        return 0; // Return 0 if no AuctionDetails object with the given itemId is found
    }
}



