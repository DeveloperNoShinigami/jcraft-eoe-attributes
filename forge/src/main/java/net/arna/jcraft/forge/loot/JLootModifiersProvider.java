package net.arna.jcraft.forge.loot;

import net.arna.jcraft.JCraft;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.data.GlobalLootModifierProvider;

public class JLootModifiersProvider extends GlobalLootModifierProvider {
    public JLootModifiersProvider(final PackOutput output) {
        super(output, JCraft.MOD_ID);
    }

    @Override
    protected void start() {
        add("loot_adapter", new JForgeLootAdapter(new LootItemCondition[]{}));
    }
}
