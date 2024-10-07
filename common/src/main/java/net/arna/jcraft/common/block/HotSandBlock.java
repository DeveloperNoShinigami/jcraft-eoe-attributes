package net.arna.jcraft.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;

public class HotSandBlock extends FallingBlock {
    public HotSandBlock(final Properties settings) {
        super(settings);
    }

    @Override
    public void stepOn(final Level world, final BlockPos pos, final BlockState state, final Entity entity) {
        if (!entity.isSteppingCarefully() && !entity.isInWater() &&
                entity instanceof final LivingEntity livingEntity && !EnchantmentHelper.hasFrostWalker(livingEntity)) {
            entity.hurt(world.damageSources().hotFloor(), 1.0F);
        }
        super.stepOn(world, pos, state, entity);
    }
}