package net.arna.jcraft.fabric.mixin;

import net.arna.jcraft.common.entity.stand.KingCrimsonEntity;
import net.arna.jcraft.common.events.ServerEntityTickEvent;
import net.arna.jcraft.common.util.JUtils;
import net.arna.jcraft.mixin_logic.EntityMixinLogic;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {

    //todo (polishing): stand position autosolver

    @Inject(method = "changeDimension", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;restoreFrom(Lnet/minecraft/world/entity/Entity;)V"))
    private void doNotPlayDesummonSoundWhenMovingWorld(ServerLevel destination, CallbackInfoReturnable<Entity> cir) {
        EntityMixinLogic.doNotPlayDesummonSoundWhenMovingWorld((Entity) (Object) this);
    }
}
