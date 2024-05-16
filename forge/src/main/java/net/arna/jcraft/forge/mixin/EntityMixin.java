package net.arna.jcraft.forge.mixin;

import net.arna.jcraft.common.entity.stand.KingCrimsonEntity;
import net.arna.jcraft.common.entity.stand.StandEntity;
import net.arna.jcraft.common.events.ServerEntityTickEvent;
import net.arna.jcraft.common.gravity.api.GravityChangerAPI;
import net.arna.jcraft.common.gravity.util.RotationUtil;
import net.arna.jcraft.common.util.JUtils;
import net.arna.jcraft.mixin_logic.EntityMixinLogic;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraftforge.common.util.ITeleporter;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Entity.class, remap = false)
public abstract class EntityMixin {

    /**
     * Stand positioning mixin function
     *
     * @param passenger stand entity
     */
    @Inject(method = "positionRider(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/Entity$MoveFunction;)V", at = @At("HEAD"), cancellable = true)
    private void jcraft$updatePassengerPosition(Entity passenger, Entity.MoveFunction positionUpdater, CallbackInfo info) {
        EntityMixinLogic.jcraft$updatePassengerPosition((Entity)(Object)this, passenger, positionUpdater, info);
    }

    /**
     * Disables sprinting particles during time erase
     */
    @SuppressWarnings("ConstantValue")
    @Inject(method = "canSpawnSprintParticle", at = @At("HEAD"), cancellable = true)
    private void jcraft$shouldSpawnSprintingParticles(CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof LivingEntity living && JUtils.getStand(living) instanceof KingCrimsonEntity kc && kc.getTETime() > 0) {
            cir.setReturnValue(false);
        }
    }
    //todo (polishing): stand position autosolver

    @SuppressWarnings("ConstantValue")
    @Inject(method = "lambda$changeDimension$16", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;restoreFrom(Lnet/minecraft/world/entity/Entity;)V"))
    private void doNotPlayDesummonSoundWhenMovingWorld(ServerLevel arg, PortalInfo portalinfo, Boolean spawnPortal, CallbackInfoReturnable<Entity> cir) {
        EntityMixinLogic.doNotPlayDesummonSoundWhenMovingWorld((Entity) (Object) this);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void preTick(CallbackInfo ci) {
        ServerEntityTickEvent.ENTITY_POST.invoker().tick((Entity) (Object) this);
    }
}
