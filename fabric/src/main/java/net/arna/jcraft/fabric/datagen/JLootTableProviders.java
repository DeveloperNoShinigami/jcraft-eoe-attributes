package net.arna.jcraft.fabric.datagen;

import net.arna.jcraft.JCraft;
import net.arna.jcraft.registry.JBlockRegistry;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.util.Identifier;

import java.util.function.BiConsumer;

public class JLootTableProviders {

    public static class BlockLoot extends FabricBlockLootTableProvider {

        protected BlockLoot(FabricDataOutput output) {
            super(output);
        }

        @Override
        public void generate() {
            addDrop(JBlockRegistry.METEORITE_BLOCK.get());
            addDrop(JBlockRegistry.METEORITE_IRON_ORE_BLOCK.get());
            addDrop(JBlockRegistry.SOUL_BLOCK.get());
            addDrop(JBlockRegistry.HOT_SAND_BLOCK.get());
            addDrop(JBlockRegistry.STELLAR_IRON_BLOCK.get());
        }
    }

    public static class EntityLoot extends SimpleFabricLootTableProvider {

        public EntityLoot(FabricDataOutput output) {
            super(output, LootContextTypes.ENTITY);
        }

        @Override
        public void accept(BiConsumer<Identifier, LootTable.Builder> consumer) {
            consumer.accept(JCraft.id("petshop"), LootTable.builder()
                    .pool(constantPool(1f)
                            .with(ItemEntry.builder(Items.FEATHER).apply(uniformAmount(1f, 2f))))
                    .pool(constantPool(1f)
                            .with(ItemEntry.builder(Items.CHAIN).apply(constantAmount(1f))))
            );
        }
    }

    private static LootPool.Builder constantPool(final float rolls) {
        return LootPool.builder().rolls(ConstantLootNumberProvider.create(rolls));
    }

    private static LootPool.Builder uniformPool(final float min, final float max) {
        return LootPool.builder().rolls(UniformLootNumberProvider.create(min, max));
    }

    private static LootFunction.Builder constantAmount(final float amount) {
        return SetCountLootFunction.builder(ConstantLootNumberProvider.create(amount));
    }

    private static LootFunction.Builder uniformAmount(final float min, final float max) {
        return SetCountLootFunction.builder(UniformLootNumberProvider.create(min, max));
    }
}