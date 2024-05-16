package net.arna.jcraft.mixin;

import net.minecraft.world.level.block.NetherPortalBlock;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(NetherPortalBlock.class)
public class NetherPortalBlockMixin {
    //TODO
    //@Redirect(method = "onEntityCollision", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;hasPassengers()Z"))
    //private boolean ignoreStandsWhenEnteringPortal(Entity entity) {
    //    return entity.getPassengerList().stream().anyMatch(e -> !(e instanceof StandEntity<?,?>));
    //}
}
