package com.github.calamari34.mantaflipbeta.Ping;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class FirestoreClient {
    private static final String FIRESTORE_URL = "https://firestore.googleapis.com/v1/projects/mantaflip-4f645/databases/(default)/documents/whitelist";

    public static boolean isWhitelisted(String uuid) throws Exception {
        URL url = new URL(FIRESTORE_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        int responseCode = conn.getResponseCode();
        if (responseCode == 200) {
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            JsonObject jsonResponse = new JsonParser().parse(response.toString()).getAsJsonObject();
            JsonArray documents = jsonResponse.getAsJsonArray("documents");

            for (JsonElement document : documents) {
                JsonObject fields = document.getAsJsonObject().getAsJsonObject("fields");
                JsonObject uuidField = fields.getAsJsonObject("uuid");
                JsonObject expiryDateField = fields.getAsJsonObject("expiryDate");

                if (uuidField != null && expiryDateField != null) {
                    String documentUuid = uuidField.get("stringValue").getAsString();
                    Instant expiryDate;
                    if (expiryDateField.has("timestampValue")) {
                        String expiryDateStr = expiryDateField.get("timestampValue").getAsString();
                        expiryDate = ZonedDateTime.parse(expiryDateStr, DateTimeFormatter.ISO_DATE_TIME).toInstant();
                    } else if (expiryDateField.has("stringValue")) {
                        String expiryDateStr = expiryDateField.get("stringValue").getAsString();
                        expiryDate = ZonedDateTime.parse(expiryDateStr, DateTimeFormatter.ISO_DATE_TIME).toInstant();
                    } else {
                        continue; // Skip this document if neither field is present
                    }

                    if (uuid.equalsIgnoreCase(documentUuid)) {
                        return Instant.now().isBefore(expiryDate);
                    }
                }
            }
        } else {
            System.out.println("High ping :(");
        }
        return false;
    }

    public static String getExpiryDateForUUID(String uuid) {
        try {
            URL url = new URL(FIRESTORE_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                JsonObject jsonResponse = new JsonParser().parse(response.toString()).getAsJsonObject();
                JsonArray documents = jsonResponse.getAsJsonArray("documents");

                for (JsonElement document : documents) {
                    JsonObject fields = document.getAsJsonObject().getAsJsonObject("fields");
                    JsonObject uuidField = fields.getAsJsonObject("uuid");
                    JsonObject expiryDateField = fields.getAsJsonObject("expiryDate");

                    if (uuidField != null && uuidField.get("stringValue").getAsString().equalsIgnoreCase(uuid)) {
                        if (expiryDateField != null) {
                            if (expiryDateField.has("timestampValue")) {
                                return expiryDateField.get("timestampValue").getAsString();
                            } else if (expiryDateField.has("stringValue")) {
                                return expiryDateField.get("stringValue").getAsString();
                            }
                        }
                        break; // UUID found, exit loop
                    }
                }
            } else {
                System.out.println("Wow really high ping :(");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
        return "not found";
    }
}