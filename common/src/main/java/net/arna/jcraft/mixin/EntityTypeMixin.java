package net.arna.jcraft.mixin;

import net.arna.jcraft.common.entity.stand.StandType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import java.util.function.Function;

@Mixin(EntityType.class)
public class EntityTypeMixin {
    private static @Unique int shouldLoadStands = 0;

    // Prevent stand entities from being loaded from NBT, they will be reconstructed instead.
    // Loading stands from NBT tends to break them.
    @Inject(method = "create", at = @At("HEAD"), cancellable = true)
    private static void doNotLoadStandEntities(CompoundTag nbt, Level world, CallbackInfoReturnable<Optional<Entity>> cir) {
        if (shouldLoadStands > 0) {
            return;
        }

        EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.get(new ResourceLocation(nbt.getString("id")));
        if (StandType.getEntityTypes().contains(entityType)) {
            cir.setReturnValue(Optional.empty());
        }
    }

    @Inject(method = "method_17843", at = @At("HEAD"))
    private static void doLoadStandsWhenLoadingArmorStandPre(CompoundTag nbtCompound, Level world, Function<Entity, Entity> function,
                                                             Entity entity, CallbackInfoReturnable<Entity> cir) {
        if (entity instanceof ArmorStand) {
            shouldLoadStands = Math.max(1, shouldLoadStands + 1);
        }
    }

    @Inject(method = "method_17843", at = @At("RETURN"))
    private static void doLoadStandsWhenLoadingArmorStandPost(CompoundTag nbtCompound, Level world, Function<Entity, Entity> function,
                                                              Entity entity, CallbackInfoReturnable<Entity> cir) {
        if (entity instanceof ArmorStand) {
            shouldLoadStands--;
        }
    }
}
