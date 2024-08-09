package com.github.calamari34.mantaflipbeta.player;

import com.github.calamari34.mantaflipbeta.MantaFlip;
import com.github.calamari34.mantaflipbeta.features.PacketListener;
//import com.github.calamari34.mantaflipbeta.features.WebSocketHandler;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.client.event.GuiOpenEvent;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

//import static com.github.calamari34.mantaflipbeta.MantaFlip.webSocketHandler;
import static com.github.calamari34.mantaflipbeta.utils.Utils.sendMessage;

public class CommandMFStart extends CommandBase {
//    private WebSocketHandler webSocketHandler;

    @Override
    public String getCommandName() {
        return "mf";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/mf <start|stop|connect>";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        System.out.println("Command /mf executed with args: " + Arrays.toString(args));
        if (args.length > 0) {
            switch (args[0].toLowerCase()) {
                case "start":
                    MantaFlip.shouldRun = true;
                    sendMessage("Starting macro");
                    startMFProcess(sender, new GuiOpenEvent(null));
                    break;
                case "stop":
                    MantaFlip.shouldRun = false;
                    sendMessage("Stopping macro");
                    break;
//                case "connect":
//                    connectToWebSocket();
//                    break;
                default:
                    sendMessage("Invalid usage. Use /mf <start|stop|connect>");
            }
        } else {
            sendMessage("Invalid usage. Use /mf <start|stop|connect>");
        }
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    private void startMFProcess(ICommandSender sender, GuiOpenEvent event) {
        executorService.schedule(() -> {
            sendMessage("Warping to your island");
            Minecraft.getMinecraft().thePlayer.sendChatMessage("/is");

            try {
                Thread.sleep(6000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }

            sendMessage("Claiming sold auctions");
            MantaFlip.shouldRun = true;
            PacketListener.claimAuctions((GuiChest) event.gui);
        }, 2, TimeUnit.SECONDS);
    }

//    private void connectToWebSocket() {
//        String ign = Minecraft.getMinecraft().getSession().getUsername();
//        String Sid = "s7cme-pvjd5s8-1q1q-1q13q-";
//        String serverUri;
//
//        try {
//            if (MantaFlip.configHandler.getBoolean("US_INSTANCE")) {
//                serverUri = "ws://sky-us.coflnet.com/modsocket?version=1.5.5-Alpha&player=" + ign + "&SId=" + Sid;
//            } else {
//                serverUri = "wss://sky.coflnet.com/modsocket?version=1.5.5-Alpha&player=" + ign + "&SId=" + Sid;
//            }
//
//            webSocketHandler = new WebSocketHandler(serverUri);
//            webSocketHandler.connectBlocking();
//            JsonObject message = new JsonObject();
//            message.addProperty("type", "flip");
//            message.addProperty("data", "\"always\"");
//
//            // Send the JSON message using WebSocketHandler's send method
//            webSocketHandler.send(message.toString());
//            sendMessage("Connected to WebSocket server");
//        } catch (URISyntaxException | InterruptedException e) {
//            e.printStackTrace();
//            sendMessage("Failed to connect to WebSocket server: " + e.getMessage());
//        }
//    }
}