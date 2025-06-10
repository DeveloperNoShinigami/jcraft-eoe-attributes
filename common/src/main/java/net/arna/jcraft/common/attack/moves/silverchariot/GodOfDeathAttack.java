package net.arna.jcraft.common.attack.moves.silverchariot;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.api.attack.MoveType;
import net.arna.jcraft.api.attack.moves.AbstractSimpleAttack;
import net.arna.jcraft.common.entity.stand.SilverChariotEntity;
import net.arna.jcraft.common.util.JParticleType;
import net.minecraft.world.entity.LivingEntity;
import java.util.Set;

public final class GodOfDeathAttack extends AbstractSimpleAttack<GodOfDeathAttack, SilverChariotEntity> {
    public GodOfDeathAttack(final int cooldown, final int windup, final int duration, final float moveDistance, final float damage, final int stun,
                            final float hitboxSize, final float knockback, final float offset) {
        super(cooldown, windup, duration, moveDistance, damage, stun, hitboxSize, knockback, offset);
        hitSpark = JParticleType.SWEEP_ATTACK;
    }

    @Override
    public @NonNull MoveType<GodOfDeathAttack> getMoveType() {
        return Type.INSTANCE;
    }

    @Override
    public @NonNull Set<LivingEntity> perform(final SilverChariotEntity attacker, final LivingEntity user) {
        Set<LivingEntity> targets = super.perform(attacker, user);

        if (targets.isEmpty()) {
            JCraft.stun(user, 30, 1);
        } else {
            attacker.setMove(getFollowup(), SilverChariotEntity.State.BEAT_DOWN);
        }

        return targets;
    }

    @Override
    protected @NonNull GodOfDeathAttack getThis() {
        return this;
    }

    @Override
    public @NonNull GodOfDeathAttack copy() {
        return copyExtras(new GodOfDeathAttack(getCooldown(), getWindup(), getDuration(), getMoveDistance(), getDamage(), getStun(),
                getHitboxSize(), getKnockback(), getOffset()));
    }

    public static class Type extends AbstractSimpleAttack.Type<GodOfDeathAttack> {
        public static final Type INSTANCE = new Type();

        @Override
        protected @NonNull App<RecordCodecBuilder.Mu<GodOfDeathAttack>, GodOfDeathAttack> buildCodec(RecordCodecBuilder.Instance<GodOfDeathAttack> instance) {
            return attackDefault(instance, GodOfDeathAttack::new);
        }
    }
}
