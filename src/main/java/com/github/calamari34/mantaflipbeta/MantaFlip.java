package com.github.calamari34.mantaflipbeta;
//import com.github.calamari34.mantaflipbeta.config.AHConfig;
//import com.github.calamari34.mantaflipbeta.config.ConfigHandler;

import cc.polyfrost.oneconfig.events.event.InitializationEvent;
import cc.polyfrost.oneconfig.libs.eventbus.Subscribe;
import com.github.calamari34.mantaflipbeta.Ping.PlayerLoginHandler;
import com.github.calamari34.mantaflipbeta.config.AHConfig;
import com.github.calamari34.mantaflipbeta.config.ConfigHandler;
import com.github.calamari34.mantaflipbeta.events.GuiEventHandler;
import com.github.calamari34.mantaflipbeta.features.AuctionDetails;


import com.github.calamari34.mantaflipbeta.features.Cofl.CustomQueue;
import com.github.calamari34.mantaflipbeta.features.Cofl.QueueItem;
import com.github.calamari34.mantaflipbeta.features.PacketListener;
import com.github.calamari34.mantaflipbeta.features.Packets;
//import com.github.calamari34.mantaflipbeta.features.WebSocketHandler;
import com.github.calamari34.mantaflipbeta.player.CommandMFStart;
import com.github.calamari34.mantaflipbeta.remoteControl.RemoteControl;
import com.github.calamari34.mantaflipbeta.utils.ClassUtils;
import com.github.calamari34.mantaflipbeta.utils.Clock;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import cc.polyfrost.oneconfig.events.EventManager;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.net.URISyntaxException;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;
import com.github.calamari34.mantaflipbeta.events.ChatReceivedEvent;
import com.github.calamari34.mantaflipbeta.features.Cofl.Cofl;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import static com.github.calamari34.mantaflipbeta.config.AHConfig.*;

import static com.github.calamari34.mantaflipbeta.features.PacketListener.relisting;
import static com.github.calamari34.mantaflipbeta.utils.InventoryUtils.getInventoryName;
import static com.github.calamari34.mantaflipbeta.utils.Utils.sendMessage;

@Mod(modid = "mantaflipbeta", useMetadata=true)
public class MantaFlip {


    public static Boolean ToggleClaim = AUTO_CLAIM;
    public static Boolean ToggleRelist = AUTO_RELIST;
    private final Pattern AUCTION_SOLD_PATTERN = Pattern.compile("^(.*?) bought (.*?) for ([\\d,]+) coins CLICK$");
    public static List<AuctionDetails> auctionDetailsList = new ArrayList<>();
    public static boolean shouldRun = false;
    @Getter @Setter private boolean open = false;
    public static final Map<String, String> itemID = new HashMap<>();
    public static final Map<String, Integer> itemTargetPrices = new HashMap<>();
    public static final Map<String, String> itemDisplayName = new HashMap<>();
    public static final Map<String, String> itemFinder = new HashMap<>();
    public static final Map<String, Integer> itemProfit = new HashMap<>();

    public final ArrayList<HashMap<String, String>> sold_items = new ArrayList<>();
    public final ArrayList<HashMap<String, String>> bought_items = new ArrayList<>();


    private static final Pattern pattern = Pattern.compile("type[\":]*(flip|FLIP|MS|SNIPE|RISKY|USER)");
    public static List<Long> timeIntervals = new ArrayList<>(); // Time intervals in minutes
    public static List<Double> profitValues = new ArrayList<>(); // Cumulative profit values

    public static double cumulativeProfit = 0;
//    public static WebSocketHandler webSocketHandler;
    public static long startTime;
    public static Cofl cofl;
    private final Clock clock = new Clock();
    public static final Minecraft mc = Minecraft.getMinecraft();
    public static RemoteControl remoteControl;
    public static AHConfig config;
    public static ConfigHandler configHandler;
    public static Boolean startup = false;
    private int tickAmount;
    private final CustomQueue queue = new CustomQueue();

