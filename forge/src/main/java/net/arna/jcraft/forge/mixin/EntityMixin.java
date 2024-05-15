package net.arna.jcraft.forge.mixin;

import net.arna.jcraft.common.entity.stand.KingCrimsonEntity;
import net.arna.jcraft.common.entity.stand.StandEntity;
import net.arna.jcraft.common.events.ServerEntityTickEvent;
import net.arna.jcraft.common.gravity.api.GravityChangerAPI;
import net.arna.jcraft.common.gravity.util.RotationUtil;
import net.arna.jcraft.common.util.JUtils;
import net.arna.jcraft.mixin_logic.EntityMixinLogic;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;
import net.minecraftforge.common.util.ITeleporter;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {

    /**
     * Stand positioning mixin function
     *
     * @param passenger stand entity
     */
    @Inject(method = "updatePassengerPosition(Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/Entity$PositionUpdater;)V", at = @At("HEAD"), cancellable = true)
    private void jcraft$updatePassengerPosition(Entity passenger, Entity.PositionUpdater positionUpdater, CallbackInfo info) {
        EntityMixinLogic.jcraft$updatePassengerPosition((Entity)(Object)this, passenger, positionUpdater, info);
    }

    /**
     * Disables sprinting particles during time erase
     */
    @SuppressWarnings("ConstantValue")
    @Inject(method = "shouldSpawnSprintingParticles", at = @At("HEAD"), cancellable = true)
    private void jcraft$shouldSpawnSprintingParticles(CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof LivingEntity living && JUtils.getStand(living) instanceof KingCrimsonEntity kc && kc.getTETime() > 0) {
            cir.setReturnValue(false);
        }
    }
    //todo (polishing): stand position autosolver

    @SuppressWarnings("ConstantValue")
    @Inject(method = "lambda$changeDimension$16", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;copyFrom(Lnet/minecraft/entity/Entity;)V"))
    private void doNotPlayDesummonSoundWhenMovingWorld(ServerWorld arg, TeleportTarget portalinfo, Boolean spawnPortal, CallbackInfoReturnable<Entity> cir) {
        EntityMixinLogic.doNotPlayDesummonSoundWhenMovingWorld((Entity) (Object) this, arg, cir);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void preTick(CallbackInfo ci) {
        ServerEntityTickEvent.ENTITY_POST.invoker().tick((Entity) (Object) this);
    }
}
