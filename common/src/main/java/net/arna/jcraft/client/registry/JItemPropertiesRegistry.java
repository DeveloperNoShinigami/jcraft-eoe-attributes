package net.arna.jcraft.client.registry;

import dev.architectury.registry.item.ItemPropertiesRegistry;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.stand.StandType;
import net.arna.jcraft.common.item.StandDiscItem;
import net.arna.jcraft.registry.JItemRegistry;

import java.util.Optional;

public interface JItemPropertiesRegistry {

    static void registerItemProperties() {
        ItemPropertiesRegistry.register(JItemRegistry.STAND_DISC.get(), JCraft.id("stand_id"),
                (stack, level, entity, i) ->
                        Optional.ofNullable(StandDiscItem.getStandType(stack))
                                .map(StandType::ordinal)
                                .map(i0 -> (float) i0 / StandType.values().length)
                                .orElse(0f));
        ItemPropertiesRegistry.register(JItemRegistry.STAND_DISC.get(), JCraft.id("stand_skin"),
                (stack, level, entity, i) -> StandDiscItem.getSkin(stack) / 3f);
    }
}
