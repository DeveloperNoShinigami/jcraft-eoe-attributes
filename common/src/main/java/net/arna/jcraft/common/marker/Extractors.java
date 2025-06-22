package net.arna.jcraft.common.marker;

import com.mojang.serialization.DataResult;
import net.arna.jcraft.common.util.NbtUtils;
import net.arna.jcraft.common.util.TriConsumer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import static net.arna.jcraft.common.marker.Identifiers.*;

public interface Extractors {

    TriConsumer<ResourceLocation, Entity, CompoundTag> ENTITY = (id, entity, compoundTag) -> {
        if (id == null) {
            return;
        }
        if (id.equals(POSITION)) {
            final Entity vehicle = entity.getVehicle();
            if (vehicle != null) {
                NbtUtils.put(compoundTag, POSITION.toString(), new Vec3(vehicle.getX(), vehicle.getY(), vehicle.getZ()));
            } else {
                NbtUtils.put(compoundTag, POSITION.toString(), new Vec3(entity.getX(), entity.getY(), entity.getZ()));
            }
        } else if (id.equals(VELOCITY)) {
            NbtUtils.put(compoundTag, VELOCITY.toString(), entity.getDeltaMovement());
        } else if (id.equals(PITCH)) {
            compoundTag.putFloat(PITCH.toString(), entity.getXRot());
        } else if (id.equals(YAW)) {
            compoundTag.putFloat(YAW.toString(), entity.getYRot());
        } else if (id.equals(YAW_HEAD)) {
            compoundTag.putFloat(YAW_HEAD.toString(), entity.getYHeadRot());
        } else if (id.equals(FALL_DISTANCE)) {
            compoundTag.putFloat(FALL_DISTANCE.toString(), entity.fallDistance);
        } else if (id.equals(FIRE)) {
            compoundTag.putInt(FIRE.toString(), entity.getRemainingFireTicks());
        } else if (id.equals(AIR)) {
            compoundTag.putInt(AIR.toString(), entity.getAirSupply());
        } else if (id.equals(GROUNDED)) {
            compoundTag.putBoolean(GROUNDED.toString(), entity.onGround());
        } else if (id.equals(INVULNERABLE)) {
            compoundTag.putBoolean(INVULNERABLE.toString(), entity.isInvulnerable());
        } else if (id.equals(PORTAL_COOLDOWN)) {
            compoundTag.putInt(PORTAL_COOLDOWN.toString(), entity.getPortalCooldown());
        } else if (id.equals(UUID)) {
            compoundTag.putUUID(UUID.toString(), entity.getUUID());
        } else if (id.equals(CUSTOM_NAME)) {
            final Component component = entity.getCustomName();
            if (component != null) {
                compoundTag.putString(CUSTOM_NAME.toString(), Component.Serializer.toJson(component));
            }
        } else if (id.equals(CUSTOM_NAME_VISIBLE)) {
            compoundTag.putBoolean(CUSTOM_NAME_VISIBLE.toString(), entity.isCustomNameVisible());
        } else if (id.equals(SILENT)) {
            compoundTag.putBoolean(SILENT.toString(), entity.isSilent());
        } else if (id.equals(NO_GRAVITY)) {
            compoundTag.putBoolean(NO_GRAVITY.toString(), entity.isNoGravity());
        } else if (id.equals(GLOWING)) {
            compoundTag.putBoolean(GLOWING.toString(), entity.hasGlowingTag());
        } else if (id.equals(TICKS_FROZEN)) {
            compoundTag.putInt(TICKS_FROZEN.toString(), entity.getTicksFrozen());
        } else if (id.equals(VISUAL_FIRE)) {
            compoundTag.putBoolean(VISUAL_FIRE.toString(), entity.hasGlowingTag());
        } else if (id.equals(TAGS)) {
            final ListTag listTag = new ListTag();
            for (final String string : entity.getTags()) {
                listTag.add(StringTag.valueOf(string));
            }
            compoundTag.put(TAGS.toString(), listTag);
        }
        // TODO passengers?
    };

