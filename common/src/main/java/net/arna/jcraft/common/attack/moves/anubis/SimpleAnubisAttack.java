package net.arna.jcraft.common.attack.moves.anubis;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import lombok.NonNull;
import net.arna.jcraft.api.attack.MoveType;
import net.arna.jcraft.api.attack.moves.AbstractSimpleAttack;
import net.arna.jcraft.common.spec.AnubisSpec;
import net.minecraft.world.entity.LivingEntity;

import java.util.Set;

@Getter
public class SimpleAnubisAttack extends AbstractSimpleAttack<SimpleAnubisAttack, AnubisSpec> {
    private final boolean checkHoldingAnubis;
    private final boolean incrementBloodlust;

    public SimpleAnubisAttack(int cooldown, int windup, int duration, float moveDistance, float damage, int stun,
                              float hitboxSize, float knockback, float offset, boolean checkHoldingAnubis, boolean incrementBloodlust) {
        super(cooldown, windup, duration, moveDistance, damage, stun, hitboxSize, knockback, offset);
        this.checkHoldingAnubis = checkHoldingAnubis;
        this.incrementBloodlust = incrementBloodlust;
    }

    @Override
    public @NonNull MoveType<SimpleAnubisAttack> getMoveType() {
        return Type.INSTANCE;
    }

    @Override
    public boolean conditionsMet(AnubisSpec attacker) {
        return super.conditionsMet(attacker) && (!checkHoldingAnubis || attacker.isHoldingAnubis());
    }

    @Override
    public @NonNull Set<LivingEntity> perform(AnubisSpec attacker, LivingEntity user) {
        Set<LivingEntity> targets = super.perform(attacker, user);
        if (incrementBloodlust) attacker.tryIncrementBloodlust(targets);

        return targets;
    }

    @Override
    protected @NonNull SimpleAnubisAttack getThis() {
        return this;
    }

    @Override
    public @NonNull SimpleAnubisAttack copy() {
        return copyExtras(new SimpleAnubisAttack(getCooldown(), getWindup(), getDuration(), getMoveDistance(), getDamage(),
                getStun(), getHitboxSize(), getKnockback(), getOffset(), checkHoldingAnubis, incrementBloodlust));
    }

    public static class Type extends AbstractSimpleAttack.Type<SimpleAnubisAttack> {
        public static final Type INSTANCE = new Type();

        @Override
        protected @NonNull App<RecordCodecBuilder.Mu<SimpleAnubisAttack>, SimpleAnubisAttack> buildCodec(RecordCodecBuilder.Instance<SimpleAnubisAttack> instance) {
            return instance.group(extras(), attackExtras(), cooldown(), windup(), duration(), moveDistance(), damage(), stun(),
                    hitboxSize(), knockback(), offset(),
                    Codec.BOOL.fieldOf("check_holding_anubis").forGetter(SimpleAnubisAttack::isCheckHoldingAnubis),
                    Codec.BOOL.fieldOf("increment_bloodlust").forGetter(SimpleAnubisAttack::isIncrementBloodlust))
                    .apply(instance, applyAttackExtras(SimpleAnubisAttack::new));
        }
    }
}
