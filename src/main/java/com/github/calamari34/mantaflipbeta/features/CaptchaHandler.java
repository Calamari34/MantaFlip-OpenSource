package com.github.calamari34.mantaflipbeta.features;

import cc.polyfrost.oneconfig.libs.universal.ChatColor;
import com.google.gson.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraftforge.client.ClientCommandHandler;

import java.io.IOException;
import java.util.*;

import static com.github.calamari34.mantaflipbeta.MantaFlip.mc;

import static com.github.calamari34.mantaflipbeta.features.WebhookSend.*;
import static com.github.calamari34.mantaflipbeta.utils.Utils.removeChatColors;
import static com.github.calamari34.mantaflipbeta.utils.Utils.sendMessage;

public class CaptchaHandler {

    private static final String COLOR_CODE_REGEX = "§.";
    private static final String WEIRD_CHAR = "🇧🇾";
    private static final List<String> onClickActions = new ArrayList<>();


    public static void handleCaptcha(String message) throws IOException {

        String[] split = message.split("Received: ");
        if (split.length < 2) {

            return;
        }

        JsonObject parsing;
        try {
            // Parse the JSON object from the split string
            parsing = new JsonParser().parse(split[1]).getAsJsonObject();
        } catch (JsonSyntaxException e) {

            return;
        }

        // Convert the JSON object to string and check for captcha commands



        JsonObject jsonData = new JsonParser().parse(split[1]).getAsJsonObject();
        String dataString = jsonData.get("data").toString();

        String cleanData = dataString.substring(1, dataString.length() - 1).replace("\\\"", "\"");
        cleanData = stripColor(cleanData);

        if (cleanData.contains("Click to get a letter captcha to prove you are not.") && !cleanData.contains("You are currently delayed for likely being afk")) {
//            Minecraft.getMinecraft().displayGuiScreen(new GuiCaptcha(DiscordIntegration.captchaGui));
            Minecraft.getMinecraft().thePlayer.sendChatMessage("/cofl captcha vertical");
        } else if (cleanData.contains("/cofl captcha ")) {
            onClickActions.clear();

            JsonArray jsonArray = new JsonParser().parse(cleanData).getAsJsonArray();

            List<String> columnClicks = new ArrayList<>();
            int currentRow = 0;
            StringBuilder captchaText = new StringBuilder();
            List<String> currentRowClicks = new ArrayList<>();
            Map<Integer, String> lineToCodeMap = new HashMap<>(); // Line number to code mapping
            int currentLineNumber = 1;

            for (JsonElement element : jsonArray) {
                JsonObject jsonObject = element.getAsJsonObject();
                String text = jsonObject.get("text").getAsString().replace("\\n", "\n").replace("🇧", "").replace("🇾", "");
                captchaText.append(text);
                if (!jsonObject.get("onClick").isJsonNull()) {
                    if (onClickActions.size() < currentRow) {
                        onClickActions.add(jsonObject.get("onClick").getAsString());
                    }

                    for (String character : text.split("")) {
                        int spacesToAdd = 0;
                        if (character.matches("�")) {
                            spacesToAdd = 8;
                        } else if (character.equals("⋅")) {
                            spacesToAdd = 4;
                        } else if (character.equals(" ")) {
                            spacesToAdd = 8;
                        }

                        for (int i = 0; i < spacesToAdd; ++i) {
                            if (!jsonObject.get("onClick").getAsString().contains("config")) {
                                currentRowClicks.add(jsonObject.get("onClick").getAsString());
                            }
                        }
                    }
                }


                if (text.contains("\n")) {

                    ++currentRow;
                    if (currentRowClicks.size() > columnClicks.size()) {
                        columnClicks.clear();
                        columnClicks.addAll(currentRowClicks);
                    }


                    currentRowClicks = new ArrayList<>();
                }
            }


            HashMap<String, Integer> characterWidths = new HashMap<>();

            for (char c : captchaText.toString().toCharArray()) {
                if (!characterWidths.containsKey(String.valueOf(c))) {
                    characterWidths.put(String.valueOf(c), Minecraft.getMinecraft().fontRendererObj.getStringWidth(String.valueOf(c)));
                }
            }

            JsonObject captchaData = new JsonObject();
            captchaData.addProperty("captcha", captchaText.toString());
            captchaData.addProperty("onClicks", onClickActions.toString());
            captchaData.addProperty("clickColumns", columnClicks.toString());
            captchaData.addProperty("lengths", characterWidths.toString());


            System.out.println("Sending captcha data to webhook" + captchaData.toString());


            sendCaptchaWebhook(captchaText.toString());
        } else if (cleanData.contains("Thanks for confirming that you are a real user")) {
            JsonObject successData = new JsonObject();
            sendSuccessEmbed();
        } else if (cleanData.contains("You solved the captcha, but you failed too many previously")) {
            JsonObject correctData = new JsonObject();
            sendAnotherEmbed();
        } else if (cleanData.contains("Your answer was not correct")) {
            JsonObject incorrectData = new JsonObject();
            sendFailedEmbed();
        }
    }


    public static String stripColor(String toStrip) {
        for (ChatColor color : ChatColor.values()) {
            toStrip = toStrip.replaceAll(color.toString(), "");
        }
        return toStrip;
    }

    public static String getOnClickAction(int index) {
        if (index > 0 && index <= onClickActions.size()) {

            return onClickActions.get(index - 1);

        } else {
            return "Invalid index";

        }
    }
    public static void sendOnClickActionToChat(int index) {
        String onClickAction = getOnClickAction(index);
        if (!onClickAction.equals("Invalid index")) {
            ClientCommandHandler commandHandler = ClientCommandHandler.instance;
            EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
            commandHandler.executeCommand(player, onClickAction);

//            Minecraft.getMinecraft().thePlayer.sendChatMessage(onClickAction);
//            Minecraft.getMinecraft().thePlayer.sendChatMessage(onClickAction);
        } else {
            System.out.println(onClickAction);
        }
    }
}
