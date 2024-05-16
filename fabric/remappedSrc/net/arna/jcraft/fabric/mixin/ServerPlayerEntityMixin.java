package net.arna.jcraft.fabric.mixin;

import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.stand.StandEntity;
import net.arna.jcraft.common.util.IJInputStateManagerHolder;
import net.arna.jcraft.common.util.InputStateManager;
import net.arna.jcraft.common.util.JUtils;
import net.arna.jcraft.mixin_logic.ServerPlayerEntityMixinLogic;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayer.class)
public class ServerPlayerEntityMixin implements IJInputStateManagerHolder {

    private @Unique boolean hadStand = false;
    private final @Unique InputStateManager inputStateManager = new InputStateManager();

    @Inject(method = "moveToWorld", at = @At("HEAD"))
    private void saveStandStateBeforeWorldMove(ServerLevel destination, CallbackInfoReturnable<Entity> cir) {
        hadStand = JUtils.getStand((ServerPlayer) (Object) this) != null;
    }

    // Inject at the end of the if-block
    @Inject(method = "moveToWorld", at = @At(value = "FIELD", target = "Lnet/minecraft/server/network/ServerPlayerEntity;syncedFoodLevel:I", shift = At.Shift.AFTER))
    private void resummonStandAfterWorldMove(ServerLevel destination, CallbackInfoReturnable<Entity> cir) {
        ServerPlayerEntityMixinLogic.resummonStandAfterWorldMove((ServerPlayer) (Object) this, hadStand, destination, cir);
    }

    @Inject(method = "moveToWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;" +
            "removePlayer(Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/entity/Entity$RemovalReason;)V"))
    private void doNotPlayDesummonSoundWhenMovingWorld(ServerLevel destination, CallbackInfoReturnable<Entity> cir) {
        ServerPlayerEntityMixinLogic.doNotPlayDesummonSoundWhenMovingWorld((ServerPlayer) (Object) this, destination, cir);
    }

    @Override
    public InputStateManager jcraft$getJInputStateManager() {
        return inputStateManager;
    }

    @Inject(at = @At("TAIL"), method = "copyFrom")
    private void copyInputStateManagerUponCopy(ServerPlayer oldPlayer, boolean alive, CallbackInfo ci) {
        ServerPlayerEntityMixinLogic.copyInputStateManagerUponCopy(inputStateManager, oldPlayer, alive, ci);
    }

    @Inject(method = "dropItem", at = @At(value = "HEAD"), cancellable = true)
    private void jcraft$dropItem(ItemStack stack, boolean throwRandomly, boolean retainOwnership, CallbackInfoReturnable<ItemEntity> cir) {
        ServerPlayerEntityMixinLogic.jcraft$dropItem((ServerPlayer) (Object) this, stack, throwRandomly, retainOwnership, cir);
    }
}
