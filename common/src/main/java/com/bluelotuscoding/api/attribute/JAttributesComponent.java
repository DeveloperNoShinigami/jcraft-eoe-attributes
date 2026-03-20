package com.bluelotuscoding.api.attribute;

import net.minecraft.nbt.CompoundTag;
import java.util.Map;

public interface JAttributesComponent {
    Map<String, CompoundTag> getStandAttributeMap();
    void reset();
}
