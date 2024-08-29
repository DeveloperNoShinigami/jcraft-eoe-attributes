package net.arna.jcraft.forge.mixin.client;

import net.arna.jcraft.JCraft;
import net.arna.jcraft.platform.JComponentPlatformUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ForgeGui.class)
public class ForgeGuiMixin {
    @Unique
    private static final ResourceLocation EMPTY_BLOOD_ICON = JCraft.id("textures/gui/blood_empty.png");
    @Unique
    private static final ResourceLocation HALF_BLOOD_ICON = JCraft.id("textures/gui/blood_half.png");
    @Unique
    private static final ResourceLocation FULL_BLOOD_ICON = JCraft.id("textures/gui/blood_full.png");
    @Unique
    private ResourceLocation currentBloodIcon = EMPTY_BLOOD_ICON;

    @Redirect(
            method = "renderFood",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIIIII)V"
            ),
            require = 3
    )
    void showVampireBloodIcons(GuiGraphics instance, ResourceLocation texture, int x, int y, int u, int v, int width, int height) {
        Player player = Minecraft.getInstance().player;
        if (JComponentPlatformUtils.getVampirism(player).isVampire()) {
            instance.blit(currentBloodIcon, x, y, 0, 0, width, height, 9, 9);
        } else {
            instance.blit(texture, x, y, u, v, width, height);
        }
    }

    @Inject(
            method = "renderFood",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIIIII)V",
                    ordinal = 0
            )
    )
    void switchToEmptyBloodIcon(int width, int height, GuiGraphics context, CallbackInfo ci) {
        currentBloodIcon = EMPTY_BLOOD_ICON;
    }

    @Inject(
            method = "renderFood",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIIIII)V",
                    ordinal = 2
            )
    )
    void switchToHalfBloodIcon(int width, int height, GuiGraphics context, CallbackInfo ci) {
        currentBloodIcon = HALF_BLOOD_ICON;
    }

    @Inject(
            method = "renderFood",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIIIII)V",
                    ordinal = 1
            )
    )
    void switchToFullBloodIcon(int width, int height, GuiGraphics context, CallbackInfo ci) {
        currentBloodIcon = FULL_BLOOD_ICON;
    }
}
