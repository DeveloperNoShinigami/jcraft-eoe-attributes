package net.arna.jcraft.common.attack.moves.killerqueen;

import lombok.Getter;
import lombok.NonNull;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.moves.base.AbstractMove;
import net.arna.jcraft.common.component.living.CommonBombTrackerComponent;
import net.arna.jcraft.common.entity.damage.JDamageSources;
import net.arna.jcraft.common.entity.stand.KillerQueenEntity;
import net.arna.jcraft.common.entity.stand.StandEntity;
import net.arna.jcraft.common.util.JParticleType;
import net.arna.jcraft.common.util.JUtils;
import net.arna.jcraft.platform.JComponentPlatformUtils;
import net.arna.jcraft.registry.JSoundRegistry;
import net.arna.jcraft.registry.JStatusRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import java.util.Set;

@Getter
public final class KQGrabHitAttack extends AbstractMove<KQGrabHitAttack, KillerQueenEntity> {
    private final int stun;

    public KQGrabHitAttack(int cooldown, int windup, int duration, float moveDistance, int stun) {
        super(cooldown, windup, duration, moveDistance);
        this.stun = stun;
    }

    @Override
    public @NonNull Set<LivingEntity> perform(KillerQueenEntity attacker, LivingEntity user, MoveContext ctx) {
        attacker.playSound(JSoundRegistry.KQ_DETONATE.get(), 1, 1);

        CommonBombTrackerComponent.BombData bombData = JComponentPlatformUtils.getBombTracker(user).getMainBomb();

        if (bombData.bombEntity instanceof LivingEntity livingEntity) {
            ServerLevel world = (ServerLevel) attacker.level();

            Vec3 pos = livingEntity.position();
            JCraft.createParticle(world, pos.x, pos.y, pos.z, JParticleType.BOOM);
            JUtils.serverPlaySound(JSoundRegistry.KQ_EXPLODE.get(), world, pos, 96);

            DamageSource damageSource = JDamageSources.stand(attacker);

            StandEntity.damageLogic(world, livingEntity, new Vec3(0, 1, 0), stun, 3, true,
                    11f, false, 4, damageSource, user, null);
            livingEntity.addEffect(new MobEffectInstance(JStatusRegistry.KNOCKDOWN.get(), 35, 0, true, false));
        }

        bombData.reset();

        return Set.of();
    }

    @Override
    protected @NonNull KQGrabHitAttack getThis() {
        return this;
    }

    @Override
    public @NonNull KQGrabHitAttack copy() {
        return copyExtras(new KQGrabHitAttack(getCooldown(), getWindup(), getDuration(), getMoveDistance(), getStun()));
    }
}
