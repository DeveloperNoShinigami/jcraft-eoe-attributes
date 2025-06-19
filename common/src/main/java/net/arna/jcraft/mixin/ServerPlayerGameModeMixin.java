package net.arna.jcraft.mixin;

import net.arna.jcraft.common.util.JUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.level.GameType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ServerPlayerGameMode.class)
public class ServerPlayerGameModeMixin {

    @Shadow @Final protected ServerPlayer player;

    /**
     * Desummon stands when going in spectator mode.
     */
    @Inject(method = "Lnet/minecraft/server/level/ServerPlayerGameMode;changeGameModeForPlayer(Lnet/minecraft/world/level/GameType;)Z", at = @At(value = "TAIL"))
    public void jcraft$changeGameModeForPlayer(GameType gameModeForPlayer, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue() && gameModeForPlayer == GameType.SPECTATOR) {
            var stand = JUtils.getStand(player);
            if (stand != null) {
                stand.desummon();
            }
        }
    }

}
