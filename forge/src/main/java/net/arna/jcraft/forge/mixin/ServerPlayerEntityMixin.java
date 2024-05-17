package net.arna.jcraft.forge.mixin;

import net.arna.jcraft.common.util.IJInputStateManagerHolder;
import net.arna.jcraft.common.util.InputStateManager;
import net.arna.jcraft.common.util.JUtils;
import net.arna.jcraft.mixin_logic.ServerPlayerEntityMixinLogic;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.ITeleporter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ServerPlayer.class, remap = false)
public class ServerPlayerEntityMixin implements IJInputStateManagerHolder {
    private @Unique boolean hadStand = false;
    private final @Unique InputStateManager inputStateManager = new InputStateManager();

    @Inject(method = "changeDimension(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraftforge/common/util/ITeleporter;)Lnet/minecraft/world/entity/Entity;", at = @At("HEAD"))
    private void saveStandStateBeforeWorldMove(ServerLevel arg, ITeleporter teleporter, CallbackInfoReturnable<Entity> cir) {
        hadStand = JUtils.getStand((ServerPlayer) (Object) this) != null;
    }

    // Inject at the end of the if-block
    @Inject(method = "changeDimension", at = @At(value = "FIELD", target = "Lnet/minecraft/server/level/ServerPlayer;lastSentFood:I", shift = At.Shift.AFTER))
    private void resummonStandAfterWorldMove(ServerLevel destination, ITeleporter teleporter, CallbackInfoReturnable<Entity> cir) {
        ServerPlayerEntityMixinLogic.resummonStandAfterWorldMove((ServerPlayer) (Object) this, hadStand, destination, cir);
    }

    @Inject(method = "changeDimension", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;removePlayerImmediately(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/world/entity/Entity$RemovalReason;)V"))
    private void doNotPlayDesummonSoundWhenMovingWorld(ServerLevel arg, ITeleporter teleporter, CallbackInfoReturnable<Entity> cir) {
        ServerPlayerEntityMixinLogic.doNotPlayDesummonSoundWhenMovingWorld((ServerPlayer) (Object) this, arg, cir);
    }

    @Override
    public InputStateManager jcraft$getJInputStateManager() {
        return inputStateManager;
    }

    @Inject(at = @At("TAIL"), method = "restoreFrom")
    private void copyInputStateManagerUponCopy(ServerPlayer oldPlayer, boolean alive, CallbackInfo ci) {
        ServerPlayerEntityMixinLogic.copyInputStateManagerUponCopy(inputStateManager, oldPlayer, alive, ci);
    }

    @Inject(method = "drop(Lnet/minecraft/world/item/ItemStack;ZZ)Lnet/minecraft/world/entity/item/ItemEntity;", at = @At(value = "HEAD"), cancellable = true)
    private void jcraft$dropItem(ItemStack stack, boolean throwRandomly, boolean retainOwnership, CallbackInfoReturnable<ItemEntity> cir) {
        ServerPlayerEntityMixinLogic.jcraft$dropItem((ServerPlayer) (Object) this, stack, throwRandomly, retainOwnership, cir);
    }
}
