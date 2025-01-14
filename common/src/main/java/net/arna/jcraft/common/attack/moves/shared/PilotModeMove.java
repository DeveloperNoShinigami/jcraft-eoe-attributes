package net.arna.jcraft.common.attack.moves.shared;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.arna.jcraft.common.attack.core.data.MoveType;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.moves.base.AbstractMove;
import net.arna.jcraft.common.entity.stand.StandEntity;
import net.minecraft.world.entity.LivingEntity;
import java.util.Set;

public final class PilotModeMove<A extends StandEntity<? extends A, ?>> extends AbstractMove<PilotModeMove<A>, A> {
    public PilotModeMove(final int cooldown) {
        super(cooldown, 0, 0, 0);
    }

    @Override
    public @NonNull MoveType<PilotModeMove<A>> getMoveType() {
        return Type.INSTANCE.cast();
    }

    @Override
    public @NonNull Set<LivingEntity> perform(final A attacker, final LivingEntity user, final MoveContext ctx) {
        attacker.togglePilotMode();
        return Set.of();
    }

    @Override
    protected @NonNull PilotModeMove<A> getThis() {
        return this;
    }

    @Override
    public @NonNull PilotModeMove<A> copy() {
        return copyExtras(new PilotModeMove<>(getCooldown()));
    }

    public static class Type extends AbstractMove.Type<PilotModeMove<?>> {
        public static final Type INSTANCE = new Type();

        @Override
        protected @NonNull App<RecordCodecBuilder.Mu<PilotModeMove<?>>, PilotModeMove<?>> buildCodec(RecordCodecBuilder.Instance<PilotModeMove<?>> instance) {
            return instance.group(extras(), cooldown()).apply(instance, applyExtras(PilotModeMove::new));
        }
    }
}
