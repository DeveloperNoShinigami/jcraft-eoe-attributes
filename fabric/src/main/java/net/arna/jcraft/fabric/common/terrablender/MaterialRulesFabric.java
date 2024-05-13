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
        MaterialRules.MaterialCondition isAtOrAboveWaterLevel = MaterialRules.water(-1, 0);

//        MaterialRules.MaterialRule sandSurface = MaterialRules.sequence(MaterialRules.condition(isAtOrAboveWaterLevel, HOT_SAND), SANDSTONE);

        return MaterialRules.sequence(
                MaterialRules.condition(MaterialRules.biome(JBiomeRegistry.DEVILS_PALM),
                        MaterialRules.sequence(MaterialRules.condition(MaterialRules.STONE_DEPTH_FLOOR, HOT_SAND), MaterialRules.condition(MaterialRules.STONE_DEPTH_FLOOR_WITH_SURFACE_DEPTH_RANGE_6, SANDSTONE)
                                )
                ));
    }

    private static MaterialRules.MaterialRule makeStateRule(Block block) {
        return MaterialRules.block(block.getDefaultState());
    }
}
