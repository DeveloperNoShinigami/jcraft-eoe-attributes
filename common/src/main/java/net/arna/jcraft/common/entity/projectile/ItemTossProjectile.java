package net.arna.jcraft.common.entity.projectile;

import net.arna.jcraft.registry.JEntityTypeRegistry;
import net.arna.jcraft.registry.JTagRegistry;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class ItemTossProjectile extends AbstractArrow {

    protected static final EntityDataAccessor<ItemStack> ITEM;

    static {
        ITEM = SynchedEntityData.defineId(ItemTossProjectile.class, EntityDataSerializers.ITEM_STACK);
    }

    public ItemTossProjectile(final Level level) {
        super(JEntityTypeRegistry.ITEM_TOSS_PROJECTILE.get(), level);
        setItem(ItemStack.EMPTY);
    }

    public ItemTossProjectile(final LivingEntity shooter, final Level level, final ItemStack item) {
        super(JEntityTypeRegistry.ITEM_TOSS_PROJECTILE.get(), shooter, level);
        setItem(item);
        if (getItem().is(JTagRegistry.HEAVY_IMPACT)) {
            this.setBaseDamage(2d);
            this.setKnockback(4);
        }
        else {
            this.setBaseDamage(0d);
            this.setKnockback(0);
        }
    }

    public ItemStack getItem() {
        return this.entityData.get(ITEM);
    }

    protected void setItem(ItemStack item) {
        this.entityData.set(ITEM, item.copyWithCount(1));
    }

    @Override
    protected ItemStack getPickupItem() {
        return getItem();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ITEM, ItemStack.EMPTY);
    }

    @Override
    public boolean isOnFire() {
        // this might be better to move to doPostHurtEffects
        return getItem().is(JTagRegistry.BURNS_ON_IMPACT);
    }

    @Override
    protected void doPostHurtEffects(LivingEntity target) {
        if (getItem().is(JTagRegistry.BLINDS_ON_IMPACT)) {
            target.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 40));
        }
        if (getItem().is(JTagRegistry.POISONS_ON_IMPACT)) {
            target.addEffect(new MobEffectInstance(MobEffects.POISON, 60));
        }
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide && getItem().is(JTagRegistry.EXPLODES_ON_IMPACT)) {
            final boolean grief = this.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING);
            this.level().explode(this, this.getX(), this.getY(), this.getZ(), 1, grief, Level.ExplosionInteraction.MOB);
            this.discard();
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (!this.level().isClientSide && getItem().is(JTagRegistry.EXPLODES_ON_IMPACT)) {
            Entity entity = result.getEntity();
            Entity entity1 = this.getOwner();
            entity.hurt(this.damageSources().arrow(this, entity1), 6f);
        }
    }
}
