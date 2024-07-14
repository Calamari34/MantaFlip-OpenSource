package com.github.calamari34.mantaflipbeta.player;

import com.github.calamari34.mantaflipbeta.MantaFlip;
import com.github.calamari34.mantaflipbeta.features.Claimer;
import com.github.calamari34.mantaflipbeta.features.PacketListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.client.event.GuiOpenEvent;

import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.github.calamari34.mantaflipbeta.utils.Utils.sendMessage;

public class CommandMFStart extends CommandBase {

    @Override
    public String getCommandName() {
        return "mf";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        System.out.println("Command /mf executed with args: " + Arrays.toString(args));
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("start")) {
                MantaFlip.shouldRun = true;
                sendMessage("Starting macro");

                startMFProcess(sender, new GuiOpenEvent(null));
            } else if (args[0].equalsIgnoreCase("stop")) {
                MantaFlip.shouldRun = false;

                sendMessage("Stopping macro");
            } else {
                sendMessage("Invalid usage. Use /mf <start|stop>");

            }
        } else {
            sendMessage("Invalid usage. Use /mf <start|stop>");
        }
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();


    private void startMFProcess(ICommandSender sender, GuiOpenEvent event) {
        executorService.schedule(() -> {
            if (MantaFlip.shouldRun) {

                                sendMessage("Warping to your island");
                Minecraft.getMinecraft().thePlayer.sendChatMessage("/is");

                try {
                    Thread.sleep(6000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(e);
                }

                sendMessage("Claiming sold auctions");
                MantaFlip.shouldRun = false;


                PacketListener.claimAuctions((GuiChest) event.gui);
            }
        }, 2, TimeUnit.SECONDS);

        // Start a new thread that checks the screen status every second
        new Thread(() -> {
            int nullScreenSeconds = 0;
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }

                if (Minecraft.getMinecraft().currentScreen == null) {
                    nullScreenSeconds++;
                } else {
                    nullScreenSeconds = 0;
                }

                if (nullScreenSeconds >= 10) {
                    MantaFlip.shouldRun = true;
                }
            }
        }).start();
    }
}