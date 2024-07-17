package com.github.calamari34.mantaflipbeta.Auth;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import java.util.UUID;

public class PlayerLoginHandler {

    @SubscribeEvent
    public void onPlayerLogin(PlayerLoggedInEvent event) {

        EntityPlayerMP player = (EntityPlayerMP) event.player;
        UUID playerUUID = player.getUniqueID();
        try {
            boolean isWhitelisted = FirestoreClient.isWhitelisted(playerUUID.toString());
            if (!isWhitelisted) {
                System.out.println("Not whitelisted");
                throw new RuntimeException("Player " + player.getName() + " is not whitelisted.");
            } else {
                System.out.println("Player whitelisted!");
                throw new RuntimeException("This is a test crash!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("An error occurred while checking whitelist status for player " + player.getName(), e);
        }
    }
}