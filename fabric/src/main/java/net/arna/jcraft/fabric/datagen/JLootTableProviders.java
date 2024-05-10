package net.arna.jcraft.fabric.datagen;

import com.google.common.collect.Maps;
import net.arna.jcraft.registry.JBlockRegistry;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.util.Identifier;

import java.util.Map;
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
        }
    }

    public static class EntityLoot extends SimpleFabricLootTableProvider {
        private final Map<Identifier, LootTable.Builder> loot = Maps.newHashMap();

        public EntityLoot(FabricDataOutput output) {
            super(output, LootContextTypes.ENTITY);
        }

        @Override
        public void accept(BiConsumer<Identifier, LootTable.Builder> consumer) {
            this.generateLoot();
            for (Map.Entry<Identifier, LootTable.Builder> entry : loot.entrySet()) {
                consumer.accept(entry.getKey(), entry.getValue());
            }
        }

        private void generateLoot() {

        }
    }
}