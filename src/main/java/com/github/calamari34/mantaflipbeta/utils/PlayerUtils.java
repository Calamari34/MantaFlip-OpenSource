package com.github.calamari34.mantaflipbeta.utils;

import com.github.calamari34.mantaflipbeta.MantaFlip;
import net.minecraft.network.Packet;

import java.util.ArrayList;

public class PlayerUtils {
    public static final ArrayList<Packet<?>> packets = new ArrayList<>();

    public static void sendPacketWithoutEvent(Packet<?> packet) {
        packets.add(packet);
        MantaFlip.mc.getNetHandler().getNetworkManager().sendPacket(packet);
    }
}
