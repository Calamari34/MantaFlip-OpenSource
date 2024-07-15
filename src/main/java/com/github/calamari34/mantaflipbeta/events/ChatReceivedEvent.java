package com.github.calamari34.mantaflipbeta.events;

import com.github.calamari34.mantaflipbeta.MantaFlip;
import com.github.calamari34.mantaflipbeta.features.AuctionDetails;
import com.github.calamari34.mantaflipbeta.features.Claimer;
import com.github.calamari34.mantaflipbeta.features.PacketListener;
import com.github.calamari34.mantaflipbeta.features.WebhookSend;
import com.github.calamari34.mantaflipbeta.utils.DiscordWebhook;
import com.github.calamari34.mantaflipbeta.utils.ScoreboardUtils;
import com.github.calamari34.mantaflipbeta.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.github.calamari34.mantaflipbeta.MantaFlip.*;
import static com.github.calamari34.mantaflipbeta.features.PacketListener.escrowTime;
import static com.github.calamari34.mantaflipbeta.features.WebhookSend.sendLimitEmbed;
import static com.github.calamari34.mantaflipbeta.features.WebhookSend.sendSoldEmbed;
import static com.github.calamari34.mantaflipbeta.utils.Utils.sendMessage;
import static com.github.calamari34.mantaflipbeta.features.Claimer.*;
public class ChatReceivedEvent {
    private final Pattern AUCTION_SOLD_PATTERN = Pattern.compile("^(.*?) bought (.*?) for ([\\d,]+) coins CLICK$");
    public final ArrayList<HashMap<String, String>> sold_items = new ArrayList<>();
    public final ArrayList<HashMap<String, String>> bought_items = new ArrayList<>();
    public static boolean shouldRun = false;



    private long elapsedTime = 0;



