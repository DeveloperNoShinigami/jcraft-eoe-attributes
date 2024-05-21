package net.arna.jcraft.mixin;

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

@Mixin(value = ServerPlayer.class)
public class ServerPlayerEntityMixin implements IJInputStateManagerHolder {
    private @Unique boolean hadStand = false;
    private final @Unique InputStateManager inputStateManager = new InputStateManager();


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
