package com.github.calamari34.mantaflipbeta.features;

import com.github.calamari34.mantaflipbeta.utils.DiscordWebhook;
import com.github.calamari34.mantaflipbeta.utils.Utils;
import org.json.JSONObject;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;

import static com.github.calamari34.mantaflipbeta.utils.Utils.formatNumber;

public class WebhookSend {

    public static String resolveUsername(String uuid) {
        try {
            URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // Parse JSON response
            JSONObject jsonObject = new JSONObject(response.toString());
            String username = jsonObject.getString("name");

            return username;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }




    public static void sendPurchaseEmbed(String item, int price, int targetPrice, int profit, long elapsedTime, String itemName, String isBed, String auctioneerId, String finder) throws IOException {
        System.out.println("Sending purchase embed");
        DiscordWebhook webhook = new DiscordWebhook("https://discord.com/api/webhooks/1245870790319280128/-lbYN4TCUpTtEg6IfDSObdbUmFSBicbtlKuLdJrmYA4GPowcAVhwTJCTBXNqas9GwomT");

        webhook.setUsername("MantaFlip");

        String encodedItemName;
        try {
            encodedItemName = URLEncoder.encode(itemName, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            encodedItemName = itemName; // Fallback to original name if encoding fails
        }
        if (finder == null) {
            finder = "Unknown";
        }else if (auctioneerId == null) {
            auctioneerId = "Unknown";
        }
        NumberFormat format = NumberFormat.getInstance();
//        String username = resolveUsername(auctioneerId);
        DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject();
        embed.setTitle("Flip Purchased")
                .addField("Item Name", item, false)
                .addField("Buy Price", formatNumber(price), false)
                .addField("Item Worth", formatNumber(targetPrice), false)
                .addField("Profit", formatNumber(profit), false)
                .addField("Buy Speed", String.valueOf(elapsedTime), false)
//                .addField("Seller", auctioneerId, false)
//                .addField("Finder", finder, false)
                .addField("Bed Flip", isBed, false)
                .setFooter("Purse: " + format.format(Utils.getPurse()))
                .setColor(new Color(0x32D74D))
                .setThumbnail("https://sky.coflnet.com/static/icon/" + encodedItemName);

        webhook.addEmbed(embed);
        webhook.execute();
    }



    static void sendListedEmbed(String item, int targetPrice, int initial) throws IOException {

        DiscordWebhook webhook = new DiscordWebhook("https://discord.com/api/webhooks/1245870790319280128/-lbYN4TCUpTtEg6IfDSObdbUmFSBicbtlKuLdJrmYA4GPowcAVhwTJCTBXNqas9GwomT");

        webhook.setUsername("MantaFlip");
        int profit = targetPrice - initial;

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
        itemName = itemName.replaceAll("[^a-zA-Z0-9]", "");


        String encodedItemName;
        try {
            encodedItemName = URLEncoder.encode(itemName, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            encodedItemName = itemName; // Fallback to original name if encoding fails
        }
        NumberFormat format = NumberFormat.getInstance();

        DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject();
        embed.setTitle("Flip Listed")
                .addField("Listed Item", item, false)

                .addField("Listed for", formatNumber(targetPrice), false)
                .addField("Profit", formatNumber(profit), false)
                .setFooter("Purse: " + format.format(Utils.getPurse()))
                .setColor(new Color(0x02ABF8))
                .setThumbnail("https://sky.coflnet.com/static/icon/" + encodedItemName);

        webhook.addEmbed(embed);
        webhook.execute();
    }

    static void sendCaptchaWebhook() throws IOException {
        DiscordWebhook webhook = new DiscordWebhook("https://discord.com/api/webhooks/1245870790319280128/-lbYN4TCUpTtEg6IfDSObdbUmFSBicbtlKuLdJrmYA4GPowcAVhwTJCTBXNqas9GwomT");

        webhook.setUsername("MantaFlip");

        DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject();
        embed.setTitle("Captcha")
                .setDescription("@everyone Captcha detected, please login and solve it (Solver is not implemented yet)")
                .setColor(new Color(0xFF0000));

        webhook.addEmbed(embed);
        webhook.execute();

    }
}