package com.github.calamari34.mantaflipbeta;
//import com.github.calamari34.mantaflipbeta.config.AHConfig;
//import com.github.calamari34.mantaflipbeta.config.ConfigHandler;

import cc.polyfrost.oneconfig.events.event.InitializationEvent;
import cc.polyfrost.oneconfig.libs.eventbus.Subscribe;
import com.github.calamari34.mantaflipbeta.Auth.PlayerLoginHandler;
import com.github.calamari34.mantaflipbeta.config.AHConfig;
import com.github.calamari34.mantaflipbeta.config.ConfigHandler;
import com.github.calamari34.mantaflipbeta.features.AuctionDetails;
import com.github.calamari34.mantaflipbeta.features.PacketListener;
import com.github.calamari34.mantaflipbeta.player.CommandMFStart;
import com.github.calamari34.mantaflipbeta.remoteControl.RemoteControl;
import com.github.calamari34.mantaflipbeta.utils.Clock;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

import java.util.*;
import java.util.List;
import java.util.regex.Pattern;
import com.github.calamari34.mantaflipbeta.events.ChatReceivedEvent;
import com.github.calamari34.mantaflipbeta.features.Cofl.Cofl;
@Mod(modid = "mantaflipbeta", useMetadata=true)
public class MantaFlip {


    public static Boolean ToggleClaim = true;
    public static Boolean ToggleRelist = true;
    private final Pattern AUCTION_SOLD_PATTERN = Pattern.compile("^(.*?) bought (.*?) for ([\\d,]+) coins CLICK$");
    public static List<AuctionDetails> auctionDetailsList = new ArrayList<>();
    public static boolean shouldRun = false;
    @Getter @Setter private boolean open = false;
    public static final Map<String, String> itemID = new HashMap<>();
    public static final Map<String, Integer> itemTargetPrices = new HashMap<>();
    public static final Map<String, String> itemDisplayName = new HashMap<>();
    public static final Map<String, Integer> itemProfit = new HashMap<>();

    public final ArrayList<HashMap<String, String>> sold_items = new ArrayList<>();
    public final ArrayList<HashMap<String, String>> bought_items = new ArrayList<>();


    private static final Pattern pattern = Pattern.compile("type[\":]*(flip|FLIP|MS|SNIPE|RISKY|USER)");
    public static List<Long> timeIntervals = new ArrayList<>(); // Time intervals in minutes
    public static List<Double> profitValues = new ArrayList<>(); // Cumulative profit values

    public static double cumulativeProfit = 0;

    public static long startTime;
    public static Cofl cofl;
    private final Clock clock = new Clock();
    public static final Minecraft mc = Minecraft.getMinecraft();
    public static RemoteControl remoteControl;
    public static AHConfig config;
    public static ConfigHandler configHandler;
    public static Boolean startup = false;


    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {

        ChatReceivedEvent chatReceivedEvent = new ChatReceivedEvent();
        MinecraftForge.EVENT_BUS.register(chatReceivedEvent);
        (configHandler = new ConfigHandler()).init();
        config = new AHConfig();
        (cofl = new Cofl()).onOpen();
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new PacketListener());
        ClientCommandHandler.instance.registerCommand(new CommandMFStart());
        remoteControl = new RemoteControl();
        MinecraftForge.EVENT_BUS.register(new PlayerLoginHandler());


    }

    @Subscribe
    public void initConfig(InitializationEvent ignore) {
        config = new AHConfig();
    }



    public static void updateProfit(int profit) {
        cumulativeProfit += profit;
        long currentTime = System.currentTimeMillis();
        long timeElapsed = (currentTime - startTime) / 1000 / 60; // Convert milliseconds to minutes
        timeIntervals.add(timeElapsed);
        profitValues.add(cumulativeProfit);
    }

    public static double getCumulativeProfit() {
        return cumulativeProfit;
    }

    public static String getWindow() {
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

    public static synchronized String GetItemDisplayName(String itemName) {
        return itemDisplayName.getOrDefault(itemName, itemName);
    }

    public static synchronized int getItemProfit(String itemName) {
        return itemProfit.getOrDefault(itemName, 0);
    }
    public static synchronized String getItemID(String itemName) {
        return itemID.getOrDefault(itemName, itemName);
    }

}
