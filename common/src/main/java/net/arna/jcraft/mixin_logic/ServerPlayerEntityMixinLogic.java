package net.arna.jcraft.mixin_logic;

import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.stand.StandEntity;
import net.arna.jcraft.common.gravity.api.GravityChangerAPI;
import net.arna.jcraft.common.gravity.api.RotationParameters;
import net.arna.jcraft.common.util.IJInputStateManagerHolder;
import net.arna.jcraft.common.util.InputStateManager;
import net.arna.jcraft.common.util.JUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class ServerPlayerEntityMixinLogic {


    public static void inject_moveToWorld_sendPacket_1(ServerPlayerEntity serverPlayer) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(serverPlayer);
        if (gravityDirection != GravityChangerAPI.getDefaultGravityDirection(serverPlayer) && JCraft.gravityConfig.resetGravityOnDimensionChange) {
            GravityChangerAPI.setDefaultGravityDirection(serverPlayer, Direction.DOWN, new RotationParameters().rotationTime(0));
        } else {
            GravityChangerAPI.setDefaultGravityDirection(serverPlayer, GravityChangerAPI.getDefaultGravityDirection(serverPlayer), new RotationParameters().rotationTime(0));
        }
    }

    public static void resummonStandAfterWorldMove(ServerPlayerEntity serverPlayer, boolean hadStand, ServerWorld destination, CallbackInfoReturnable<Entity> cir) {
        if (!hadStand) {
            return;
        }
        StandEntity<?, ?> stand = JCraft.summon(destination, serverPlayer);
        if (stand != null) {
            stand.setPlaySummonSound(false);
        }
    }

    public static void doNotPlayDesummonSoundWhenMovingWorld(ServerPlayerEntity serverPlayer, ServerWorld destination, CallbackInfoReturnable<Entity> cir) {
        StandEntity<?, ?> stand = JUtils.getStand(serverPlayer);
        if (stand == null) {
            return;
        }

        stand.setPlayDesummonSound(false);
    }



    public static void copyInputStateManagerUponCopy(InputStateManager thisManager, ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci) {
        if (!alive) {
            return;
        }
        InputStateManager old = ((IJInputStateManagerHolder) oldPlayer).jcraft$getJInputStateManager();
        thisManager.copyFrom(old);
    }

    public static void jcraft$dropItem(ServerPlayerEntity serverPlayer, ItemStack stack, boolean throwRandomly, boolean retainOwnership, CallbackInfoReturnable<ItemEntity> cir) {
        if (!JUtils.canAct(serverPlayer)) {
            cir.cancel();
        }
    }
}
