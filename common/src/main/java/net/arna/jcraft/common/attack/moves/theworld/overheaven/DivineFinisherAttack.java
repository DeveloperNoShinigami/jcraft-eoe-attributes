package net.arna.jcraft.common.attack.moves.theworld.overheaven;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.arna.jcraft.api.attack.MoveType;
import net.arna.jcraft.api.attack.moves.AbstractSimpleAttack;
import net.arna.jcraft.common.entity.projectile.KnifeProjectile;
import net.arna.jcraft.common.entity.stand.TheWorldOverHeavenEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.phys.Vec3;
import java.util.Set;

public final class DivineFinisherAttack extends AbstractSimpleAttack<DivineFinisherAttack, TheWorldOverHeavenEntity> {
    public DivineFinisherAttack(final int cooldown, final int windup, final int duration, final float moveDistance, final float damage, final int stun,
                                final float hitboxSize, final float knockback, final float offset) {
        super(cooldown, windup, duration, moveDistance, damage, stun, hitboxSize, knockback, offset);
        ranged = true;
    }

    @Override
    public @NonNull MoveType<DivineFinisherAttack> getMoveType() {
        return Type.INSTANCE.cast();
    }

    @Override
    public @NonNull Set<LivingEntity> perform(final TheWorldOverHeavenEntity attacker, final LivingEntity user) {
        final Set<LivingEntity> targets = super.perform(attacker, user);

        final Vec3 rotVec = user.getLookAngle();

        for (int i = 0; i < 4; i++) {
            KnifeProjectile knife = new KnifeProjectile(attacker.level(), user);
            knife.setDelayedLightning(10 + i * 5);
            knife.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
            knife.setNoGravity(true);
            knife.setDeltaMovement(new Vec3(rotVec.x * 0.7, 0, rotVec.z * 0.7).yRot(1.5708f * i));
            knife.setPos(attacker.getEyePosition());
            attacker.level().addFreshEntity(knife);
        }

        return targets;
    }

    @Override
    protected @NonNull DivineFinisherAttack getThis() {
        return this;
    }

    @Override
    public @NonNull DivineFinisherAttack copy() {
        return copyExtras(new DivineFinisherAttack(getCooldown(), getWindup(), getDuration(), getMoveDistance(),
                getDamage(), getStun(), getHitboxSize(), getKnockback(), getOffset()));
    }

    public static class Type extends AbstractSimpleAttack.Type<DivineFinisherAttack> {
        public static final Type INSTANCE = new Type();

        @Override
        protected @NonNull App<RecordCodecBuilder.Mu<DivineFinisherAttack>, DivineFinisherAttack> buildCodec(RecordCodecBuilder.Instance<DivineFinisherAttack> instance) {
            return attackDefault(instance, DivineFinisherAttack::new);
        }
    }
}
