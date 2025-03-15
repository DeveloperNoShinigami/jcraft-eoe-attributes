package net.arna.jcraft.forge.mixin;

import net.arna.jcraft.registry.JBlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Why do we have to do this? Well, in fabric there is a registry to add {@link BlockPathTypes} to blocks.
 * In Forge, you do that by giving the BlockPathTypes to the block at creation. We can't use that
 * however, because we create our {@link net.minecraft.world.level.block.Block Blocks} before we get the
 * Forge {@link net.minecraftforge.common.property.Properties Properties}, in the registry of common.
 */
@Mixin(value = WalkNodeEvaluator.class)
public class WalkNodeEvaluatorMixin {

    @Inject(method = "getBlockPathTypeRaw", at = @At("RETURN"), cancellable = true)
    private static void hackHotSandAvoidanceIn(BlockGetter level, BlockPos pos, CallbackInfoReturnable<BlockPathTypes> cir) {
        if (level.getBlockState(pos).is(JBlockRegistry.HOT_SAND_BLOCK.get())) {
            cir.setReturnValue(BlockPathTypes.DAMAGE_OTHER);
        }
    }

}
