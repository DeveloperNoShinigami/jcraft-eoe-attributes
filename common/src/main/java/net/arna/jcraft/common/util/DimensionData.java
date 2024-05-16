package net.arna.jcraft.common.util;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class DimensionData {
    public final LivingEntity user;
    public @Nullable Vec3 pos = null;
    public final ResourceKey<Level> worldKey;
    public int timer = 300;

    public DimensionData(LivingEntity user, ResourceKey<Level> worldKey, int timer) {
        this.user = user;
        this.worldKey = worldKey;
        this.timer = timer;
    }

    public DimensionData(LivingEntity user, @Nullable Vec3 pos, ResourceKey<Level> worldKey) {
        this.user = user;
        this.pos = pos;
        this.worldKey = worldKey;
    }

    public DimensionData(LivingEntity user, @Nullable Vec3 pos, ResourceKey<Level> worldKey, int timer) {
        this.user = user;
        this.pos = pos;
        this.worldKey = worldKey;
        this.timer = timer;
    }
}
