package com.github.calamari34.mantaflipbeta.utils;

import com.github.calamari34.mantaflipbeta.MantaFlip;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.network.play.server.S45PacketTitle;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import java.awt.*;
import java.net.URI;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
public class Utils {

    private static final Pattern PATTERN_PURSE = Pattern.compile("(Purse|Piggy): (?:§.)?([0-9.,]+)");

    public static int getPurse() {
        String purse = "";
        List<String> matches = ScoreboardUtils.getSidebarLines().stream().map(ScoreboardUtils::cleanSB).map(PATTERN_PURSE::matcher).filter(Matcher::find).map(Matcher::group).collect(Collectors.toList());
        String purseline = matches.get(0);
        Matcher matcher = PATTERN_PURSE.matcher(purseline);
        if (matcher.find()) {
            purse = matcher.group(2);
            purse = purse.replace(",", "");
            purse = purse.replaceAll("\\..*", "");
            return Integer.parseInt(purse);
        }
        return Integer.parseInt(purse);

    }

    public static void sendMessage(String message) {
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA + String.valueOf(EnumChatFormatting.BOLD) + "[MantaFlip] " + EnumChatFormatting.RESET + EnumChatFormatting.WHITE + message));
    }



    public static String formatNumbers(int number) {
        if (number >= 1_000_000_000) {
            return number / 1_000_000_000 + "B";
        } else if (number >= 1_000_000) {
            return number / 1_000_000 + "M";
        } else if (number >= 1_000) {
            return number / 1_000 + "K";
        } else {
            return String.valueOf(number);
        }
    }
    public static void sendServerMessage(String message) {
        Minecraft.getMinecraft().thePlayer.sendChatMessage(message);
    }

    public static String convertToShort(int number) {
        if (number >= 1_000_000_000) return formatNumber((double) number / 1_000_000_000, "b", 4);
        else if (number >= 100_000_000) return formatNumber((double) number / 1_000_000, "m", 4);
        else if (number >= 1_000_000) return formatNumber((double) number / 1_000_000, "m", 3);
        else if (number >= 1_000) return formatNumber((double) number / 1_000, "k", 3);
        else return Integer.toString(number);
    }
    public static String formatNumber(double number, String suffix, int significantFigures) {
        String format = "%." + significantFigures + "g";
        return String.format(format, number) + suffix;
    }


}


