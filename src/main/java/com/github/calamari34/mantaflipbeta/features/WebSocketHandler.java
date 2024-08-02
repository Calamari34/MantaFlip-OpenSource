//package com.github.calamari34.mantaflipbeta.features;
//
//import org.java_websocket.WebSocketImpl;
//import org.java_websocket.client.WebSocketClient;
//import org.java_websocket.framing.Framedata;
//import org.java_websocket.handshake.ServerHandshake;
//import java.net.URI;
//import java.net.URISyntaxException;
//
//import static com.github.calamari34.mantaflipbeta.features.Cofl.Cofl.handleMessage;
//import static com.github.calamari34.mantaflipbeta.utils.Utils.sendMessage;
//
//public class WebSocketHandler extends WebSocketClient {
//
//    public WebSocketHandler(String serverUri) throws URISyntaxException {
//        super(new URI(serverUri));
//    }
//
//    @Override
//    public void onOpen(ServerHandshake handshakedata) {
//        System.out.println("WebSocket opened");
//    }
//
//    @Override
//    public void onMessage(String message) {
////        sendMessage("Received message: " + message);
//        if (message.contains("\"type\":\"flip\"")) {
//            handleMessage(message);
//        }
//    }
//
//    @Override
//    public void onClose(int code, String reason, boolean remote) {
//        System.out.println("WebSocket closed with exit code " + code + " additional info: " + reason);
//    }
//
//    @Override
//    public void onError(Exception ex) {
//        ex.printStackTrace();
//    }
//
//
//
//    public void sendPing() {
//        try {
//            this.sendPing();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}