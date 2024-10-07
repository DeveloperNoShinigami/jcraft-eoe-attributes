package net.arna.jcraft.common.entity.damage;

import lombok.NonNull;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.stand.StandEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nullable;

public class JDamageSources {

    public static final ResourceKey<DamageType> STAND = ResourceKey.create(Registries.DAMAGE_TYPE, JCraft.id("stand"));
    public static final ResourceKey<DamageType> WHITE_SNAKE_POISON = ResourceKey.create(Registries.DAMAGE_TYPE, JCraft.id("wspoison"));
    public static final ResourceKey<DamageType> BLEEDING = ResourceKey.create(Registries.DAMAGE_TYPE, JCraft.id("jbleeding"));
    public static final ResourceKey<DamageType> PHPOISON = ResourceKey.create(Registries.DAMAGE_TYPE, JCraft.id("phpoison"));

    public static DamageSource create(final Level world, final ResourceKey<DamageType> key, final @Nullable Entity source, final @Nullable Entity attacker) {
        return new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(key), source, attacker);
    }

    public static DamageSource create(final Level world, final ResourceKey<DamageType> key, final @Nullable Entity attacker) {
        return new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(key), attacker);
    }

    public static DamageSource create(final Level world, final ResourceKey<DamageType> key) {
        return new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(key));
    }

    public static @NonNull DamageSource stand(final StandEntity<?, ?> stand) {
        return create(stand.level(), STAND, stand.getUser());
    }

    @Contract(value = "_ -> new", pure = true)
    public static @NonNull DamageSource whitesnakePoison(Entity user) {
        return create(user.level(), WHITE_SNAKE_POISON, user);
    }

    public static @NonNull DamageSource bleeding(Level world) {
        return create(world, BLEEDING);
    }

    public static @NonNull DamageSource phpoison(Level world) {
        return create(world, PHPOISON);
    }
}
