package com.bluelotuscoding.mixin.common;

import com.bluelotuscoding.api.attribute.JAttributesComponent;
import com.bluelotuscoding.api.attribute.StandAttributeManager;
import net.arna.jcraft.api.component.living.CommonStandComponent;
import net.arna.jcraft.api.stand.StandType;
import net.arna.jcraft.common.component.impl.living.CommonStandComponentImpl;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Class: CommonStandComponentImpl
 * Stand: Any
 * Purpose: Integrates the JAttributes API into the JCraft component system.
 */
import java.util.HashMap;
import java.util.Map;

// Targets JCraft internal class (not stable API) — verified against JCraft 0.17.6
// Class: net.arna.jcraft.common.component.impl.living.CommonStandComponentImpl
// Purpose: Attaches per-stand attribute storage (NBT map) to the stand component.
//          Saves the current stand's attribute modifier values before a type switch,
//          and loads the new stand's values after. Persists via readFromNbt/writeToNbt.
@Mixin(value = CommonStandComponentImpl.class, remap = false)
public abstract class CommonStandComponentImplMixin implements JAttributesComponent {

    @Shadow @Final private net.minecraft.world.entity.Entity entity;
    @Shadow private StandType type;

    @Unique
    private final Map<String, CompoundTag> jcraftAttributes_standAttributeMap = new HashMap<>();

    @Inject(method = "setTypeAndSkin(Lnet/arna/jcraft/api/stand/StandType;I)V", at = @At("HEAD"))
    private void jcraftAttributes_beforeSetType(StandType newType, int skin, CallbackInfo ci) {
        if (entity instanceof LivingEntity living) {
            StandAttributeManager.saveAttributes(living, (CommonStandComponent) (Object) this);
        }
    }

    @Inject(method = "setTypeAndSkin(Lnet/arna/jcraft/api/stand/StandType;I)V", at = @At("TAIL"))
    private void jcraftAttributes_afterSetType(StandType newType, int skin, CallbackInfo ci) {
        if (entity instanceof LivingEntity living) {
            StandAttributeManager.loadAttributes(living, (CommonStandComponent) (Object) this);
        }
    }

    @Inject(method = "readFromNbt(Lnet/minecraft/class_2487;)V", at = @At("TAIL"))
    private void jcraftAttributes_readFromNbt(CompoundTag tag, CallbackInfo ci) {
        jcraftAttributes_standAttributeMap.clear();
        if (tag.contains("JCraftAttributesList", Tag.TAG_LIST)) {
            ListTag list = tag.getList("JCraftAttributesList", Tag.TAG_COMPOUND);
            for (int i = 0; i < list.size(); i++) {
                CompoundTag entry = list.getCompound(i);
                String standId = entry.getString("StandId");
                CompoundTag data = entry.getCompound("Data");
                jcraftAttributes_standAttributeMap.put(standId, data);
            }
        }
    }

    @Inject(method = "writeToNbt(Lnet/minecraft/class_2487;)V", at = @At("TAIL"))
    private void jcraftAttributes_writeToNbt(CompoundTag tag, CallbackInfo ci) {
        ListTag list = new ListTag();
        for (Map.Entry<String, CompoundTag> entry : jcraftAttributes_standAttributeMap.entrySet()) {
            CompoundTag compoundEntry = new CompoundTag();
            compoundEntry.putString("StandId", entry.getKey());
            compoundEntry.put("Data", entry.getValue());
            list.add(compoundEntry);
        }
        tag.put("JCraftAttributesList", list);
    }

    @Override
    @Unique
    public Map<String, CompoundTag> getStandAttributeMap() {
        return jcraftAttributes_standAttributeMap;
    }
    
    @Override
    @Unique
    public void reset() {
        jcraftAttributes_standAttributeMap.clear();
    }
}
