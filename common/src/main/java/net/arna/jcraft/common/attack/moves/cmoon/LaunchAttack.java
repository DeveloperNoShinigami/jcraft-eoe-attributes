package net.arna.jcraft.common.attack.moves.cmoon;

import lombok.NonNull;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.moves.base.AbstractSimpleAttack;
import net.arna.jcraft.common.entity.projectile.BlockProjectile;
import net.arna.jcraft.common.entity.stand.CMoonEntity;
import net.arna.jcraft.common.gravity.api.GravityChangerAPI;
import net.arna.jcraft.common.util.JParticleType;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Set;

public final class LaunchAttack extends AbstractSimpleAttack<LaunchAttack, CMoonEntity> {
    public LaunchAttack(int cooldown, int windup, int duration, float moveDistance, float damage, int stun,
                        float hitboxSize, float knockback, float offset) {
        super(cooldown, windup, duration, moveDistance, damage, stun, hitboxSize, knockback, offset);
        ranged = true;
        hitSpark = JParticleType.HIT_SPARK_2;
    }

    @Override
    public @NonNull Set<LivingEntity> perform(CMoonEntity attacker, LivingEntity user, MoveContext ctx) {
        Set<LivingEntity> targets = super.perform(attacker, user, ctx);

        BlockProjectile block = new BlockProjectile(attacker.level());
        BlockState steppingState = attacker.getBlockStateOn();
        if (steppingState.isAir() || !steppingState.canOcclude()) {
            block.setBlockStack(Items.STONE.getDefaultInstance());
        } else {
            block.setBlockStack(steppingState.getBlock().asItem().getDefaultInstance());
        }

        Vec3i hoverDir = GravityChangerAPI.getGravityDirection(user).getNormal().multiply(-1);

        block.setMaster(user);
        block.moveTo(attacker.getX() + hoverDir.getX() * 1.5, attacker.getY() + hoverDir.getY() * 1.5,
                attacker.getZ() + hoverDir.getZ() * 1.5, attacker.getYRot(), attacker.getXRot());
        block.setDeltaMovement(hoverDir.getX() * 0.4, hoverDir.getY() * 0.4, hoverDir.getZ() * 0.4);
        attacker.level().addFreshEntity(block);

        return targets;
    }

    @Override
    protected @NonNull LaunchAttack getThis() {
        return this;
    }

    @Override
    public @NonNull LaunchAttack copy() {
        return copyExtras(new LaunchAttack(getCooldown(), getWindup(), getDuration(), getMoveDistance(), getDamage(),
                getStun(), getHitboxSize(), getKnockback(), getOffset()));
    }
}
