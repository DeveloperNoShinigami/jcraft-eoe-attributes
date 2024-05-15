package net.arna.jcraft.fabric.common.terrablender;

import net.arna.jcraft.registry.JBiomeRegistry;
import net.arna.jcraft.registry.JBlockRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.world.gen.surfacebuilder.MaterialRules;

public class MaterialRulesFabric {

    private static final MaterialRules.MaterialRule SANDSTONE = makeStateRule(Blocks.SANDSTONE);
    private static final MaterialRules.MaterialRule HOT_SAND = makeStateRule(JBlockRegistry.HOT_SAND_BLOCK.get());

    public static MaterialRules.MaterialRule makeRules() {
        final var devilsPalmCond = MaterialRules.biome(JBiomeRegistry.DEVILS_PALM);
        final var replaceStoneWithSandstone = MaterialRules.condition(MaterialRules.STONE_DEPTH_FLOOR_WITH_SURFACE_DEPTH_RANGE_6, SANDSTONE);
        final var replaceStoneWithHotSand = MaterialRules.condition(MaterialRules.STONE_DEPTH_FLOOR, HOT_SAND);
        return MaterialRules.sequence(
                // order of the hot sand / sandstone is very important and correct as it is right now
                MaterialRules.condition(devilsPalmCond, MaterialRules.sequence(replaceStoneWithHotSand, replaceStoneWithSandstone))
        );
    }

    private static MaterialRules.MaterialRule makeStateRule(Block block) {
        return MaterialRules.block(block.getDefaultState());
    }
}
