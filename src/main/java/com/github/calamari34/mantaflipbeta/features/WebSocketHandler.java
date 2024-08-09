//package com.github.calamari34.mantaflipbeta.features;
//
//import com.google.gson.Gson;
//import com.google.gson.JsonObject;
//import com.google.gson.JsonParser;
//import net.minecraft.client.Minecraft;
//import org.java_websocket.client.WebSocketClient;
//import org.java_websocket.handshake.ServerHandshake;
//
//import java.net.URI;
//import java.net.URISyntaxException;
//import java.util.UUID;
//
//import com.github.calamari34.mantaflipbeta.features.Cofl.Cofl;
//import static com.github.calamari34.mantaflipbeta.MantaFlip.config;
//import static com.github.calamari34.mantaflipbeta.config.AHConfig.US_INSTANCE;
//import static com.github.calamari34.mantaflipbeta.features.Cofl.Cofl.handleMessage;
//import static com.github.calamari34.mantaflipbeta.utils.Utils.sendMessage;
//
//public class WebSocketHandler extends WebSocketClient {
//
//    private static final Gson gson = new Gson();
//    private boolean connected = false;
//
//    private static WebSocketHandler instance; // Static instance variable
//    private boolean reconnecting = false;
//
//    public WebSocketHandler(String serverUri) throws URISyntaxException {
//        super(new URI(serverUri));
//    }
//
//    public static WebSocketHandler getInstance(String serverUri) throws URISyntaxException {
//        if (instance == null) {
//            instance = new WebSocketHandler(serverUri);
//        }
//        return instance;
//    }
//
//    public void reconnect() {
//        if (!this.isOpen() && !reconnecting) { // Use 'this' to access the instance's state
//            reconnecting = true;
//            new Thread(() -> {
//                while (!this.isOpen()) { // Again, use 'this'
//                    try {
//                        this.reconnectBlocking();
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//                reconnecting = false;
//            }).start();
//        }
//    }
//
//    @Override
//    public void onOpen(ServerHandshake handshakedata) {
////        sendMessage("Connected to WebSocket server");
//        connected = true;
//        JsonObject message = new JsonObject();
//        message.addProperty("type", "flip");
//        message.addProperty("data", "\"always\"");
//
//        // Send the JSON message
//        send(message.toString());
//        // Handle other initialization tasks here, such as sending initial commands
//    }
//
//    @Override
//    public void onMessage(String message) {
//        System.out.println("Received message: " + message);
//        if (message.contains("\"type\":\"flip\"")) {
//            System.out.println("Received flip message: " + message);
////            sendMessage("Received flip message: " + message);
//            handleMessage(message);
//        }
//    }
//
//
//    @Override
//    public void onClose(int code, String reason, boolean remote) {
////        sendMessage("Disconnected from WebSocket server: " + reason);
//        connected = false;
//        // Attempt to reconnect
//        try {
//            Thread.sleep(5000);
//            reconnect();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public void onError(Exception ex) {
////        sendMessage("WebSocket error: " + ex.getMessage());
//        if (connected) {
//            close();
//        }
//    }
//
//    private void parseMessage(String message) {
//        JsonObject msg = JsonParser.parseString(message).getAsJsonObject();
//        if (!msg.has("type")) return;
//
//        String type = msg.get("type").getAsString();
//        switch (type) {
//            case "flip":
//                sendMessage("Received flip message: " + msg);
//                break;
//            case "chatMessage":
//                sendMessage("Received chat message: " + msg);
//                handleChatMessage(msg);
//                break;
//            // Handle other message types here
//            default:
//                System.out.println("Unhandled message type: " + type);
//        }
//    }
//
//
//
//    private void handleChatMessage(JsonObject msg) {
//        // Handle chat message
//    }
//
//    // Add more handlers for different message types as needed
//
//
//}
