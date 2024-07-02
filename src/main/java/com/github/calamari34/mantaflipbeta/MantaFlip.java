package com.github.calamari34.mantaflipbeta;
import com.github.calamari34.mantaflipbeta.features.AuctionDetails;
import com.github.calamari34.mantaflipbeta.features.PacketListener;
import com.github.calamari34.mantaflipbeta.player.CommandMFStart;
import com.google.gson.*;

import com.mojang.realmsclient.client.FileUpload;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.github.calamari34.mantaflipbeta.utils.Utils.sendMessage;
import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = "mantaflipbeta", useMetadata=true)
public class MantaFlip {

    private final Pattern AUCTION_SOLD_PATTERN = Pattern.compile("^(.*?) bought (.*?) for ([\\d,]+) coins CLICK$");
    private static List<AuctionDetails> auctionDetailsList = new ArrayList<>();
    public static boolean shouldRun = false;
    @Getter @Setter private boolean open = false;
    static final Map<String, Integer> itemTargetPrices = new HashMap<>();

    public final ArrayList<HashMap<String, String>> sold_items = new ArrayList<>();
    public final ArrayList<HashMap<String, String>> bought_items = new ArrayList<>();


    private static final Pattern pattern = Pattern.compile("type[\":]*(flip|FLIP|MS|SNIPE|RISKY|USER)");

    private static long startTime; // Ensure this is correctly set and used
    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new PacketListener());
        ClientCommandHandler.instance.registerCommand(new CommandMFStart());
    }

    public String getWindow() {
        String windowTitle = null;
        if (Minecraft.getMinecraft().currentScreen instanceof GuiChest) {
            GuiChest guiChest = (GuiChest) Minecraft.getMinecraft().currentScreen;
            ContainerChest chest = (ContainerChest) Minecraft.getMinecraft().thePlayer.openContainer;
            IInventory lowerChestInventory = chest.getLowerChestInventory();
            windowTitle = lowerChestInventory.getDisplayName().getUnformattedText();
        }
        return windowTitle;
    }

    public AuctionDetails findAuctionDetailsByItemName(String itemName) {
        for (AuctionDetails auctionDetails : auctionDetailsList) {
            if (auctionDetails.getItemName().equals(itemName)) {
                return auctionDetails;
            }
        }
        return null; // Return null if no AuctionDetails object with the given itemName is found
    }

    public static synchronized int getTargetPrice(String itemName) {
        return itemTargetPrices.getOrDefault(itemName, 0);
    }
}
