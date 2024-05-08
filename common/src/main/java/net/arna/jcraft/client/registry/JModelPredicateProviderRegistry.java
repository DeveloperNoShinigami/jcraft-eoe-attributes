package net.arna.jcraft.client.registry;

import net.arna.jcraft.JCraft;
import net.arna.jcraft.registry.JItemRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.ModelPredicateProviderRegistry;

import static net.arna.jcraft.common.item.BloodBottleItem.MAX_BLOOD;

@Environment(EnvType.CLIENT)
public interface JModelPredicateProviderRegistry {
    static void register() {
        ModelPredicateProviderRegistry.register(
                JItemRegistry.BLOOD_BOTTLE.get(),
                JCraft.id("blood"),
                (stack, world, entity, seed) -> stack.getOrCreateNbt().getFloat("Blood") / MAX_BLOOD
        );
    }
}
