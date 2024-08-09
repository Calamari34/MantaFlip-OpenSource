package com.github.calamari34.mantaflipbeta.features.Cofl;

import com.github.calamari34.mantaflipbeta.MantaFlip;
import com.github.calamari34.mantaflipbeta.features.AuctionDetails;
import com.github.calamari34.mantaflipbeta.features.CaptchaHandler;
import com.github.calamari34.mantaflipbeta.features.PacketListener;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import lombok.Getter;
import lombok.Setter;

import java.io.PrintStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

import static com.github.calamari34.mantaflipbeta.MantaFlip.*;
import static com.github.calamari34.mantaflipbeta.utils.Utils.sendMessage;

public class Cofl {
    @Getter
    public final CustomQueue customQueue = new CustomQueue();
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



    private static final Pattern pattern = Pattern.compile("type[\":]*flip");

        public static void handleMessage(String str) {

        try {


            if (!MantaFlip.shouldRun || PacketListener.relisting) {
                return;
            }
            CaptchaHandler.handleCaptcha(str);

            if (pattern.matcher(str).find()) {
                String[] split = str.split("Received: ");
                JsonObject received = new JsonParser().parse(split[1]).getAsJsonObject();
//                JsonReader reader = new JsonReader(new StringReader(str));
//                reader.setLenient(true);
//                JsonObject received = JsonParser.parseReader(reader).getAsJsonObject();

                if (!received.get("type").getAsString().equals("flip")) return;
                JsonObject auction = new JsonParser().parse(received.get("data").getAsString()).getAsJsonObject();

                String itemName = auction.get("auction").getAsJsonObject().get("itemName").getAsString();
                int startingBid = auction.get("auction").getAsJsonObject().get("startingBid").getAsInt();

                String tag = auction.get("auction").getAsJsonObject().get("tag").getAsString();
                int target = auction.get("target").getAsInt();
                String finder = auction.get("finder").getAsString();
                JsonArray messages = auction.get("messages").getAsJsonArray();

                String onClick = messages.get(0).getAsJsonObject().get("onClick").getAsString();
                String auctionId = onClick.substring("/viewauction ".length());
                AuctionDetails auctionDetails = new AuctionDetails(itemName, startingBid, target, auctionId, tag);
                int profit = target - startingBid;
                MantaFlip.itemTargetPrices.put(itemName, target);
                MantaFlip.itemDisplayName.put(itemName, tag);
                MantaFlip.itemFinder.put(itemName, finder);
                MantaFlip.itemProfit.put(itemName, profit);
                MantaFlip.itemID.put(itemName, auctionId);

                auctionDetailsList.add(auctionDetails);
                sendMessage("buying");
//                auctionDetails.sendServerMessage();
                QueueItem queueItem = new QueueItem(auctionId);
                MantaFlip.getInstance().getQueue().add(queueItem);
                MantaFlip.getInstance().getQueue().scheduleClear();
                String windowTitle = MantaFlip.getWindow();
                if (windowTitle == null ) {
                    queueItem.openAuction();
                }

//                customQueue.add(new QueueItem(auctionId, itemName, startingBid, target, auctionId));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
