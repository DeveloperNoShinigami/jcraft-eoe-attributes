package net.arna.jcraft.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class SoulBlock extends Block {
    public SoulBlock(Properties settings) {
        super(settings);
    }

    @Override
    public void stepOn(Level world, BlockPos pos, BlockState state, Entity entity) {
        if (!entity.isCurrentlyGlowing() && entity instanceof LivingEntity livingEntity) {
            livingEntity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 80, 0));
        }
        super.stepOn(world, pos, state, entity);
    }
}