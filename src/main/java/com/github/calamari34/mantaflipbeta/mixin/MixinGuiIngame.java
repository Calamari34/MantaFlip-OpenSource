//package com.github.calamari34.mantaflipbeta.mixin;
//
//import com.github.calamari34.mantaflipbeta.events.ScoreboardRenderEvent;
//import net.minecraft.client.gui.GuiIngame;
//import net.minecraft.client.gui.ScaledResolution;
//import net.minecraft.scoreboard.ScoreObjective;
//import net.minecraft.util.EnumChatFormatting;
//import net.minecraftforge.common.MinecraftForge;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.ModifyArg;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//
//import java.util.regex.Pattern;
//
//import static com.github.calamari34.mantaflipbeta.config.AHConfig.CUSTOM_SCOREBOARD;
//
///*
// Credits: Cephetir
// */
//@Mixin(GuiIngame.class)
//public class MixinGuiIngame {
//
//    @ModifyArg(method = "renderScoreboard", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;drawString(Ljava/lang/String;III)I"))
//    public String a(String text) {
//        String txt = keepScoreboardCharacters(stripColor(text)).trim();
//        if (txt.startsWith("www") && CUSTOM_SCOREBOARD) return "  &b&l&oMantaFlip  ";
//        if (txt.startsWith("SKY") && CUSTOM_SCOREBOARD) return "   §d§lMantaFlip   ";
//        if (Pattern.compile("\\d{2}/\\d{2}/\\d{2}").matcher(txt).find()) return txt.split(" ")[0];
//        if (text.startsWith(String.valueOf(EnumChatFormatting.RED)) && Pattern.compile("\\d+").matcher(txt).matches()) return "";
//        else return text;
//    }
//
//    @Inject(method = "renderScoreboard", at = @At("HEAD"), cancellable = true)
//    public void renderScoreboard(ScoreObjective s, ScaledResolution score, CallbackInfo ci) {
//        if (MinecraftForge.EVENT_BUS.post(new ScoreboardRenderEvent(s, score))) ci.cancel();
//    }
//
//    public String keepScoreboardCharacters(String str) {
//        return str.replaceAll("[^a-z A-Z:\\d/'.]", "");
//    }
//
//    public String stripColor(String str) {
//        return str.replaceAll("(?i)§[\\dA-FK-OR]", "");
//    }
//}