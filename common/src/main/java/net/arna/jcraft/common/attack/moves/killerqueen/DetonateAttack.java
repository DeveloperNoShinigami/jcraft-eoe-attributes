package net.arna.jcraft.common.attack.moves.killerqueen;

import lombok.NonNull;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.moves.base.AbstractMove;
import net.arna.jcraft.common.attack.moves.base.AbstractSimpleAttack;
import net.arna.jcraft.common.component.living.CommonBombTrackerComponent;
import net.arna.jcraft.common.entity.damage.JDamageSources;
import net.arna.jcraft.common.entity.projectile.BubbleProjectile;
import net.arna.jcraft.common.entity.stand.AbstractKillerQueenEntity;
import net.arna.jcraft.common.entity.stand.StandEntity;
import net.arna.jcraft.common.util.JParticleType;
import net.arna.jcraft.common.util.JUtils;
import net.arna.jcraft.platform.JComponentPlatformUtils;
import net.arna.jcraft.registry.JSoundRegistry;
import net.arna.jcraft.registry.JStatusRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.Vec3;
import java.util.Set;

public final class DetonateAttack extends AbstractMove<DetonateAttack, AbstractKillerQueenEntity<?, ?>> {
    public DetonateAttack(final int cooldown, final int windup, final int duration, final float moveDistance) {
        super(cooldown, windup, duration, moveDistance);
    }

    @Override
    public @NonNull Set<LivingEntity> perform(final AbstractKillerQueenEntity<?, ?> attacker, final LivingEntity user, final MoveContext ctx) {
        final CommonBombTrackerComponent.BombData bombData = JComponentPlatformUtils.getBombTracker(user).getMainBomb();

        final Entity bombEntity = bombData.bombEntity;
        final Vec3 bombPos = bombData.getBombPos();

        if (bombPos != null) {
            if (bombEntity instanceof ItemEntity || bombEntity instanceof BubbleProjectile) {
                bombEntity.discard();
            }
            explode(attacker, user, bombPos);
        }

        bombData.reset();

        return Set.of();
    }

    @Override
    protected @NonNull DetonateAttack getThis() {
        return this;
    }

    @Override
    public @NonNull DetonateAttack copy() {
        return copyExtras(new DetonateAttack(getCooldown(), getWindup(), getDuration(), getMoveDistance()));
    }

    public static void explode(final AbstractKillerQueenEntity<?, ?> stand, final Entity user, final Vec3 pos) {
        final ServerLevel serverWorld = (ServerLevel) stand.level();

        JCraft.createParticle(serverWorld, pos.x, pos.y, pos.z, JParticleType.BOOM);
        JUtils.serverPlaySound(JSoundRegistry.KQ_EXPLODE.get(), serverWorld, pos, 96);

        final DamageSource damageSource = JDamageSources.stand(stand);
        final Set<? extends LivingEntity> toExplode = AbstractSimpleAttack.findHits(stand, pos, 4.4, damageSource);

        for (LivingEntity living : toExplode) {
            final Vec3 kbVec = living.getEyePosition().subtract(pos).normalize();
            StandEntity.damageLogic(stand.level(), living, kbVec, 2, 3, true, 11f, false, 4, damageSource, user, null);
            living.addEffect(new MobEffectInstance(JStatusRegistry.KNOCKDOWN.get(), 35, 0, true, false));
        }
    }
}
