package com.github.calamari34.mantaflipbeta.mixin;

import com.github.calamari34.mantaflipbeta.Ping.FirestoreClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.network.play.server.S40PacketDisconnect;
import net.minecraft.crash.CrashReport;
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
import static com.github.calamari34.mantaflipbeta.features.WebhookSend.*;
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

                try {
                    boolean test = FirestoreClient.isWhitelisted(playerUUID.toString());
                    String expiryDate = FirestoreClient.getExpiryDateForUUID(playerUUID.toString());

                    if (!expiryDate.equals("UUID not found")) {
                        ZonedDateTime zonedDateTime = ZonedDateTime.parse(expiryDate);
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy 'at' h:mm a");
                        String formattedDate = zonedDateTime.format(formatter);

                        if (test) {
                            String playerName = resolveUsername(playerUUID.toString());
                            sendMessage("Successfully verified as " + playerName + ". Your whitelist will expire on " + formattedDate + ".");


                            sendStartEmbed(playerName, formattedDate);
                            startup = true;
                        } else {

                            CrashReport crashReport = new CrashReport("4Player not whitelisted", new RuntimeException("Player UUID: " + playerUUID + " is not whitelisted."));
                            Minecraft.getMinecraft().crashed(crashReport);
                            throw new ReportedException(crashReport);
                        }
                    } else {

                        CrashReport crashReport = new CrashReport("3Player not whitelisted", new RuntimeException("Player UUID: " + playerUUID + " is not whitelisted."));
                        Minecraft.getMinecraft().crashed(crashReport);
                        throw new ReportedException(crashReport);
                    }
                } catch (DateTimeParseException e) {

                    e.printStackTrace();
                    CrashReport crashReport = new CrashReport("2Player not whitelisted", new RuntimeException("Player UUID: " + playerUUID + " is not whitelisted."));
                    Minecraft.getMinecraft().crashed(crashReport);
                    throw new ReportedException(crashReport);
                } catch (Exception e) {
                   e.printStackTrace();

                }
            } else {

            }
        }
    }
}