package net.arna.jcraft.common.attack.moves.dirtydeedsdonedirtcheap;

import lombok.NonNull;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.core.ctx.MoveVariable;
import net.arna.jcraft.common.attack.moves.base.AbstractMove;
import net.arna.jcraft.common.entity.PlayerCloneEntity;
import net.arna.jcraft.common.entity.stand.D4CEntity;
import net.arna.jcraft.common.entity.stand.StandType;
import net.arna.jcraft.platform.JComponentPlatformUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import java.util.Set;

public final class CloneSpawnMove extends AbstractMove<CloneSpawnMove, D4CEntity> {
    public enum CloneType {
        SWORD(Items.IRON_SWORD),
        AXE(Items.WOODEN_AXE),
        BOW(Items.BOW),
        EMPTY(Items.AIR);

        public final Item weapon;

        CloneType(Item weapon) {
            this.weapon = weapon;
        }
    }

    public static final MoveVariable<CloneType> CLONE_TYPE = new MoveVariable<>(CloneType.class);

    public CloneSpawnMove(int cooldown, int windup, int duration, float moveDistance) {
        super(cooldown, windup, duration, moveDistance);
        ranged = true;
    }

    @Override
    public void onInitiate(D4CEntity attacker) {
        super.onInitiate(attacker);
        attacker.getMoveContext().set(CLONE_TYPE, CloneType.SWORD);
    }

    @Override
    public @NonNull Set<LivingEntity> perform(D4CEntity attacker, LivingEntity user, MoveContext ctx) {
        final ItemStack weapon = ctx.get(CLONE_TYPE).weapon.getDefaultInstance();
        if (weapon.isDamageableItem()) {
            weapon.setDamageValue(weapon.getMaxDamage());
        }

        if (user instanceof ServerPlayer playerEntity) {
            final PlayerCloneEntity clone = new PlayerCloneEntity(attacker.level());
            clone.copyPosition(playerEntity);
            clone.setMaster(playerEntity);
            clone.disableDrops();

            attacker.level().addFreshEntity(clone);
            clone.setItemSlot(EquipmentSlot.MAINHAND, weapon);
            JComponentPlatformUtils.getStandData(clone).setType(StandType.NONE);
        } else if (user instanceof Mob mob) { //Code sourced from MobEntity.class convertTo()
            final EntityType<?> entityType = mob.getType();
            final Mob newMob = (Mob) entityType.create(attacker.level());

            if (newMob == null) {
                JCraft.LOGGER.error("Failed to create D4C clone mob of type " + entityType + " in world " + attacker.level());
                return Set.of();
            }

            newMob.copyPosition(mob);
            newMob.setBaby(mob.isBaby());

            if (mob.hasCustomName()) {
                newMob.setCustomName(mob.getCustomName());
                newMob.setCustomNameVisible(mob.isCustomNameVisible());
            }

            newMob.tickCount = mob.tickCount;

            attacker.level().addFreshEntity(newMob);
            newMob.setItemSlot(EquipmentSlot.MAINHAND, weapon);
            JComponentPlatformUtils.getStandData(newMob).setType(StandType.NONE);
        }

        return Set.of();
    }

    @Override
    public void registerContextEntries(MoveContext ctx) {
        ctx.register(CLONE_TYPE, CloneType.SWORD);
    }

    @Override
    protected @NonNull CloneSpawnMove getThis() {
        return this;
    }

    @Override
    public @NonNull CloneSpawnMove copy() {
        return copyExtras(new CloneSpawnMove(getCooldown(), getWindup(), getDuration(), getMoveDistance()));
    }
}
