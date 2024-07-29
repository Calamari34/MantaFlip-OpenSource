package com.github.calamari34.mantaflipbeta.mixin;

import net.minecraft.client.gui.GuiMainMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiMainMenu.class)
public class MixinGuiMainMenu {

    @Inject(method = "initGui", at = @At("HEAD"))
    public void onInitGui(CallbackInfo ci) {
        // Check if the player instance is available
//        if (Minecraft.getMinecraft().thePlayer != null) {
//            UUID playerUUID = Minecraft.getMinecraft().thePlayer.getUniqueID();
//            try {
//                boolean isWhitelisted = FirestoreClient.isWhitelisted(playerUUID.toString());
//                if (isWhitelisted) {
//                    System.out.println("UUID is whitelisted.");
//                } else {
//                    System.out.println("UUID is not whitelisted.");
//                    CrashReport crashReport = new CrashReport("This is a test crash!", new RuntimeException("This is a test crash!"));
//                    Minecraft.getMinecraft().crashed(crashReport);
//                    throw new ReportedException(crashReport);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//                System.out.println("An error occurred while checking whitelist status.");
//            }
//        } else {
//            System.out.println("Player instance not available.");
//        }
    }
}
