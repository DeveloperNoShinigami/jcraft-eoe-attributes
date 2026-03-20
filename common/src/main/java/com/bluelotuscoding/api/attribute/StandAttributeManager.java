package com.bluelotuscoding.api.attribute;

import com.bluelotuscoding.api.registry.JAttributeRegistry;
import net.arna.jcraft.api.component.living.CommonStandComponent;
import net.arna.jcraft.api.stand.StandType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;

public class StandAttributeManager {

    public static void saveAttributes(LivingEntity entity, CommonStandComponent component) {
        if (entity.level().isClientSide || !(component instanceof JAttributesComponent jAttrs)) return;
        StandType currentType = component.getType();
        if (currentType == null) return;
        CompoundTag data = new CompoundTag();
        for (JAttributeRegistry.AttributeEntry e : JAttributeRegistry.ALL)
            saveAttribute(entity, e.attribute(), e.nbtKey(), data);
        jAttrs.getStandAttributeMap().put(currentType.getId().toString(), data);
    }

    public static void loadAttributes(LivingEntity entity, CommonStandComponent component) {
        if (entity.level().isClientSide || !(component instanceof JAttributesComponent jAttrs)) return;
        StandType newType = component.getType();
        if (newType == null) return;
        resetToBase(entity);
        CompoundTag data = jAttrs.getStandAttributeMap().get(newType.getId().toString());
        if (data == null) return;
        for (JAttributeRegistry.AttributeEntry e : JAttributeRegistry.ALL)
            loadAttribute(entity, e.attribute(), e.nbtKey(), data);
    }

    private static void resetToBase(LivingEntity entity) {
        for (JAttributeRegistry.AttributeEntry e : JAttributeRegistry.ALL) {
            AttributeInstance inst = entity.getAttribute(e.attribute());
            if (inst == null) continue;
            inst.setBaseValue(e.attribute().getDefaultValue());
            new java.util.HashSet<>(inst.getModifiers()).forEach(m -> inst.removeModifier(m.getId()));
        }
    }

    public static void clearAttributes(LivingEntity entity) {
        for (JAttributeRegistry.AttributeEntry e : JAttributeRegistry.ALL) {
            AttributeInstance inst = entity.getAttribute(e.attribute());
            if (inst == null) continue;
            new java.util.HashSet<>(inst.getModifiers()).forEach(m -> inst.removeModifier(m.getId()));
        }
    }

    private static void saveAttribute(LivingEntity entity, Attribute attribute, String key, CompoundTag tag) {
        AttributeInstance instance = entity.getAttribute(attribute);
        if (instance != null) {
            tag.put(key, instance.save());
        }
    }

    private static void loadAttribute(LivingEntity entity, Attribute attribute, String key, CompoundTag tag) {
        AttributeInstance instance = entity.getAttribute(attribute);
        if (instance != null && tag.contains(key, Tag.TAG_COMPOUND)) {
            instance.load(tag.getCompound(key));
        }
    }

    public static void onStandChange(LivingEntity entity) {
        // Handled by CommonStandComponentImplMixin calling saveAttributes/loadAttributes directly
    }
}
