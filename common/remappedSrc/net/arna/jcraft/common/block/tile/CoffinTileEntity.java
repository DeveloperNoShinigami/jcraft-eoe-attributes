package net.arna.jcraft.common.block.tile;

import net.arna.jcraft.common.block.CoffinBlock;
import net.arna.jcraft.registry.JBlockEntityTypeRegistry;
import net.arna.jcraft.registry.JBlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class CoffinTileEntity extends BlockEntity implements GeoBlockEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public CoffinTileEntity(BlockPos pos, BlockState state) {
        super(JBlockEntityTypeRegistry.COFFIN_TILE.get(), pos, state);
    }


    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController(this, "controller", 30, this::predicate));
    }

    private PlayState predicate(AnimationState animationState) {
        if (!level.getBlockState(getBlockPos()).is(JBlockRegistry.COFFIN_BLOCK.get())) {
            return PlayState.STOP;
        }

        boolean occupied = getBlockState().getValue(CoffinBlock.OCCUPIED);

        return animationState.setAndContinue(RawAnimation.begin().thenLoop(occupied ? "animation.coffin.closed" : "animation.coffin.open"));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
