package com.github.calamari34.mantaflipbeta.features;

import com.google.gson.*;
import net.minecraft.client.Minecraft;

import java.util.*;

import static com.github.calamari34.mantaflipbeta.utils.Utils.removeChatColors;

public class CaptchaHandler {
    public static void handleCaptcha(String paramString) {
        System.out.println("CaptchaHandler called");
        JsonObject jsonObject = new JsonParser().parse(paramString).getAsJsonObject();
        String data = jsonObject.get("data").getAsString();
        String cleanedData = data.substring(1, data.length() - 1).replace("\\\"", "\"");
        cleanedData = removeChatColors(cleanedData);

        if (cleanedData.contains("Click to get a letter captcha to prove you are not.") && !cleanedData.contains("You are currently delayed for likely being afk")) {
            executeCommand("/cofl captcha vertical");
        } else if (cleanedData.contains("/cofl captcha ")) {
            processCaptcha(cleanedData);
        } else if (cleanedData.contains("Thanks for confirming that you are a real user")) {
            sendJsonResponse("CaptchaSuccess", new JsonObject());
        } else if (cleanedData.contains("You solved the captcha, but you failed too many previously")) {
            sendJsonResponse("CaptchaCorrect", new JsonObject());
        } else if (cleanedData.contains("Your answer was not correct")) {
            sendJsonResponse("CaptchaIncorrect", new JsonObject());
        }
    }

    private static void processCaptcha(String cleanedData) {
        JsonArray jsonArray = new JsonParser().parse(cleanedData).getAsJsonArray();
        List<String> onClicks = new ArrayList<>();
        List<String> clickColumns = new ArrayList<>();
        StringBuilder captchaText = new StringBuilder();
        List<String> currentClicks = new ArrayList<>();

        for (JsonElement element : jsonArray) {
            JsonObject jsonElement = element.getAsJsonObject();
            String text = jsonElement.get("text").getAsString().replace("\\n", "\n").replace("🇧", "").replace("🇾", "");
            captchaText.append(text);

            if (!jsonElement.get("onClick").isJsonNull()) {
                String onClick = jsonElement.get("onClick").getAsString();
                if (onClicks.isEmpty()) {
                    onClicks.add(onClick);
                }

                for (String s : text.split("")) {
                    if (!s.matches("�") && !s.equals("⋅")) {
                        currentClicks.add(onClick);
                    }
                }
            }

            if (text.contains("\n")) {
                if (currentClicks.size() > clickColumns.size()) {
                    clickColumns.clear();
                    clickColumns.addAll(currentClicks);
                }
                currentClicks.clear();
            }
        }

        Map<String, Integer> charLengths = new HashMap<>();
        for (char c : captchaText.toString().toCharArray()) {
            String charStr = String.valueOf(c);
            if (!charLengths.containsKey(charStr)) {
                charLengths.put(charStr, Minecraft.getMinecraft().fontRendererObj.getStringWidth(charStr));
            }
        }

        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("captcha", captchaText.toString());
        responseJson.addProperty("onClicks", onClicks.toString());
        responseJson.addProperty("clickColumns", clickColumns.toString());
        responseJson.addProperty("lengths", charLengths.toString());

        sendJsonResponse("Captcha", responseJson.toString());
    }

    private static void executeCommand(String command) {
        Minecraft.getMinecraft().thePlayer.sendChatMessage(command);
    }

    private static void sendJsonResponse(String type, String json) {
        // Implement your method to send the JSON response
        System.out.println(type + ": " + json);
    }

    private static void sendJsonResponse(String type, JsonObject jsonObject) {
        sendJsonResponse(type, jsonObject.toString());
    }
}