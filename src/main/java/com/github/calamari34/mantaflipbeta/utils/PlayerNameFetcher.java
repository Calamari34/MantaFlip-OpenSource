package com.github.calamari34.mantaflipbeta.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class PlayerNameFetcher {

    public static String getPlayerNameByUUID(String uuid) {

        String sanitizedUuid = uuid.replace("-", "");
        String baseUrl = "https://sky.coflnet.com/api/player/";
        String urlString = baseUrl + sanitizedUuid + "/name";
        StringBuilder response = new StringBuilder();

        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // Check if the response contains the error message for an invalid UUID
            JsonObject jsonResponse = JsonParser.parseString(response.toString()).getAsJsonObject();
            if (jsonResponse.has("slug") && "invalid_uuid".equals(jsonResponse.get("slug").getAsString())) {
                return null; // UUID is invalid
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null; // or handle error appropriately
        }

        return response.toString().replaceAll("\"", ""); // Remove quotes from the response
    }
}
