package net.arna.jcraft.common.component.impl;

import lombok.Getter;
import net.arna.jcraft.common.component.living.CommonVampireComponent;
import net.arna.jcraft.common.spec.JSpec;
import net.arna.jcraft.common.spec.SpecType;
import net.arna.jcraft.common.spec.VampireSpec;
import net.arna.jcraft.common.util.JUtils;
import net.arna.jcraft.registry.JTagRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.level.Level;

public abstract class CommonVampireComponentImpl implements CommonVampireComponent {
    private final LivingEntity entity;
    private final Player player;
    private FoodData hungerManager = null;
    private boolean isVampire = false;
    @Getter
    private float blood = 20;
    private byte healCount = 0;
    private int regenTick = 0, starveTick = 0;

    public static final int MIN_REGEN_BLOOD = 16; // 75%

    public CommonVampireComponentImpl(final LivingEntity entity) {
        this.entity = entity;
        if (entity instanceof Player playerEntity) {
            this.player = playerEntity;
            hungerManager = playerEntity.getFoodData();
        } else this.player = null;
    }

    public void tick() {
        final Level world = entity.level();
        if (world.isClientSide) {
            return;
        }

        if (player != null) {
            JSpec<?, ?> spec = JUtils.getSpec(player);
            // FOR NOW, these are intrinsically tied.
            setVampire(spec != null && spec.getType().equals(SpecType.VAMPIRE));
        }

        if (!isVampire) {
            return;
        }

        // Taking damage when unprotected from sunlight.
        if (world.isDay() && !world.isThundering() && !(entity.getItemBySlot(EquipmentSlot.HEAD).is(JTagRegistry.PROTECTS_FROM_SUN)) && world.canSeeSky(entity.blockPosition())) {
            entity.setSecondsOnFire(1);
            entity.hurt(world.damageSources().dryOut(), 2.0F);
        }

        // Vampires do not have to breathe.
        entity.setAirSupply(entity.getMaxAirSupply());

        if (blood < 1 && --starveTick < 1) {
            // Starve
            player.hurt(world.damageSources().starve(), 1.0F);
            starveTick = 80;
        } else {
            // Regenerate
            float health = entity.getHealth();
            if (health < entity.getMaxHealth() && blood >= MIN_REGEN_BLOOD && --regenTick < 1) {
                player.heal(1);

                // Every third heal takes away a blood unit
                if (++healCount > 2) {
                    blood--;
                    healCount = 0;
                }

                regenTick = 10;
            }
        }

        // Prevent the player's default hunger from doing anything
        if (hungerManager == null) {
            if (player != null) {
                hungerManager = player.getFoodData();
            }
        } else {
            // Vampires get tired slower
            if (hungerManager.getExhaustionLevel() > 32.0F) {
                hungerManager.addExhaustion(-32.0F);
                setBlood(blood - 1);
            }

            hungerManager.setFoodLevel(20);
            hungerManager.setSaturation(0f);
        }
    }

    @Override
    public void setBlood(final float blood) {
        this.blood = Mth.clamp(blood, 0, 20);
        sync(entity);
    }

    @Override
    public boolean isVampire() {
        return isVampire || player != null && JUtils.getSpec(player) instanceof VampireSpec;
    }

    @Override
    public void setVampire(boolean b) {
        if (b == this.isVampire) return;
        this.isVampire = b;
        sync(entity);
    }

    public void sync(final Entity entity) {
    }

    public boolean shouldSyncWith(final ServerPlayer player) {
        return player == entity;
    }

    public void writeSyncPacket(final FriendlyByteBuf buf, final ServerPlayer recipient) {
        buf.writeFloat(blood);
        buf.writeBoolean(isVampire);
    }

    public void applySyncPacket(final FriendlyByteBuf buf) {
        blood = buf.readFloat();
        isVampire = buf.readBoolean();
    }

    public void readFromNbt(final CompoundTag tag) {
        blood = tag.getFloat("Blood");
        isVampire = tag.getBoolean("Vampire");
    }

    public void writeToNbt(final CompoundTag tag) {
        tag.putFloat("Blood", blood);
        tag.putBoolean("Vampire", isVampire);
    }
}
