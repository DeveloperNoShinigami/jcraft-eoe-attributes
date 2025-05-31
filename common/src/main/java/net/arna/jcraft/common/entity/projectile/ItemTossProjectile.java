package net.arna.jcraft.common.entity.projectile;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.arna.jcraft.registry.JEntityTypeRegistry;
import net.arna.jcraft.registry.JTagRegistry;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Arrays;

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
        // this part has been heavily inspired by AbstractArrow
        Entity entity = result.getEntity();
        Entity entity2 = this.getOwner();
        DamageSource damageSource;
        if (entity2 == null) {
            damageSource = this.damageSources().arrow(this, this);
        } else {
            damageSource = this.damageSources().arrow(this, entity2);
            if (entity2 instanceof LivingEntity) {
                ((LivingEntity)entity2).setLastHurtMob(entity);
            }
        }

        boolean bl = entity.getType() == EntityType.ENDERMAN;
        if (this.isOnFire() && !bl) {
            entity.setSecondsOnFire(5);
        }

        entity.hurt(damageSource, (float)getBaseDamage());
        if (bl) {
            return;
        }

        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity)entity;
            // handle knockback
            if (this.getKnockback() > 0) {
                double d = Math.max(0, 1f - livingEntity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
                Vec3 vec3 = this.getDeltaMovement().multiply(1f, 0f, 1f).normalize().scale(getKnockback() * 0.6 * d);
                if (vec3.lengthSqr() > 0) {
                    livingEntity.push(vec3.x, 0.1, vec3.z);
                }
            }
//            if (!this.level().isClientSide && entity2 instanceof LivingEntity) {
//                EnchantmentHelper.doPostHurtEffects(livingEntity, entity2);
//                EnchantmentHelper.doPostDamageEffects((LivingEntity)entity2, livingEntity);
//            }
            this.doPostHurtEffects(livingEntity);
//            if (entity2 != null && livingEntity != entity2 && livingEntity instanceof Player && entity2 instanceof ServerPlayer && !this.isSilent()) {
//                ((ServerPlayer)entity2).connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.ARROW_HIT_PLAYER, 0.0F));
//            }
        }
        // end of inspired part
        if (!this.level().isClientSide && getItem().is(JTagRegistry.EXPLODES_ON_IMPACT)) {
            entity.hurt(this.damageSources().arrow(this, entity2), 6f);
        }
        // a little bit more inspiration
        if (this.getPierceLevel() <= 0) {
            this.discard();
        }
    }
}
