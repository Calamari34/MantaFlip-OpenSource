package com.github.calamari34.mantaflipbeta.events;

import com.github.calamari34.mantaflipbeta.MantaFlip;
import com.github.calamari34.mantaflipbeta.features.AuctionDetails;
import com.github.calamari34.mantaflipbeta.features.PacketListener;
import com.github.calamari34.mantaflipbeta.features.WebhookSend;
import com.github.calamari34.mantaflipbeta.utils.ScoreboardUtils;
import com.github.calamari34.mantaflipbeta.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

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
import static com.github.calamari34.mantaflipbeta.config.AHConfig.RELIST_TIMEOUT;

import static com.github.calamari34.mantaflipbeta.features.Packets.TimeElapsed;
import static com.github.calamari34.mantaflipbeta.features.WebhookSend.*;
import static com.github.calamari34.mantaflipbeta.utils.Utils.sendMessage;
import static com.github.calamari34.mantaflipbeta.features.Claimer.*;
public class ChatReceivedEvent {
    public static final ArrayList<PacketListener> selling_queue = new ArrayList<>();
    private final Pattern AUCTION_SOLD_PATTERN = Pattern.compile("^(.*?) bought (.*?) for ([\\d,]+) coins CLICK$");
    public final ArrayList<HashMap<String, String>> sold_items = new ArrayList<>();
    public final ArrayList<HashMap<String, String>> bought_items = new ArrayList<>();
    public static boolean shouldRun = false;
    public static boolean ah_full = false;


    private long elapsedTime = 0;





    @SubscribeEvent
    public void onChatReceived(ClientChatReceivedEvent event) {



        try {
            String message = event.message.getUnformattedText();

            if (message.startsWith("You") && message.contains("don't have") && message.contains("afford this bid")) {
                MantaFlip.mc.thePlayer.closeScreen();
                cofl.customQueue.setRunning(false);
                return;
            }



            if (message.contains("Reached the active auctions limit!")) {
                ah_full = true;
                sendLimitEmbed();
            }

            if (message.contains("You already have an item in the auction slot!")) {
                sendErrorEmbed();
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

                    int targetPrice = MantaFlip.getTargetPrice(item);
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
                            if (profit > 0) {

                                System.out.println("Relisting auction!");
                                try {
                                    Thread.sleep(RELIST_TIMEOUT);
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
                                if (ah_full) {
                                    sendMessage("Auction house is full, not relisting item: " + item);
                                    return;
                                }

                                try {
                                    PacketListener.relistAuction(item, targetPrice, price);
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }

                            } else {
                                sendMessage("Item worth is 0. Not relisting item: " + item);
                            }
                        }, 5, TimeUnit.SECONDS);

                    }
                    else
                    {
                        sendMessage("Relisting is disabled");
                    }

                    String tag = GetItemDisplayName(item);
                    System.out.println("tag" + tag);




                    String bed = PacketListener.isbBed;
                    WebhookSend.sendPurchaseEmbed(item, price, targetPrice, profit, TimeElapsed, bed, tag);

                    HashMap<String, String> purchasedItem = new HashMap<>();
                    purchasedItem.put("Item Name", item);
                    purchasedItem.put("Buy Price", String.valueOf(price));
                    purchasedItem.put("Item Worth", String.valueOf(targetPrice));
                    purchasedItem.put("Profit", String.valueOf(profit));
                    purchasedItem.put("Buy Speed", String.valueOf(TimeElapsed) + "ms");
                    purchasedItem.put("Bed Flip", PacketListener.isbBed);
                    sendMessage("Purchased " + item + " for profit: " + profit + " coins. Buy speed: " + TimeElapsed + "ms");

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
                MantaFlip.mc.thePlayer.closeScreen();
                cofl.customQueue.setRunning(false);

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


}
