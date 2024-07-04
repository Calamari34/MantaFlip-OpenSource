package com.github.calamari34.mantaflipbeta.features.Cofl;
import com.github.calamari34.mantaflipbeta.MantaFlip;
import com.github.calamari34.mantaflipbeta.features.AuctionDetails;
import com.github.calamari34.mantaflipbeta.features.PacketListener;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import lombok.Setter;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;
import com.github.calamari34.mantaflipbeta.features.Cofl.Queue;

import static com.github.calamari34.mantaflipbeta.MantaFlip.*;
import static com.github.calamari34.mantaflipbeta.utils.Utils.sendMessage;



public class Cofl {
    @Getter
    public final Queue queue = new Queue();
    private @Getter
    @Setter boolean open = false;
    public final ArrayList<HashMap<String, String>> sold_items = new ArrayList<>();
    public final ArrayList<HashMap<String, String>> bought_items = new ArrayList<>();

    public void onOpen() {
        System.setOut(new PrintStream(System.out) {
            public void println(String str) {
                handleMessage(str);
                super.println(str);
            }
        });
    }

    private Thread antiafk;

    private final Pattern pattern = Pattern.compile("type[\":]*flip");

    public void handleMessage(String str) {
        try {
            if (!MantaFlip.shouldRun || PacketListener.relisting || !str.startsWith("Received:")) {
                return;
            }
            if (pattern.matcher(str).find()) {

                String[] split = str.split("Received: ");
                JsonObject received = new JsonParser().parse(split[1]).getAsJsonObject();
                if (!received.get("type").getAsString().equals("flip")) return;
                JsonObject auction = new JsonParser().parse(received.get("data").getAsString()).getAsJsonObject();
                String itemName = auction.get("auction").getAsJsonObject().get("itemName").getAsString();
//                JsonArray messages = dataObject.get("messages").getAsJsonArray();
//                String onClick = messages.get(0).getAsJsonObject().get("onClick").getAsString();
//                String auctionId = onClick.substring("/viewauction ".length());
                int startingBid = auction.get("auction").getAsJsonObject().get("startingBid").getAsInt();
//                String uid = auction.get("auction").getAsJsonObject().get("flatNbt").getAsJsonObject().get("uid").getAsString();
//                sendMessage("uid:" + uid);
                int target = auction.get("target").getAsInt();
                JsonArray messages = auction.get("messages").getAsJsonArray();
                String onClick = messages.get(0).getAsJsonObject().get("onClick").getAsString();
                String auctionId = onClick.substring("/viewauction ".length());
//                String uid = auction.get("auction").getAsJsonObject().get("flatNbt").getAsJsonObject().get("uid").getAsString();
//                sendMessage("uid:" + uid);
//              getQueue().add(new QueueItem(auctionId, itemName, startingBid, target, uid));
//              getQueue().scheduleClear();
                AuctionDetails auctionDetails = new AuctionDetails(itemName, startingBid, target, auctionId);
                MantaFlip.itemTargetPrices.put(itemName, target);
                auctionDetailsList.add(auctionDetails);
                auctionDetails.sendServerMessage();


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




}
