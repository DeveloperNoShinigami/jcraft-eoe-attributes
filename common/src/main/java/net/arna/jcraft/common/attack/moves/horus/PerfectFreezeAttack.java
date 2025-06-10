package net.arna.jcraft.common.attack.moves.horus;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.api.attack.MoveType;
import net.arna.jcraft.api.attack.moves.AbstractSimpleAttack;
import net.arna.jcraft.common.entity.projectile.IceBranchProjectile;
import net.arna.jcraft.common.entity.stand.HorusEntity;
import net.arna.jcraft.common.util.JParticleType;
import net.arna.jcraft.common.util.JUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;

import java.util.Set;

public class PerfectFreezeAttack extends AbstractSimpleAttack<PerfectFreezeAttack, HorusEntity> {
    public PerfectFreezeAttack(int cooldown, int windup, int duration, float moveDistance, float damage, int stun,
                               float hitboxSize, float knockback, float offset) {
        super(cooldown, windup, duration, moveDistance, damage, stun, hitboxSize, knockback, offset);
    }

    @Override
    public @NonNull MoveType<PerfectFreezeAttack> getMoveType() {
        return Type.INSTANCE;
    }

    @Override
    public @NonNull Set<LivingEntity> perform(HorusEntity attacker, LivingEntity user) {
        Set<LivingEntity> targets = super.perform(attacker, user);

        final int NUM_BRANCHES = 3;
        for (int i = 0; i < NUM_BRANCHES; i++) {
            final IceBranchProjectile iceBranch = new IceBranchProjectile(attacker.level(), user, 0);
            iceBranch.moveTo(attacker.getX(), attacker.getY(), attacker.getZ(), -attacker.getYRot() + 180, -attacker.getXRot());
            iceBranch.setYRot(iceBranch.getYRot() + (360.0F * i) / NUM_BRANCHES);
            attacker.level().addFreshEntity(iceBranch);
        }
        attacker.level().getEntitiesOfClass(Projectile.class, attacker.getBoundingBox().inflate(5.0)).forEach(
                p -> JUtils.setVelocity(p, 0, 0, 0)
        );
        JCraft.createParticle((ServerLevel) attacker.level(), attacker.getX(), attacker.getY(), attacker.getZ(), JParticleType.FLASH);

        // Add slowness effect to hit entities
        targets.forEach(t -> t.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 0, false, true)));

        return targets;
    }

    @Override
    protected @NonNull PerfectFreezeAttack getThis() {
        return this;
    }

    @Override
    public @NonNull PerfectFreezeAttack copy() {
        return copyExtras(new PerfectFreezeAttack(getCooldown(), getWindup(), getDuration(), getMoveDistance(),
                getDamage(), getStun(), getHitboxSize(), getKnockback(), getOffset()));
    }

    public static class Type extends AbstractSimpleAttack.Type<PerfectFreezeAttack> {
        public static final Type INSTANCE = new Type();

        @Override
        protected @NonNull App<RecordCodecBuilder.Mu<PerfectFreezeAttack>, PerfectFreezeAttack> buildCodec(RecordCodecBuilder.Instance<PerfectFreezeAttack> instance) {
            return attackDefault(instance, PerfectFreezeAttack::new);
        }
    }
}
