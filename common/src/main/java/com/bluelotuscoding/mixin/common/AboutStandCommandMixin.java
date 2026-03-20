package com.bluelotuscoding.mixin.common;

import com.bluelotuscoding.util.MoveContext;
import com.mojang.brigadier.context.CommandContext;
// net.arna.jcraft.common.command.AboutStandCommand not needed with targets approach
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Class: AboutStandCommand
 * Stand: All
 * Purpose: Captures the player context during the 'about' command to enable dynamic move descriptions.
 */
@Mixin(targets = "net.arna.jcraft.common.command.AboutStandCommand", remap = false)
public abstract class AboutStandCommandMixin {

    @Inject(method = "run(Lcom/mojang/brigadier/context/CommandContext;)I", at = @At("HEAD"), remap = false)
    private static void jcraft_attributes$captureContext(CommandContext<CommandSourceStack> context, CallbackInfoReturnable<Integer> cir) {
        try {
            ServerPlayer player = context.getSource().getPlayerOrException();
            MoveContext.setPlayer(player);
        } catch (Exception e) {
            // Log the exception if necessary, but remove System.out.println
        }
    }

    @Inject(method = "run(Lcom/mojang/brigadier/context/CommandContext;)I", at = @At("RETURN"), remap = false)
    private static void jcraft_attributes$clearContext(CommandContext<CommandSourceStack> context, CallbackInfoReturnable<Integer> cir) {
        MoveContext.clear();
    }
}