    private static MantaFlip instance;

    public static MantaFlip getInstance() {
        return instance;
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        instance = this;
    }


    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new Packets());
        MinecraftForge.EVENT_BUS.register(new GuiEventHandler());
        MinecraftForge.EVENT_BUS.register(this);
        ChatReceivedEvent chatReceivedEvent = new ChatReceivedEvent();
        MinecraftForge.EVENT_BUS.register(chatReceivedEvent);
        (configHandler = new ConfigHandler()).init();
//        config = new AHConfig();
        (cofl = new Cofl()).onOpen();
        EventManager.INSTANCE.register(this);
        MinecraftForge.EVENT_BUS.register(new PacketListener());
        ClientCommandHandler.instance.registerCommand(new CommandMFStart());
        remoteControl = new RemoteControl();
        MinecraftForge.EVENT_BUS.register(new PlayerLoginHandler());
        MinecraftForge.EVENT_BUS.register(ClassUtils.getInstance());

        // Initialize and connect WebSocketHandler
//        try {
//            String ign = "PotatoesOnCrack";
//            String Sid = "s7cme-pvjd5s8-1q1q-1q13q-";
//            String serverUri = "ws://sky-us.coflnet.com/modsocket?version=1.5.5-Alpha&player="+ ign+"&SId=" + Sid;
//            webSocketHandler = WebSocketHandler.getInstance(serverUri);
//            webSocketHandler.connectBlocking();
//        } catch (URISyntaxException | InterruptedException e) {
//            e.printStackTrace();
//        }


    }
    public CustomQueue getQueue() {
        return this.queue;
    }

//    public static String serverUri;
//    public static void main(String[] args) {
//        String ign = Minecraft.getMinecraft().getSession().getUsername();
//        String Sid = "s7cme-pvjd5s8-1q1q-1q13q-";
//
//        try {
//            if (US_INSTANCE)
//            {
//                serverUri = "ws://sky-us.coflnet.com/modsocket?version=1.5.5-Alpha&player="+ ign+"&SId=" + Sid;
//            }
//            else {
//                serverUri = "wss://sky.coflnet.com/modsocket?version=1.5.5-Alpha&player="+ ign+"&SId=" + Sid;
//            }
//
//            WebSocketHandler client = new WebSocketHandler(serverUri);
//            client.connectBlocking();
//        } catch (URISyntaxException | InterruptedException e) {
//            e.printStackTrace();
//        }
//    }



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
    public static synchronized String GetItemFinder(String itemName) {
        return itemFinder.getOrDefault(itemName, itemName);
    }

    public static synchronized int getItemProfit(String itemName) {
        return itemProfit.getOrDefault(itemName, 0);
    }
    public static synchronized String getItemID(String itemName) {
        return itemID.getOrDefault(itemName, itemName);
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (mc.thePlayer == null || event.phase != TickEvent.Phase.START) return;
        tickAmount++;
        // Uncommented essential code to ensure they are executed
//        if (tickAmount % 20 == 0) Utils.checkFooter();
//        if (pageFlipper != null) pageFlipper.switchStates();
//        if (tickAmount % (RELIST_CHECK_TIMEOUT * 72_000) == 0 && ScoreboardUtils.getSidebarLines().stream().map(ScoreboardUtils::cleanSB).anyMatch(s -> s.contains("SKYBLOCK")) && AUTO_RELIST) {
//            PacketListener.shouldBeRelisting = true;
//            if (relisting = false)
//            {
//                relister.toggle();
//            }
//
//
//        }
//        if (claimer != null) claimer.onTick();
//        if (relister != null) relister.onTick();


        if (mc.currentScreen instanceof GuiDisconnected && clock.passed()) {
            clock.schedule(RECONNECT_DELAY * 1000L);
            FMLClientHandler.instance().connectToServer(new GuiMultiplayer(new GuiMainMenu()), new ServerData(" ", "mc.hypixel.net", false));
        }
    }


}
