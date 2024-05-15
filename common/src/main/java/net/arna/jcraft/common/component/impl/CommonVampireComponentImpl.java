package net.arna.jcraft.common.component.impl;

import lombok.Getter;
import net.arna.jcraft.common.component.living.CommonVampireComponent;
import net.arna.jcraft.common.item.SunProtectionItem;
import net.arna.jcraft.common.spec.JSpec;
import net.arna.jcraft.common.spec.SpecType;
import net.arna.jcraft.common.util.JUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public abstract class CommonVampireComponentImpl implements CommonVampireComponent {
    private final LivingEntity entity;
    private PlayerEntity player;
    private HungerManager hungerManager = null;
    private boolean isVampire = false;
    @Getter
    private float blood = 20;
    private byte healCount = 0;
    private int regenTick = 0, starveTick = 0;

    public static final int MIN_REGEN_BLOOD = 16; // 75%

    public CommonVampireComponentImpl(LivingEntity entity) {
        this.entity = entity;
        if (entity instanceof PlayerEntity playerEntity) {
            this.player = playerEntity;
            hungerManager = playerEntity.getHungerManager();
        }
    }

    public void tick() {
        World world = entity.getWorld();
        if (world.isClient) {
            return;
        }

        if (player != null) {
            JSpec<?, ?> spec = JUtils.getSpec(player);
            if (spec == null || !spec.getType().equals(SpecType.VAMPIRE)) {
                setVampire(false);
            }
        }

        if (!isVampire) {
            return;
        }

        if (world.isDay() && !(entity.getEquippedStack(EquipmentSlot.HEAD).getItem() instanceof SunProtectionItem) && world.isSkyVisible(entity.getBlockPos())) {
            entity.setOnFireFor(1);
        }

        if (blood < 1 && --starveTick < 1) {
            // Starve
            player.damage(world.getDamageSources().starve(), 1.0F);
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
                hungerManager = player.getHungerManager();
            }
        } else {
            // Vampires get tired slower
            if (hungerManager.getExhaustion() > 32.0F) {
                hungerManager.addExhaustion(-32.0F);
                setBlood(blood - 1);
            }

            hungerManager.setFoodLevel(20);
            hungerManager.setSaturationLevel(0f);
        }
    }

    @Override
    public void setBlood(float blood) {
        this.blood = MathHelper.clamp(blood, 0, 20);
        sync(entity);
    }

    @Override
    public boolean isVampire() {
        return isVampire;
    }

    @Override
    public void setVampire(boolean b) {
        this.isVampire = b;
        sync(entity);
    }

    public void sync(Entity entity) {
    }

    public boolean shouldSyncWith(ServerPlayerEntity player) {
        return player == entity;
    }

    public void writeSyncPacket(PacketByteBuf buf, ServerPlayerEntity recipient) {
        buf.writeFloat(blood);
        buf.writeBoolean(isVampire);
    }

    public void applySyncPacket(PacketByteBuf buf) {
        blood = buf.readFloat();
        isVampire = buf.readBoolean();
    }

    public void readFromNbt(NbtCompound tag) {
        blood = tag.getFloat("Blood");
        isVampire = tag.getBoolean("Vampire");
    }

    public void writeToNbt(NbtCompound tag) {
        tag.putFloat("Blood", blood);
        tag.putBoolean("Vampire", isVampire);
    }
}
