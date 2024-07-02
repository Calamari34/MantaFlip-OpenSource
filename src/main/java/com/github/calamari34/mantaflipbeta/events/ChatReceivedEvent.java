package com.github.calamari34.mantaflipbeta.events;

import com.github.calamari34.mantaflipbeta.features.AuctionDetails;
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

import static com.github.calamari34.mantaflipbeta.MantaFlip.auctionDetailsList;
import static com.github.calamari34.mantaflipbeta.MantaFlip.getTargetPrice;
import static com.github.calamari34.mantaflipbeta.utils.Utils.sendMessage;


public class ChatReceivedEvent {
    private final Pattern AUCTION_SOLD_PATTERN = Pattern.compile("^(.*?) bought (.*?) for ([\\d,]+) coins CLICK$");
    public final ArrayList<HashMap<String, String>> sold_items = new ArrayList<>();
    public final ArrayList<HashMap<String, String>> bought_items = new ArrayList<>();
    public static boolean shouldRun = false;
    @SubscribeEvent
    public void onChatReceived(ClientChatReceivedEvent event) {
        try {
            String message = event.message.getUnformattedText();

            if (message.startsWith("You purchased")) {

                long elapsedTime = PacketListener.endTime - PacketListener.startTime; // Correctly calculate elapsed time
                System.out.println("Elapsed time: " + elapsedTime);
                System.out.println("Purchased something");
                Pattern purchasePattern = Pattern.compile("You purchased (.*) for ([\\d,]+) coins!");
                Matcher purchaseMatcher = purchasePattern.matcher(message);

                if (purchaseMatcher.find()) {
                    String item = purchaseMatcher.group(1);
                    int price = Integer.parseInt(purchaseMatcher.group(2).replace(",", ""));

                    int targetPrice = getTargetPrice(item);
                    System.out.println(item + " Target price: " + targetPrice);
                    int profit = targetPrice - price;


                    PacketListener.claimAuction(item);

                    System.out.println("Relisting auction!");

                    ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

                    executorService.schedule(() -> {

                        if (targetPrice != 0) { // Check if the itemWorth is not 0
                            PacketListener.relistAuction(item, targetPrice, price);
                        } else {
                            sendMessage("Item worth is 0. Not relisting item: " + item);
                        }

                    }, 5, TimeUnit.SECONDS);


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
                    String itemName = item;

                    for (String prefix : reforgePrefixes) {
                        if (itemName.startsWith(prefix + " ")) {
                            itemName = itemName.substring(prefix.length()).trim();
                            break;
                        }
                    }

                    itemName = itemName.toUpperCase().replace(" ", "_").replaceAll("\\W", "");
                    itemName = itemName.replaceFirst("\\[Lvl \\d+\\] ", "");
                    String pattern = "✪{1,10}";
                    itemName = itemName.replaceAll(pattern, "");

                    String isBed = PacketListener.isbBed;

                    HashMap<String, String> purchasedItem = new HashMap<>();
                    purchasedItem.put("Item Name", item);
                    purchasedItem.put("Buy Price", String.valueOf(price));
                    purchasedItem.put("Item Worth", String.valueOf(targetPrice));
                    purchasedItem.put("Profit", String.valueOf(profit));
                    purchasedItem.put("Buy Speed", String.valueOf(elapsedTime) + "ms");
                    purchasedItem.put("Bed Flip", PacketListener.isbBed);
                    sendMessage("Purchased " + item + " for profit: " + profit + " coins. Buy speed: " + elapsedTime + "ms");

                    bought_items.add(purchasedItem);

                    AuctionDetails auctionDetails = findAuctionDetailsByItemName(itemName);
                    System.out.println(auctionDetails);
//                    String auctioneerId = null;
//                    String finder = null;
//                    if (auctionDetails != null) {
//                        auctioneerId = auctionDetails.getAuctioneerId();
//                        finder = auctionDetails.getFinder();
//                        System.out.println("Auctioneerid:" + auctioneerId + " finder:" + finder);
//                        // Now you have the auctioneerId and finder
//                    }
                    String auctioneerId = "test";
                    String finder = "Flip";
                    WebhookSend.sendPurchaseEmbed(item, price, targetPrice, profit, elapsedTime, itemName, isBed, auctioneerId, finder);
                } else {
                    System.out.println("No match found");
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
                }catch (Exception ignored) {
                }

            }
            if (!message.contains(":")) {
                if (message.contains("[Auction]")) {

                    // §6[Auction] §aphiinix_ §ebought §fImplosion Belt §efor §6900,000 coins §lCLICK
                    Matcher matcher = AUCTION_SOLD_PATTERN.matcher(ScoreboardUtils.cleanSB(message));
                    if (matcher.matches()) {

                        String purchaser;
                        try {
                            purchaser = matcher.group(1).split("\\[Auction] ")[1];
                        } catch (Exception ignored) {
                            purchaser = message.split("\\[Auction] ")[1].split(" bought")[0];
                        }
                        HashMap<String, String> sold_item = new HashMap<>();
                        NumberFormat format = NumberFormat.getInstance();
                        sold_item.put("item", matcher.group(2));
                        sold_item.put("price", matcher.group(3));
                        DiscordWebhook webhook = new DiscordWebhook("https://discord.com/api/webhooks/1245870790319280128/-lbYN4TCUpTtEg6IfDSObdbUmFSBicbtlKuLdJrmYA4GPowcAVhwTJCTBXNqas9GwomT");
                        webhook.setUsername("MantaFlip");
                        new DiscordWebhook.EmbedObject().setTitle("Someone bought an item!").setFooter("Purse: " + format.format(Utils.getPurse())).addField("Item:", matcher.group(2), true).addField("Price:", matcher.group(3), true).addField("Purchaser:", purchaser, true).setColor(Color.decode("#003153"));
                        webhook.execute();

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

    public AuctionDetails findAuctionDetailsByItemName(String itemName) {
        for (AuctionDetails auctionDetails : auctionDetailsList) {
            if (auctionDetails.getItemName().equals(itemName)) {
                return auctionDetails;
            }
        }
        return null; // Return null if no AuctionDetails object with the given itemName is found
    }
}