    @SubscribeEvent
    public void onChatReceived(ClientChatReceivedEvent event) {


        try {
            String message = event.message.getUnformattedText();

            if (message.contains("Putting coins in escrow")) {

                escrowTime = System.currentTimeMillis();
                elapsedTime = escrowTime - PacketListener.auctionHouseOpenTime;
                System.out.println("Elapsed time: " + elapsedTime + "ms");
            }

            if (message.contains("Reached the active auctions limit!")) {
                sendLimitEmbed();
            }





            if (message.startsWith("You purchased")) {
                System.out.println("Purchased something");


                if (!MantaFlip.ToggleClaim || !MantaFlip.ToggleRelist) {
                    sendMessage("Claiming and relisting is disabled");
                    return;

                }



                Pattern purchasePattern = Pattern.compile("You purchased (.*) for ([\\d,]+) coins!");
                Matcher purchaseMatcher = purchasePattern.matcher(message);

                if (purchaseMatcher.find()) {
                    String item = purchaseMatcher.group(1);
                    int price = Integer.parseInt(purchaseMatcher.group(2).replace(",", ""));

                    int targetPrice = getTargetPrice(item);
                    System.out.println(item + " Target price: " + targetPrice);
                    int profit = targetPrice - price;
                    MantaFlip.updateProfit(profit);

                    if (MantaFlip.ToggleClaim)
                    {
                        PacketListener.claimAuction(item);
                    }
                    else
                    {
                        sendMessage("Claiming is disabled");
                    }


                    HashMap<String, String> map = new HashMap<>();
                    map.put("name", item);
                    map.put("price", Utils.formatNumbers(price));
                    MantaFlip.cofl.bought_items.add(map);
                    if (MantaFlip.ToggleRelist)
                    {

                        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
                        executorService.schedule(() -> {
                            if (targetPrice != 0) {

                                System.out.println("Relisting auction!");
                                PacketListener.relistAuction(item, targetPrice, price);

                            } else {
                                sendMessage("Item worth is 0. Not relisting item: " + item);
                            }
                        }, 5, TimeUnit.SECONDS);

                    }
                    else
                    {
                        sendMessage("Relisting is disabled");
                    }
                    String itemName = formatItemName(item);
                    String tag = GetItemDisplayName(item);
                    System.out.println("tag" + tag);


//                    AuctionDetails auctionDetails = findAuctionDetailsByItemName(itemName);
//
//                    String tag = ""; // Initialize tag variable
//
//// Check if auctionDetails is not null and then retrieve the tag
//                    if (auctionDetails != null) {
//                        tag = auctionDetails.getTag(); // Assuming getTag() method exists in AuctionDetails class
//                        // Other existing code to retrieve auctioneerId and finder, if needed
//                    }

//                    if (auctionDetails != null) {
//                        auctioneerId = auctionDetails.getAuctioneerId();
//                        finder = auctionDetails.getFinder();
//                        System.out.println("AuctioneerId: " + auctioneerId + " Finder: " + finder);
//                    }

                    String isBed = PacketListener.isbBed;
                    WebhookSend.sendPurchaseEmbed(item, price, targetPrice, profit, elapsedTime, itemName, isBed, tag);

                    HashMap<String, String> purchasedItem = new HashMap<>();
                    purchasedItem.put("Item Name", item);
                    purchasedItem.put("Buy Price", String.valueOf(price));
                    purchasedItem.put("Item Worth", String.valueOf(targetPrice));
                    purchasedItem.put("Profit", String.valueOf(profit));
                    purchasedItem.put("Buy Speed", String.valueOf(elapsedTime) + "ms");
                    purchasedItem.put("Bed Flip", PacketListener.isbBed);
                    sendMessage("Purchased " + item + " for profit: " + profit + " coins. Buy speed: " + elapsedTime + "ms");

                    bought_items.add(purchasedItem);
                }
            }



            if (message.contains("You were spawned in Limbo") || message.contains("return from AFK")) {
                try {
                    sendMessage("Detected in Limbo or Lobby, sending you back to skyblock");
                    shouldRun = false;
                    Thread.sleep(30000 + new Random().nextInt(5000));
                    Minecraft.getMinecraft().thePlayer.sendChatMessage("/lobby");
                    Thread.sleep(30000 + new Random().nextInt(5000));
                    Minecraft.getMinecraft().thePlayer.sendChatMessage("/skyblock");
                    Thread.sleep(30000 + new Random().nextInt(5000));
                    Minecraft.getMinecraft().thePlayer.sendChatMessage("/is");
                    shouldRun = true;
                } catch (Exception ignored) {
                }
            }



            if (!message.contains(":")) {
                if (message.contains("[Auction]")) {

                    Pattern soldPattern = Pattern.compile("^(.*?) bought (.*?) for ([\\d,]+) coins CLICK$");
                    Matcher soldMatcher = soldPattern.matcher(ScoreboardUtils.cleanSB(message));

                    if (soldMatcher.find()) {
                        String purchaser;
                        try {
                            purchaser = soldMatcher.group(1).split("\\[Auction] ")[1];
                        } catch (Exception ignored) {
                            purchaser = message.split("\\[Auction] ")[1].split(" bought")[0];
                        }
                        
                        open();


                        HashMap<String, String> sold_item = new HashMap<>();
                        NumberFormat format = NumberFormat.getInstance();
                        sold_item.put("item", soldMatcher.group(2));
                        sold_item.put("price", soldMatcher.group(3));
                        MantaFlip.cofl.sold_items.add(sold_item);
                        sendSoldEmbed(soldMatcher.group(2), Integer.parseInt(soldMatcher.group(3).replace(",", "")), purchaser);
                        
                    }



                }
            }

            if (message.contains("You didn't participate")) {
                Minecraft.getMinecraft().displayGuiScreen(null);
                System.out.println("Chat message: " + message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private AuctionDetails findAuctionDetailsByItemName(String itemName) {
        for (AuctionDetails auctionDetails : auctionDetailsList) {
            if (auctionDetails.getItemName().equalsIgnoreCase(itemName)) {
                return auctionDetails;
            }
        }
        return null;
    }

    private String formatItemName(String itemName) {
        String[] reforgePrefixes = {
                "Fabled", "Suspicious", "Gilded", "Salty", "Treacherous", "Stiff",
                "Lucky", "Magnetic", "Fruitful", "Refined", "Stellar", "Mithraic",
                "Auspicious", "Fleet", "Heated", "Gentle", "Odd", "Fast", "Fair",
                "Epic", "Sharp", "Heroic", "Spicy", "Legendary", "Deadly", "Fine",
                "Grand", "Hasty", "Neat", "Rapid", "Unreal", "Awkward", "Rich",
                "Clean", "Fierce", "Heavy", "Light", "Mythic", "Pure", "Smart",
                "Titanic", "Wise", "Perfect", "Necrotic", "Spiked", "Renowned",
                "Cubic", "Reinforced", "Loving", "Ridiculous", "Empowered",
                "Giant", "Submerged", "Jaded", "Bizarre", "Itchy", "Ominous",
                "Pleasant", "Pretty", "Shiny", "Simple", "Strange", "Vivid",
                "Godly", "Demonic", "Forceful", "Hurtful", "Keen", "Strong",
                "Superior", "Unpleasant", "Zealous", "Silky", "Bloody", "Shaded",
                "Sweet", "Warped", "Snowy", "Rooted", "Blooming", "Glistening",
                "Strengthened", "Fortified", "Waxed", "Ancient", "Hyper", "Dirty",
                "Chomp", "Pitchin", "Bulky", "Withered", "Mossy", "Festive",
                "Headstrong", "Spiritual", "Coldfused", "Empty"
        };

        for (String prefix : reforgePrefixes) {
            if (itemName.startsWith(prefix + " ")) {
                itemName = itemName.substring(prefix.length()).trim();
                break;
            }
        }

        itemName = itemName.toUpperCase().replace(" ", "_").replaceAll("\\W", "");
        itemName = itemName.replaceFirst("\\[Lvl \\d+\\] ", "");
        itemName = itemName.replaceAll("✪{1,10}", "");
        return itemName;
    }
}
