package net.arna.jcraft.common.block.tile;

import mod.azure.azurelib.animatable.GeoBlockEntity;
import net.arna.jcraft.common.block.CoffinBlock;
import net.arna.jcraft.registry.JBlockEntityTypeRegistry;
import net.arna.jcraft.registry.JBlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import mod.azure.azurelib.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelib.core.animation.AnimatableManager;
import mod.azure.azurelib.core.animation.AnimationController;
import mod.azure.azurelib.core.animation.AnimationState;
import mod.azure.azurelib.core.animation.RawAnimation;
import mod.azure.azurelib.core.object.PlayState;
import mod.azure.azurelib.util.AzureLibUtil;

public class CoffinTileEntity extends BlockEntity implements GeoBlockEntity {
    private final AnimatableInstanceCache cache = AzureLibUtil.createInstanceCache(this);

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
