package net.arna.jcraft.common.entity.projectile;

import lombok.NonNull;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.util.IOwnable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Collections;

/**
 * Class for any entities that serve as attacks.
 * Every implementation must append {@link JAttackEntity#readMasterNbt(CompoundTag)} and {@link JAttackEntity#writeMasterNbt(CompoundTag)} to serialization methods.
 */
public class JAttackEntity extends LivingEntity implements IOwnable {
    protected LivingEntity master;

    protected JAttackEntity(EntityType<? extends LivingEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    public LivingEntity getMaster() {
        return this.master;
    }

    @Override
    public void setMaster(LivingEntity m) {
        this.master = m;
    }

    @Override
    public @NonNull Iterable<ItemStack> getArmorSlots() {
        return Collections.emptyList();
    }

    @Override
    public @NonNull ItemStack getItemBySlot(@NonNull EquipmentSlot slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItemSlot(@NonNull EquipmentSlot slot, @NonNull ItemStack stack) {
        JCraft.LOGGER.warn("Attempted to set item slot of " + getClass().getName() + "@" + hashCode());
    }

    @Override
    public boolean shouldShowName() {
        return false;
    }

    public void writeMasterNbt(CompoundTag nbt) {
        if (master == null) {
            return;
        }
        final boolean ownerIsPlayer = master instanceof Player;
        nbt.putBoolean("OwnerIsPlayer", ownerIsPlayer);
        if (ownerIsPlayer) {
            nbt.putUUID("MasterUUID", master.getUUID());
        } else {
            nbt.putInt("MasterID", master.getId());
        }
    }

    public void readMasterNbt(CompoundTag nbt) {
        final boolean ownerIsPlayer = nbt.getBoolean("OwnerIsPlayer");
        if (ownerIsPlayer) {
            master = level().getPlayerByUUID(nbt.getUUID("MasterUUID"));
        } else {
            if (level().getEntity(nbt.getInt("MasterID")) instanceof LivingEntity living)
                master = living;
        }
    }

    @Override
    public @NonNull HumanoidArm getMainArm() {
        return HumanoidArm.RIGHT;
    }
}
