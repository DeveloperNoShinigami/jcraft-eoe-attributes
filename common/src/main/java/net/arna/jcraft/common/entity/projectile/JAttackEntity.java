package net.arna.jcraft.common.entity.projectile;

import net.arna.jcraft.common.util.IOwnable;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import java.util.List;

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
    public Iterable<ItemStack> getArmorSlots() {
        return List.of();
    }

    @Override
    public ItemStack getItemBySlot(EquipmentSlot slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItemSlot(EquipmentSlot slot, ItemStack stack) {

    }

    @Override
    public boolean shouldShowName() {
        return false;
    }

    @Override
    public HumanoidArm getMainArm() {
        return null;
    }
}
