package net.arna.jcraft.common.attack.moves.silverchariot;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.arna.jcraft.api.attack.MoveType;
import net.arna.jcraft.api.attack.moves.AbstractSimpleAttack;
import net.arna.jcraft.common.entity.stand.SilverChariotEntity;
import net.arna.jcraft.common.util.JUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.Set;

public final class CircleSlashAttack extends AbstractSimpleAttack<CircleSlashAttack, SilverChariotEntity> {
    private final float originalDamage;

    public CircleSlashAttack(final int cooldown, final int windup, final int duration, final float moveDistance, final float damage, final int stun,
                             final float hitboxSize, final float knockback, final float offset) {
        super(cooldown, windup, duration, moveDistance, damage, stun, hitboxSize, knockback, offset);
        originalDamage = damage;
    }

    @Override
    public @NonNull MoveType<CircleSlashAttack> getMoveType() {
        return Type.INSTANCE;
    }

    @Override
    public void onInitiate(final SilverChariotEntity attacker) {
        super.onInitiate(attacker);
        withDamage(originalDamage);
    }

    @Override
    public @NonNull Set<LivingEntity> perform(final SilverChariotEntity attacker, final LivingEntity user) {
        final Set<LivingEntity> targets = super.perform(attacker, user);

        //noinspection IntegerDivisionInFloatingPointContext // intended
        withDamage(originalDamage + (getChargeTime() / 10) * 0.75f);
        double launchMultiplier = getDamage() / 5; // damage [6.5 to 11]

        for (LivingEntity living : targets) {
            Vec3 launchVec = living.position().subtract(user.position()).normalize().scale(launchMultiplier);
            JUtils.addVelocity(living, launchVec.x, launchVec.y + 0.2, launchVec.z);
        }

        return targets;
    }

    @Override
    protected @NonNull CircleSlashAttack getThis() {
        return this;
    }

    @Override
    public @NonNull CircleSlashAttack copy() {
        return copyExtras(new CircleSlashAttack(getCooldown(), getWindup(), getDuration(), getMoveDistance(), getDamage(), getStun(),
                getHitboxSize(), getKnockback(), getOffset()));
    }

    public static class Type extends AbstractSimpleAttack.Type<CircleSlashAttack> {
        public static final Type INSTANCE = new Type();

        @Override
        protected @NonNull App<RecordCodecBuilder.Mu<CircleSlashAttack>, CircleSlashAttack> buildCodec(RecordCodecBuilder.Instance<CircleSlashAttack> instance) {
            return attackDefault(instance, CircleSlashAttack::new);
        }
    }
}
