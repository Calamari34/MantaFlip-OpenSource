package com.github.calamari34.mantaflipbeta.utils;

import net.minecraft.client.Minecraft;
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
            System.out.println("Purse: " + purse);
            return Integer.parseInt(purse);
        }
        return Integer.parseInt(purse);

    }

    public static void sendMessage(String message) {
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA + String.valueOf(EnumChatFormatting.BOLD) + "[MantaFlip] " + EnumChatFormatting.RESET + EnumChatFormatting.WHITE + message));
    }

    public static String formatNumber(int number) {
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
}
