package net.arna.jcraft.common.item;

import net.arna.jcraft.common.network.s2c.ShaderActivationPacket;
import net.arna.jcraft.platform.JComponentPlatformUtils;
import net.arna.jcraft.api.registry.JSoundRegistry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class DebugWand extends Item {
    public DebugWand(Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        if (world.isClientSide) {
            return InteractionResultHolder.pass(user.getItemInHand(hand));
        }

        if (user.isShiftKeyDown()) {
            world.playSound(null, user.blockPosition(), JSoundRegistry.TW_TS_CLEAN.get(), SoundSource.PLAYERS, 1.2f, 1);
            ShaderActivationPacket.send((ServerPlayer) user, user, 0, 20 * 6, ShaderActivationPacket.Type.ZA_WARUDO);
        }
        return super.use(world, user, hand);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        Level level = context.getLevel();

        /*
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        NetworkManager.sendToServer(JPacketRegistry.C2S_MENU_CALL, buf);
         */


        if (player == null || level.isClientSide) {
            return InteractionResult.PASS;
        }

        // Nearest armor stand grabs you
        ArmorStand armorStand = level.getNearestEntity(ArmorStand.class, TargetingConditions.DEFAULT, player,
                player.getX(),
                player.getY(),
                player.getZ(),
                player.getBoundingBox().inflate(32.0));
        JComponentPlatformUtils.getGrab(player).startGrab(armorStand, 80, 0.4);


        // Feel free to remove or modify these to debug other components/features.
        if (player.isShiftKeyDown()) {
            //TimestopShaderPostProcessor.playEffect(player.getPos().toVector3f());
            //ShaderActivationPacket.send((ServerPlayer) player, player, 0, 20 * 6, ShaderActivationPacket.Type.ZA_WARUDO);
        } else {
            JComponentPlatformUtils.getShockwaveHandler(context.getLevel()).addShockwave(context.getClickLocation(), player.getLookAngle());
        }

        return super.useOn(context);
    }
}
