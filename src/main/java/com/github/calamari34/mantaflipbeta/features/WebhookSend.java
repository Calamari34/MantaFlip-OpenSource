package com.github.calamari34.mantaflipbeta.features;

import com.github.calamari34.mantaflipbeta.MantaFlip;
import com.github.calamari34.mantaflipbeta.utils.DiscordWebhook;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
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
import java.time.Instant;
import java.util.Random;


import static com.github.calamari34.mantaflipbeta.MantaFlip.GetItemDisplayName;
import static com.github.calamari34.mantaflipbeta.MantaFlip.getItemProfit;
import static com.github.calamari34.mantaflipbeta.utils.Utils.formatNumbers;
import static com.github.calamari34.mantaflipbeta.utils.Utils.sendMessage;

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


    public static void sendPurchaseEmbed(String item, int price, int targetPrice, int profit, long elapsedTime, String bed, String tag) throws IOException {
        System.out.println("Sending purchase embed");

        String webhookUrl = MantaFlip.configHandler.getString("Webhook");
        if (webhookUrl == null || webhookUrl.isEmpty()) {
            throw new IllegalArgumentException("Webhook URL is not set in the configuration.");
        }

        DiscordWebhook webhook = new DiscordWebhook(webhookUrl);

        webhook.setUsername("MantaFlip");
        webhook.setAvatarUrl("https://cdn.discordapp.com/attachments/1242759092645138474/1262282832127070298/MantaFlip.jpg?ex=669607ff&is=6694b67f&hm=65e3b7c13144b0ac7f2ab4f3b23ee9beef4cd8bd88a24a1bf4998a1e2422b3d1&");

        double profitPercentage = ((double) profit / price) * 100;
        long roundedProfitPercentage = Math.round(profitPercentage);

        if (elapsedTime >= 1000000) {
            Random random = new Random();
            int randomNumber = 2 + random.nextInt(33);
            int time2 = randomNumber + 40;
            elapsedTime = Long.parseLong(String.valueOf(time2));
        }

        NumberFormat format = NumberFormat.getInstance();
        String formattedProfit;
        if (profit < 0) {
            profit = Math.abs(profit);
            formattedProfit = "-" + formatNumbers(profit);
        } else {
            formattedProfit = formatNumbers(profit);
        }

        String newTP;
        if (targetPrice == 0) {
            newTP = formatNumbers(price) + " because UserFinder";
        } else {
            newTP = formatNumbers(targetPrice);
        }

        if (bed != null && !bed.isEmpty()) {
            bed = bed.substring(0, 1).toUpperCase() + bed.substring(1);
        }


        String id = MantaFlip.itemID.get(item);


        DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject();
        embed.setTitle("**Flip Purchased**")
                .setUrl("https://sky.coflnet.com/auction/" + id)
                .addField("Item Name \uD83C\uDFF7\uFE0F ", item, true)
                .addField("Buy Speed ⏱\uFE0F ", elapsedTime + "ms", true)
                .addField("Bed Flip \uD83D\uDECF\uFE0F ", bed, true)
                .addField("Buy Price \uD83D\uDCB0 ", formatNumbers(price), true)
                .addField("Value \uD83D\uDCB5 ", newTP, true)
                .addField("Profit \uD83D\uDCC8 ", formattedProfit + " || " + roundedProfitPercentage + "%", true)
                .setTimestamp(Instant.now())
                .setFooter("Flipping Notification • MantaFlip", "https://cdn.discordapp.com/attachments/1242759092645138474/1261447373503205506/Untitled-2.png?ex=6694f82a&is=6693a6aa&hm=c82875ec29c48e08bb9276675b95d226eedeb93329bfb5a10c77b4d29d6c1781&")
                .setColor(new Color(0x1F73D9))
                .setThumbnail("https://sky.coflnet.com/static/icon/" + tag);

        webhook.addEmbed(embed);

        try {
            webhook.execute();
        } catch (IOException e) {
            System.err.println("Error executing webhook: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }


    public static void sendLimitEmbed() throws IOException {
        DiscordWebhook webhook = new DiscordWebhook(MantaFlip.configHandler.getString("Webhook"));
        webhook.setUsername("MantaFlip");
        webhook.setAvatarUrl("https://cdn.discordapp.com/attachments/1242759092645138474/1262282832127070298/MantaFlip.jpg?ex=669607ff&is=6694b67f&hm=65e3b7c13144b0ac7f2ab4f3b23ee9beef4cd8bd88a24a1bf4998a1e2422b3d1&");
        DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject();
        embed.setTitle("**⚠\uFE0F Unable To Relist Item**")
                .setThumbnail("https://minotar.net/helm/" + Minecraft.getMinecraft().getSession().getUsername() + "/600.png")
                .setDescription("The auction house is full. To avoid overflow, consider disabling the macro with `/disable COFL Macro`.")
                .setFooter("Flipping Notification • MantaFlip", "https://cdn.discordapp.com/attachments/1242759092645138474/1261447373503205506/Untitled-2.png?ex=6694f82a&is=6693a6aa&hm=c82875ec29c48e08bb9276675b95d226eedeb93329bfb5a10c77b4d29d6c1781&")
                .setTimestamp(Instant.now())
                .setColor(new Color(0xC31E42));
        webhook.addEmbed(embed);
        webhook.execute();


    }

    public static void sendErrorEmbed() throws IOException {
        DiscordWebhook webhook = new DiscordWebhook(MantaFlip.configHandler.getString("Webhook"));
        webhook.setUsername("MantaFlip");
        webhook.setAvatarUrl("https://cdn.discordapp.com/attachments/1242759092645138474/1262282832127070298/MantaFlip.jpg?ex=669607ff&is=6694b67f&hm=65e3b7c13144b0ac7f2ab4f3b23ee9beef4cd8bd88a24a1bf4998a1e2422b3d1&");
        DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject();
        embed.setTitle("**⚠\uFE0F Error Occured**")
                .setThumbnail("https://minotar.net/helm/" + Minecraft.getMinecraft().getSession().getUsername() + "/600.png")
                .setDescription("An error occurred, there is an item stuck in the current auction slot run `/disable Auto Relister` and report this error.")
                .setFooter("Flipping Notification • MantaFlip", "https://cdn.discordapp.com/attachments/1242759092645138474/1261447373503205506/Untitled-2.png?ex=6694f82a&is=6693a6aa&hm=c82875ec29c48e08bb9276675b95d226eedeb93329bfb5a10c77b4d29d6c1781&")
                .setTimestamp(Instant.now())
                .setColor(new Color(0xC31E42));
        webhook.addEmbed(embed);
        webhook.execute();


    }

    public static void sendStartEmbed(String user, String date) throws IOException {
        DiscordWebhook webhook = new DiscordWebhook(MantaFlip.configHandler.getString("Webhook"));
        webhook.setUsername("MantaFlip");
        webhook.setAvatarUrl("https://cdn.discordapp.com/attachments/1242759092645138474/1262282832127070298/MantaFlip.jpg?ex=669607ff&is=6694b67f&hm=65e3b7c13144b0ac7f2ab4f3b23ee9beef4cd8bd88a24a1bf4998a1e2422b3d1&");
        DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject();
        embed.setTitle("**Logged in ✅ **")
                .setThumbnail("https://minotar.net/helm/" + Minecraft.getMinecraft().getSession().getUsername() + "/600.png")
                .setDescription("Successfully logged in as " + user + ". Your access will expire on " + date + ".")
                .setFooter("Flipping Notification • MantaFlip", "https://cdn.discordapp.com/attachments/1242759092645138474/1261447373503205506/Untitled-2.png?ex=6694f82a&is=6693a6aa&hm=c82875ec29c48e08bb9276675b95d226eedeb93329bfb5a10c77b4d29d6c1781&")
                .setTimestamp(Instant.now())
                .setColor(new Color(0x9E2C7D));
        webhook.addEmbed(embed);
        webhook.execute();


    }

    public static void sendSoldEmbed(String item, int Price, String purchaser) throws IOException {
        String tag = GetItemDisplayName(item);
        int profit = getItemProfit(item);
        double profitPercentage = ((double) profit / (Price - profit)) * 100;
        long roundedProfitPercentage = Math.round(profitPercentage);


        DiscordWebhook webhook = new DiscordWebhook(MantaFlip.configHandler.getString("Webhook"));
        webhook.setUsername("MantaFlip");
        webhook.setAvatarUrl("https://cdn.discordapp.com/attachments/1242759092645138474/1262282832127070298/MantaFlip.jpg?ex=669607ff&is=6694b67f&hm=65e3b7c13144b0ac7f2ab4f3b23ee9beef4cd8bd88a24a1bf4998a1e2422b3d1&");

        DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject();
        embed.setTitle("**Flip Sold**")
                .addField("**Item** \uD83D\uDCE6 ", item, true)
                .addField("**Profit** \uD83D\uDCB0", formatNumbers(profit) + " || " + roundedProfitPercentage + "%", true)
                .addField("**Purchaser** \uD83E\uDDD1 ", purchaser, true)
                .setThumbnail("https://sky.coflnet.com/static/icon/" + tag)
                .setFooter("Flipping Notification • MantaFlip", "https://cdn.discordapp.com/attachments/1242759092645138474/1261447373503205506/Untitled-2.png?ex=6694f82a&is=6693a6aa&hm=c82875ec29c48e08bb9276675b95d226eedeb93329bfb5a10c77b4d29d6c1781&")
                .setTimestamp(Instant.now())
                .setColor(new Color(0x1ED55F));

        webhook.addEmbed(embed);
        webhook.execute();
    }


    static void sendListedEmbed(String item, int targetPrice, int initial) throws IOException {

        DiscordWebhook webhook = new DiscordWebhook(MantaFlip.configHandler.getString("Webhook"));
        String tag = GetItemDisplayName(item);
        webhook.setUsername("MantaFlip");
        webhook.setAvatarUrl("https://cdn.discordapp.com/attachments/1242759092645138474/1262282832127070298/MantaFlip.jpg?ex=669607ff&is=6694b67f&hm=65e3b7c13144b0ac7f2ab4f3b23ee9beef4cd8bd88a24a1bf4998a1e2422b3d1&");

        int profit = targetPrice - initial;
        double profitPercentage = ((double) profit / initial) * 100;
        long roundedProfitPercentage = Math.round(profitPercentage);


        NumberFormat format = NumberFormat.getInstance();

        DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject();
        embed.setTitle("**Flip Listed**")
                .addField("**Item** \uD83D\uDCE6 ", item, true)
                .setTimestamp(Instant.now())
                .addField("**Price** \uD83D\uDCB0 ", formatNumbers(targetPrice), true)
                .addField("**Profit** \uD83D\uDCC8 ", formatNumbers(profit) + " || " + roundedProfitPercentage + "%", true)
                .setFooter("Flipping Notification • MantaFlip", "https://cdn.discordapp.com/attachments/1242759092645138474/1261447373503205506/Untitled-2.png?ex=6694f82a&is=6693a6aa&hm=c82875ec29c48e08bb9276675b95d226eedeb93329bfb5a10c77b4d29d6c1781&")
                .setColor(new Color(0xFFA500))
                .setThumbnail("https://sky.coflnet.com/static/icon/" + tag);

        webhook.addEmbed(embed);
        webhook.execute();
    }

    public static void sendCaptchaWebhook(String captchaDataJson) throws IOException {
        DiscordWebhook webhook = new DiscordWebhook(MantaFlip.configHandler.getString("Webhook"));
        webhook.setUsername("MantaFlip");
        webhook.setAvatarUrl("https://cdn.discordapp.com/attachments/1242759092645138474/1262282832127070298/MantaFlip.jpg?ex=669607ff&is=6694b67f&hm=65e3b7c13144b0ac7f2ab4f3b23ee9beef4cd8bd88a24a1bf4998a1e2422b3d1&");


        // Add line numbers before each newline
        StringBuilder numberedCaptchaData = new StringBuilder();
        int lineNumber = 1;
        boolean isNewLine = true;

        for (char c : captchaDataJson.toCharArray()) {
            if (isNewLine) {
                numberedCaptchaData.append(lineNumber).append(" ");
                isNewLine = false;
            }
            if (c == '\n') {
                numberedCaptchaData.append(c);
                lineNumber++;
                isNewLine = true;
            } else {
                numberedCaptchaData.append(c);
            }
        }

        captchaDataJson = numberedCaptchaData.toString().replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");


        DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject();
        embed.setTitle("⚠️ **Captcha Detected**")
                .setDescription("```" + captchaDataJson + "```")
                .setColor(new Color(0xC31E42))
                .setTimestamp(Instant.now())
                .setFooter("Flipping Notification • MantaFlip", "https://cdn.discordapp.com/attachments/1242759092645138474/1261447373503205506/Untitled-2.png?ex=6694f82a&is=6693a6aa&hm=c82875ec29c48e08bb9276675b95d226eedeb93329bfb5a10c77b4d29d6c1781&");

        webhook.addEmbed(embed);

        // Log the payload


        try {
            webhook.execute();
        } catch (IOException e) {
            // Log the error message and stack trace
            System.err.println("Error sending webhook: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void sendSuccessEmbed() throws IOException {
        DiscordWebhook webhook = new DiscordWebhook(MantaFlip.configHandler.getString("Webhook"));
        webhook.setUsername("MantaFlip");
        webhook.setAvatarUrl("https://cdn.discordapp.com/attachments/1242759092645138474/1262282832127070298/MantaFlip.jpg?ex=669607ff&is=6694b67f&hm=65e3b7c13144b0ac7f2ab4f3b23ee9beef4cd8bd88a24a1bf4998a1e2422b3d1&");
        DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject();
        embed.setTitle("✅ **Captcha Success**")
                .setThumbnail("https://minotar.net/helm/" + Minecraft.getMinecraft().getSession().getUsername() + "/600.png")
                .setDescription("Successfully solved the captcha.")
                .setFooter("Flipping Notification • MantaFlip", "https://cdn.discordapp.com/attachments/1242759092645138474/1261447373503205506/Untitled-2.png?ex=6694f82a&is=6693a6aa&hm=c82875ec29c48e08bb9276675b95d226eedeb93329bfb5a10c77b4d29d6c1781&")
                .setTimestamp(Instant.now())
                .setColor(new Color(0x1ED55F));
        webhook.addEmbed(embed);
        webhook.execute();


    }
    public static void sendFailedEmbed() throws IOException {
        DiscordWebhook webhook = new DiscordWebhook(MantaFlip.configHandler.getString("Webhook"));
        webhook.setUsername("MantaFlip");
        webhook.setAvatarUrl("https://cdn.discordapp.com/attachments/1242759092645138474/1262282832127070298/MantaFlip.jpg?ex=669607ff&is=6694b67f&hm=65e3b7c13144b0ac7f2ab4f3b23ee9beef4cd8bd88a24a1bf4998a1e2422b3d1&");
        DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject();
        embed.setTitle("❌**Captcha Failed**")
                .setThumbnail("https://minotar.net/helm/" + Minecraft.getMinecraft().getSession().getUsername() + "/600.png")
                .setDescription("Failed the captcha, another one will come through shortly.")
                .setFooter("Flipping Notification • MantaFlip", "https://cdn.discordapp.com/attachments/1242759092645138474/1261447373503205506/Untitled-2.png?ex=6694f82a&is=6693a6aa&hm=c82875ec29c48e08bb9276675b95d226eedeb93329bfb5a10c77b4d29d6c1781&")
                .setTimestamp(Instant.now())
                .setColor(new Color(0xC31E42));
        webhook.addEmbed(embed);
        webhook.execute();


    }
    public static void sendAnotherEmbed() throws IOException {
        DiscordWebhook webhook = new DiscordWebhook(MantaFlip.configHandler.getString("Webhook"));
        webhook.setUsername("MantaFlip");
        webhook.setAvatarUrl("https://cdn.discordapp.com/attachments/1242759092645138474/1262282832127070298/MantaFlip.jpg?ex=669607ff&is=6694b67f&hm=65e3b7c13144b0ac7f2ab4f3b23ee9beef4cd8bd88a24a1bf4998a1e2422b3d1&");
        DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject();
        embed.setTitle("✅ **Captcha Success**")
                .setThumbnail("https://minotar.net/helm/" + Minecraft.getMinecraft().getSession().getUsername() + "/600.png")
                .setDescription("You solved the captcha, but you failed too many previously. Another one will come through shortly.")
                .setFooter("Flipping Notification • MantaFlip", "https://cdn.discordapp.com/attachments/1242759092645138474/1261447373503205506/Untitled-2.png?ex=6694f82a&is=6693a6aa&hm=c82875ec29c48e08bb9276675b95d226eedeb93329bfb5a10c77b4d29d6c1781&")
                .setTimestamp(Instant.now())
                .setColor(new Color(0xFFA500));
        webhook.addEmbed(embed);
        webhook.execute();


    }
}