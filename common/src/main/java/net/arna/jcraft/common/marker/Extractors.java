package net.arna.jcraft.common.marker;

import net.arna.jcraft.common.util.TriConsumer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public interface Extractors {

    TriConsumer<ResourceLocation, Entity, CompoundTag> ENTITY = (id, entity, compoundTag) -> {
        if (id == null) {
            return;
        }
        // TODO continue
        return;
    };

    // TODO LIVING_ENTITY etc

}
