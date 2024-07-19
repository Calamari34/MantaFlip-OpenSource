package com.github.calamari34.mantaflipbeta.mixin;

import com.github.calamari34.mantaflipbeta.Auth.FirestoreClient;
import com.github.calamari34.mantaflipbeta.utils.PlayerNameFetcher;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.network.play.server.S40PacketDisconnect;
import net.minecraft.crash.CrashReport;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ReportedException;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.UUID;

import static com.github.calamari34.mantaflipbeta.MantaFlip.startup;
import static com.github.calamari34.mantaflipbeta.features.WebhookSend.resolveUsername;
import static com.github.calamari34.mantaflipbeta.features.WebhookSend.sendStartEmbed;
import static com.github.calamari34.mantaflipbeta.utils.Utils.sendMessage;

@Mixin(NetHandlerPlayClient.class)
public class MixinNetHandlerPlayClient {

    @Inject(method = "handleDisconnect", at = @At("RETURN"))
    private void onHandleDisconnect(S40PacketDisconnect packetIn, CallbackInfo ci) {
        // Log the disconnection reason to the console
        System.out.println("Disconnected from server: " + packetIn.getReason().getUnformattedText());
    }

    @Inject(method = "handleJoinGame", at = @At("RETURN"))
    public void onHandleJoinGame(S01PacketJoinGame packet, CallbackInfo ci) {
        if (!startup) {
            if (Minecraft.getMinecraft().thePlayer != null) {
                UUID playerUUID = Minecraft.getMinecraft().thePlayer.getUniqueID();
                System.out.println("Player UUID: " + playerUUID);
                try {
                    boolean isWhitelisted = FirestoreClient.isWhitelisted(playerUUID.toString());
                    String expiryDate = FirestoreClient.getExpiryDateForUUID(playerUUID.toString());

                    if (!expiryDate.equals("UUID not found")) {
                        ZonedDateTime zonedDateTime = ZonedDateTime.parse(expiryDate);
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy 'at' h:mm a");
                        String formattedDate = zonedDateTime.format(formatter);

                        if (isWhitelisted) {
                            String playerName = resolveUsername(playerUUID.toString());
                            sendMessage("Successfully verified as " + playerName + ". Your whitelist will expire on " + formattedDate + ".");
                            System.out.println("UUID is whitelisted.");

                            sendStartEmbed(playerName, formattedDate);
                            startup = true;
                        } else {
                            System.out.println("UUID is not whitelisted.");
                            CrashReport crashReport = new CrashReport("4Player not whitelisted", new RuntimeException("Player UUID: " + playerUUID + " is not whitelisted."));
                            Minecraft.getMinecraft().crashed(crashReport);
                            throw new ReportedException(crashReport);
                        }
                    } else {
                        System.out.println("UUID not found or expiry date is missing.");
                        CrashReport crashReport = new CrashReport("3Player not whitelisted", new RuntimeException("Player UUID: " + playerUUID + " is not whitelisted."));
                        Minecraft.getMinecraft().crashed(crashReport);
                        throw new ReportedException(crashReport);
                    }
                } catch (DateTimeParseException e) {
                    System.out.println("Failed to parse expiry date for UUID: " + playerUUID);
                    e.printStackTrace();
                    CrashReport crashReport = new CrashReport("2Player not whitelisted", new RuntimeException("Player UUID: " + playerUUID + " is not whitelisted."));
                    Minecraft.getMinecraft().crashed(crashReport);
                    throw new ReportedException(crashReport);
                } catch (Exception e) {
                    System.out.println("An error occurred while checking whitelist status for UUID: " + playerUUID);
                    e.printStackTrace();

                }
            } else {
                System.out.println("Player instance not available.");
            }
        }
    }
}