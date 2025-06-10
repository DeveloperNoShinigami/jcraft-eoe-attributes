package net.arna.jcraft.client.registry;

import net.arna.jcraft.JCraft;
import net.arna.jcraft.registry.JItemRegistry;
import net.minecraft.client.renderer.item.ItemProperties;

import static net.arna.jcraft.common.item.BloodBottleItem.MAX_BLOOD;

public interface JItemPropertiesRegistry {

    static void registerItemProperties() {
        ItemProperties.register(
                JItemRegistry.BLOOD_BOTTLE.get(),
                JCraft.id("blood"),
                (stack, world, entity, seed) -> stack.getOrCreateTag().getFloat("Blood") / MAX_BLOOD
        );
    }
}
