package net.arna.jcraft.fabric.common.terrablender;

import net.arna.jcraft.api.registry.JBiomeRegistry;
import net.arna.jcraft.api.registry.JBlockRegistry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.SurfaceRules;

public class MaterialRulesFabric {

    private static final SurfaceRules.RuleSource SANDSTONE = makeStateRule(Blocks.SANDSTONE);
    private static final SurfaceRules.RuleSource HOT_SAND = makeStateRule(JBlockRegistry.HOT_SAND_BLOCK.get());

    public static SurfaceRules.RuleSource makeRules() {
        final var devilsPalmCond = SurfaceRules.isBiome(JBiomeRegistry.DEVILS_PALM);
        final var replaceStoneWithSandstone = SurfaceRules.ifTrue(SurfaceRules.DEEP_UNDER_FLOOR, SANDSTONE);
        final var replaceStoneWithHotSand = SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, HOT_SAND);
        return SurfaceRules.sequence(
                // order of the hot sand / sandstone is very important and correct as it is right now
                SurfaceRules.ifTrue(devilsPalmCond, SurfaceRules.sequence(replaceStoneWithHotSand, replaceStoneWithSandstone))
        );
    }

    private static SurfaceRules.RuleSource makeStateRule(Block block) {
        return SurfaceRules.state(block.defaultBlockState());
    }
}