    TriConsumer<ResourceLocation, Entity, CompoundTag> LIVING_ENTITY = (id, entity, compoundTag) -> {
        if (id == null || !(entity instanceof final LivingEntity livingEntity)) {
            return;
        }
        ENTITY.accept(id, entity, compoundTag);
        if (id.equals(HEALTH)) {
            compoundTag.putFloat(HEALTH.toString(), livingEntity.getHealth());
        } else if (id.equals(HURT_TIME)) {
            compoundTag.putInt(HURT_TIME.toString(), livingEntity.hurtTime);
        } else if (id.equals(HURT_BY_TIMESTAMP)) {
            compoundTag.putInt(HURT_BY_TIMESTAMP.toString(), livingEntity.getLastHurtByMobTimestamp());
        } else if (id.equals(DEATH_TIME)) {
            compoundTag.putInt(DEATH_TIME.toString(), livingEntity.deathTime);
        } else if (id.equals(ABSORPTION_AMOUNT)) {
            compoundTag.putFloat(ABSORPTION_AMOUNT.toString(), livingEntity.getAbsorptionAmount());
        } else if (id.equals(ATTRIBUTES)) {
            compoundTag.put(ATTRIBUTES.toString(), livingEntity.getAttributes().save());
        }
        // TODO only save certain effects?
        else if (id.equals(ACTIVE_EFFECTS)) {
            final ListTag listTag = new ListTag();
            for (final MobEffectInstance mobEffectInstance : livingEntity.getActiveEffects()) {
                listTag.add(mobEffectInstance.save(new CompoundTag()));
            }
            compoundTag.put(ACTIVE_EFFECTS.toString(), listTag);
        } else if (id.equals(FALL_FLYING)) {
            compoundTag.putBoolean(FALL_FLYING.toString(), livingEntity.isFallFlying());
        } else if (id.equals(SLEEPING_POSITION) && livingEntity.getSleepingPos().isPresent()) {
            NbtUtils.put(compoundTag, SLEEPING_POSITION.toString(), livingEntity.getSleepingPos().get());
        } else if (id.equals(BRAIN)) {
            final DataResult<Tag> dataResult = livingEntity.getBrain().serializeStart(NbtOps.INSTANCE);
            dataResult.result().ifPresent(tag -> compoundTag.put(BRAIN.toString(), tag));
        }
    };

    TriConsumer<ResourceLocation, Entity, CompoundTag> MOB = (id, entity, compoundTag) -> {
        if (id == null || !(entity instanceof final Mob mob)) {
            return;
        }
        LIVING_ENTITY.accept(id, entity, compoundTag);
        if (id.equals(CAN_PICKUP_LOOT)) {
            compoundTag.putBoolean(CAN_PICKUP_LOOT.toString(), mob.canPickUpLoot());
        } else if (id.equals(PERSISTENCE_REQUIRED)) {
            compoundTag.putBoolean(PERSISTENCE_REQUIRED.toString(), mob.isPersistenceRequired());
        }
        // TODO only specific armor items?
        else if (id.equals(ARMOR_ITEMS)) {
            final ListTag listTag = new ListTag();
            for (final ItemStack itemStack : mob.getArmorSlots()) {
                final CompoundTag armorTag = new CompoundTag();
                if (!itemStack.isEmpty()) {
                    itemStack.save(armorTag);
                }
                listTag.add(armorTag);
            }
            compoundTag.put(ARMOR_ITEMS.toString(), listTag);
        }
        // TODO only specific hand items?
        else if (id.equals(HAND_ITEMS)) {
            final ListTag listTag = new ListTag();
            for (final ItemStack itemStack : mob.getHandSlots()) {
                final CompoundTag handSlot = new CompoundTag();
                if (!itemStack.isEmpty()) {
                    itemStack.save(handSlot);
                }
                listTag.add(handSlot);
            }
            compoundTag.put(HAND_ITEMS.toString(), listTag);
        }
        // armor and hand drop chances cannot be set without mixin
        // TODO leash info
        else if (id.equals(LEFT_HANDED_MOB)) {
            compoundTag.putBoolean(LEFT_HANDED_MOB.toString(), mob.isLeftHanded());
        }
        // TODO loot table info
        else if (id.equals(NO_AI)) {
            compoundTag.putBoolean(NO_AI.toString(), mob.isLeftHanded());
        }
    };

    TriConsumer<ResourceLocation, Entity, CompoundTag> AGEABLE_MOB = (id, entity, compoundTag) -> {
        if (id == null || !(entity instanceof final AgeableMob ageableMob)) {
            return;
        }
        MOB.accept(id, entity, compoundTag);
        if (id.equals(AGE)) {
            compoundTag.putInt(AGE.toString(), ageableMob.getAge());
        }
        // forced age cannot be set without mixin
    };

    TriConsumer<ResourceLocation, Entity, CompoundTag> ANIMAL = (id, entity, compoundTag) -> {
        if (id == null || !(entity instanceof final Animal animal)) {
            return;
        }
        AGEABLE_MOB.accept(id, entity, compoundTag);
        if (id.equals(LOVE)) {
            compoundTag.putBoolean(LOVE.toString(), animal.isInLove());
        }
    };

}