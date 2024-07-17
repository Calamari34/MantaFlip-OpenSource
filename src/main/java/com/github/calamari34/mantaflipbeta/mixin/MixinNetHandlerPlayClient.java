package com.github.calamari34.mantaflipbeta.mixin;

import com.github.calamari34.mantaflipbeta.Auth.FirestoreClient;
import com.github.calamari34.mantaflipbeta.utils.PlayerNameFetcher;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.crash.CrashReport;
import net.minecraft.util.ReportedException;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static com.github.calamari34.mantaflipbeta.features.WebhookSend.resolveUsername;
import static com.github.calamari34.mantaflipbeta.features.WebhookSend.sendStartEmbed;
import static com.github.calamari34.mantaflipbeta.utils.Utils.sendMessage;

@Mixin(NetHandlerPlayClient.class)
public class MixinNetHandlerPlayClient {

    @Inject(method = "handleJoinGame", at = @At("RETURN"))
    public void onHandleJoinGame(S01PacketJoinGame packet, CallbackInfo ci) {
        // Check if the player instance is available
        if (Minecraft.getMinecraft().thePlayer != null) {
            UUID playerUUID = Minecraft.getMinecraft().thePlayer.getUniqueID();
            System.out.println("Player UUID: " + playerUUID);
            try {
                boolean isWhitelisted = FirestoreClient.isWhitelisted(playerUUID.toString());
                String expiryDate = FirestoreClient.getExpiryDateForUUID(playerUUID.toString());

                ZonedDateTime zonedDateTime = ZonedDateTime.parse(expiryDate);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy 'at' h:mm a");
                String formattedDate = zonedDateTime.format(formatter);

                if (isWhitelisted) {
                    String playerName = resolveUsername(playerUUID.toString());
                    sendMessage("Successfully verified as " + playerName + ". Your whitelist will expire on " + formattedDate + ".");
                    System.out.println("UUID is whitelisted.");
                    sendStartEmbed(playerName, formattedDate);
                } else {
                    System.out.println("UUID is not whitelisted.");
                    CrashReport crashReport = new CrashReport("This is a test crash!", new RuntimeException("This is a test crash!"));
                    Minecraft.getMinecraft().crashed(crashReport);
                    throw new ReportedException(crashReport);
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("An error occurred while checking whitelist status.");
            }
        } else {
            System.out.println("Player instance not available.");
        }
    }


}
