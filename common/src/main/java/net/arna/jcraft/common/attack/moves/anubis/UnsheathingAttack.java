package net.arna.jcraft.common.attack.moves.anubis;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.arna.jcraft.common.attack.core.data.MoveType;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.moves.base.AbstractSimpleAttack;
import net.arna.jcraft.common.spec.AnubisSpec;
import net.minecraft.world.entity.LivingEntity;

import java.util.Set;

public class UnsheathingAttack extends AbstractSimpleAttack<UnsheathingAttack, AnubisSpec> {
    public UnsheathingAttack(int cooldown, int windup, int duration, float moveDistance, float damage, int stun,
                             float hitboxSize, float knockback, float offset) {
        super(cooldown, windup, duration, moveDistance, damage, stun, hitboxSize, knockback, offset);
    }

    @Override
    public @NonNull MoveType<UnsheathingAttack> getMoveType() {
        return Type.INSTANCE;
    }

    @Override
    public boolean conditionsMet(AnubisSpec attacker) {
        return super.conditionsMet(attacker) && attacker.isHoldingSheathedAnubis();
    }

    @Override
    public @NonNull Set<LivingEntity> perform(AnubisSpec attacker, LivingEntity user, MoveContext ctx) {
        Set<LivingEntity> targets = super.perform(attacker, user, ctx);
        attacker.tryIncrementBloodlust(targets);
        attacker.unsheatheAttack(targets);

        return targets;
    }

    @Override
    protected @NonNull UnsheathingAttack getThis() {
        return this;
    }

    @Override
    public @NonNull UnsheathingAttack copy() {
        return copyExtras(new UnsheathingAttack(getCooldown(), getWindup(), getDuration(), getMoveDistance(), getDamage(),
                getStun(), getHitboxSize(), getKnockback(), getOffset()));
    }

    public static class Type extends AbstractSimpleAttack.Type<UnsheathingAttack> {
        public static final Type INSTANCE = new Type();

        @Override
        protected @NonNull App<RecordCodecBuilder.Mu<UnsheathingAttack>, UnsheathingAttack> buildCodec(RecordCodecBuilder.Instance<UnsheathingAttack> instance) {
            return attackDefault(instance, UnsheathingAttack::new);
        }
    }
}
