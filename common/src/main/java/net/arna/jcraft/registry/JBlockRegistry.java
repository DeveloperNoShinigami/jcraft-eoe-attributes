package net.arna.jcraft.registry;

import dev.architectury.registry.registries.RegistrySupplier;
import net.arna.jcraft.common.block.CoffinBlock;
import net.arna.jcraft.common.block.FoolishSandBlock;
import net.arna.jcraft.common.block.HotSandBlock;
import net.arna.jcraft.common.block.SoulBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.sound.BlockSoundGroup;

import static net.arna.jcraft.JCraft.BLOCK_REGISTRY;

public interface JBlockRegistry {

    //Block
    RegistrySupplier<Block> FOOLISH_SAND_BLOCK = BLOCK_REGISTRY.register("foolish_sand_block", () -> new FoolishSandBlock(AbstractBlock.Settings.create()
            .strength(0.5f)
            .sounds(BlockSoundGroup.SAND)
    ));
    RegistrySupplier<Block> SOUL_BLOCK = BLOCK_REGISTRY.register("soul_block", () -> new SoulBlock(AbstractBlock.Settings.create()
            .strength(4.0f)
            .sounds(BlockSoundGroup.SOUL_SOIL)
    ));
    RegistrySupplier<Block> METEORITE_BLOCK = BLOCK_REGISTRY.register("meteorite_block", () -> new Block(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK)
            .requiresTool()
            .strength(6.0f, 1200f)
            .sounds(BlockSoundGroup.ANCIENT_DEBRIS)
    ));
    RegistrySupplier<Block> METEORITE_IRON_ORE_BLOCK = BLOCK_REGISTRY.register("meteorite_iron_ore_block", () -> new Block(AbstractBlock.Settings.copy(Blocks.IRON_ORE)
            .requiresTool()
            .strength(9.0f, 1200f)
            .sounds(BlockSoundGroup.ANCIENT_DEBRIS)
    ));
    RegistrySupplier<Block> STELLAR_IRON_BLOCK = BLOCK_REGISTRY.register("stellar_iron_block", () -> new Block(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK)
            .requiresTool()
            .strength(7.5f, 1000f)
            .sounds(BlockSoundGroup.NETHERITE)
    ));
    RegistrySupplier<Block> HOT_SAND_BLOCK = BLOCK_REGISTRY.register("hot_sand_block", () -> new HotSandBlock(AbstractBlock.Settings.create()
            .strength(0.5f)
            .sounds(BlockSoundGroup.SAND)
    ));
    RegistrySupplier<Block> COFFIN_BLOCK = BLOCK_REGISTRY.register("coffin", () -> new CoffinBlock(AbstractBlock.Settings.create().sounds(BlockSoundGroup.WOOD).nonOpaque()));


    static void init() {

    }
}
