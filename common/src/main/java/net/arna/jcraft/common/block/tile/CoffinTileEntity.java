package net.arna.jcraft.common.block.tile;

import net.arna.jcraft.common.block.CoffinBlock;
import net.arna.jcraft.api.registry.JBlockEntityTypeRegistry;
import net.arna.jcraft.api.registry.JBlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class CoffinTileEntity extends BlockEntity {
    //private final AnimatableInstanceCache cache = AzureLibUtil.createInstanceCache(this);

    public CoffinTileEntity(final BlockPos pos, final BlockState state) {
        super(JBlockEntityTypeRegistry.COFFIN_TILE.get(), pos, state);
    }

    /*
    @Override
    public void registerControllers(final AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 30, this::predicate));
    }

    private PlayState predicate(final AnimationState<?> animationState) {
        if (level == null || !level.getBlockState(getBlockPos()).is(JBlockRegistry.COFFIN_BLOCK.get())) {
            return PlayState.STOP;
        }

        boolean occupied = getBlockState().getValue(CoffinBlock.OCCUPIED);

        return animationState.setAndContinue(RawAnimation.begin().thenLoop(occupied ? "animation.coffin.closed" : "animation.coffin.open"));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }*/
}
