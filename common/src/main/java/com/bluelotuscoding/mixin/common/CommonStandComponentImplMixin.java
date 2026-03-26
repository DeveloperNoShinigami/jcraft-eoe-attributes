package com.bluelotuscoding.mixin.common;

import com.bluelotuscoding.api.attribute.StandAttributeManager;
import com.bluelotuscoding.api.registry.JAttributeRegistry;
import net.arna.jcraft.api.stand.StandType;
import net.arna.jcraft.common.component.impl.living.CommonStandComponentImpl;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * Mixin into CommonStandComponentImpl to provide per-stand attribute persistence.
 * When a player swaps stands, their current attribute values are saved under the
 * old stand's ID and restored from the new stand's saved data.
 */
@Mixin(value = CommonStandComponentImpl.class, remap = false)
public abstract class CommonStandComponentImplMixin {

    @Shadow private Entity entity;
    @Shadow private StandType type;

    /**
     * Per-stand attribute storage: maps Stand ResourceLocation string -> attribute snapshot.
     * Example: { "jcraft:the_world": {StandDamage: 50.0, ...}, "jcraft:cream": {StandDamage: 0.0, ...} }
     */
    @Unique
    private final Map<String, CompoundTag> jcraftAttributes$perStandData = new HashMap<>();

    /**
     * Inject BEFORE setTypeAndSkin to capture the old stand's attributes
     * and load the new stand's attributes.
     */
    @Inject(method = "setTypeAndSkin", at = @At("HEAD"))
    private void jcraftAttributes$onStandSwap(@Nullable StandType newType, int skin, CallbackInfo ci) {
        if (entity.level().isClientSide) return;
        if (!(entity instanceof LivingEntity living)) return;

        // Get the old stand's ID (before the swap)
        ResourceLocation oldId = (this.type != null) ? this.type.getId() : null;
        // Get the new stand's ID
        ResourceLocation newId = (newType != null) ? newType.getId() : null;

        // Don't do anything if the stand isn't actually changing
        if (oldId != null && oldId.equals(newId)) return;

        StandAttributeManager.onStandChange(living, oldId, newId, jcraftAttributes$perStandData);
    }

    /**
     * Save per-stand attribute data into the component's NBT.
     */
    @Inject(method = "writeToNbt", at = @At("TAIL"))
    private void jcraftAttributes$writePerStandData(CompoundTag tag, CallbackInfo ci) {
        if (jcraftAttributes$perStandData.isEmpty()) return;

        // Also save the CURRENT stand's live attributes before writing
        if (this.type != null && entity instanceof LivingEntity living) {
            CompoundTag currentSnapshot = StandAttributeManager.captureSnapshot(living);
            jcraftAttributes$perStandData.put(this.type.getId().toString(), currentSnapshot);
        }

        CompoundTag standDataTag = new CompoundTag();
        for (Map.Entry<String, CompoundTag> entry : jcraftAttributes$perStandData.entrySet()) {
            standDataTag.put(entry.getKey(), entry.getValue());
        }
        tag.put("jcraft_attributes_StandData", standDataTag);
    }

    /**
     * Load per-stand attribute data from the component's NBT.
     */
    @Inject(method = "readFromNbt", at = @At("TAIL"))
    private void jcraftAttributes$readPerStandData(CompoundTag tag, CallbackInfo ci) {
        jcraftAttributes$perStandData.clear();

        if (tag.contains("jcraft_attributes_StandData")) {
            CompoundTag standDataTag = tag.getCompound("jcraft_attributes_StandData");
            for (String key : standDataTag.getAllKeys()) {
                jcraftAttributes$perStandData.put(key, standDataTag.getCompound(key));
            }
        }

        // After loading, restore the attributes for the current stand
        if (this.type != null && entity instanceof LivingEntity living) {
            String currentStandKey = this.type.getId().toString();
            if (jcraftAttributes$perStandData.containsKey(currentStandKey)) {
                StandAttributeManager.restoreSnapshot(living, jcraftAttributes$perStandData.get(currentStandKey));
            }
        }
    }
}
