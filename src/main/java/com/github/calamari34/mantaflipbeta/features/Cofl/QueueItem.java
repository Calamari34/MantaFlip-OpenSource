package com.github.calamari34.mantaflipbeta.features.Cofl;

import com.github.calamari34.mantaflipbeta.MantaFlip;
import com.github.calamari34.mantaflipbeta.utils.Utils;
import lombok.Getter;
import net.minecraft.client.Minecraft;

import static com.github.calamari34.mantaflipbeta.utils.Utils.sendMessage;

public class QueueItem {
    private final String command;

    public QueueItem(String command) {
        this.command = command;
    }

    public void openAuction() {
        MantaFlip.shouldRun = true;

        Minecraft.getMinecraft().thePlayer.sendChatMessage("/viewauction " + command);
    }


}