package net.arna.jcraft.common.attack.moves.dirtydeedsdonedirtcheap;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.arna.jcraft.api.attack.MoveType;
import net.arna.jcraft.api.attack.moves.AbstractMove;
import net.arna.jcraft.common.entity.stand.D4CEntity;
import net.arna.jcraft.api.attack.enums.MobilityType;
import net.arna.jcraft.registry.JStatusRegistry;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import java.util.Set;

public final class FlagMove extends AbstractMove<FlagMove, D4CEntity> {
    public FlagMove(final int cooldown, final int windup, final int duration, final float moveDistance) {
        super(cooldown, windup, duration, moveDistance);
        mobilityType = MobilityType.HIGHJUMP;
    }

    @Override
    public @NonNull MoveType<FlagMove> getMoveType() {
        return Type.INSTANCE;
    }

    @Override
    public void onInitiate(final D4CEntity attacker) {
        super.onInitiate(attacker);

        attacker.getUserOrThrow().addEffect(
                new MobEffectInstance(JStatusRegistry.KNOCKDOWN.get(), getDuration(), 0, true, false)
        );
        attacker.getUserOrThrow().addEffect(
                new MobEffectInstance(MobEffects.SLOW_FALLING, getDuration(), 0, true, false)
        );
    }

    @Override
    public @NonNull Set<LivingEntity> perform(final D4CEntity attacker, final LivingEntity user) {
        int duration = getWindupPoint();
        user.addEffect(
                new MobEffectInstance(MobEffects.INVISIBILITY, duration, 0, true, false)
        );
        user.addEffect(
                new MobEffectInstance(MobEffects.LEVITATION, duration, 2, true, false)
        );

        return Set.of();
    }

    @Override
    protected @NonNull FlagMove getThis() {
        return this;
    }

    @Override
    public @NonNull FlagMove copy() {
        return copyExtras(new FlagMove(getCooldown(), getWindup(), getDuration(), getMoveDistance()));
    }

    public static class Type extends AbstractMove.Type<FlagMove> {
        public static final Type INSTANCE = new Type();

        @Override
        protected @NonNull App<RecordCodecBuilder.Mu<FlagMove>, FlagMove> buildCodec(RecordCodecBuilder.Instance<FlagMove> instance) {
            return baseDefault(instance, FlagMove::new);
        }
    }
}
