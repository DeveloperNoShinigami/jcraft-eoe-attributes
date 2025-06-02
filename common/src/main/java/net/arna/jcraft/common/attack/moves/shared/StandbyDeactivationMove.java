package net.arna.jcraft.common.attack.moves.shared;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.arna.jcraft.common.attack.core.IAttacker;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.core.data.MoveType;
import net.arna.jcraft.common.attack.moves.base.AbstractMove;
import net.minecraft.world.entity.LivingEntity;

import java.util.Set;

public final class StandbyDeactivationMove<A extends IAttacker<? extends A, ?>> extends AbstractMove<StandbyDeactivationMove<A>, A> {

    public StandbyDeactivationMove(int cooldown, int windup, int duration) {
        super(cooldown, windup, duration, 0f);
    }

    @Override
    public @NonNull MoveType<StandbyDeactivationMove<A>> getMoveType() {
        return Type.INSTANCE.cast();
    }

    @Override
    public @NonNull Set<LivingEntity> perform(A attacker, LivingEntity user, MoveContext ctx) {
        attacker.setStandby(false);
        return Set.of();
    }

    @Override
    protected @NonNull StandbyDeactivationMove<A> getThis() {
        return this;
    }

    @Override
    public @NonNull StandbyDeactivationMove<A> copy() {
        return copyExtras(new StandbyDeactivationMove<>(getCooldown(), getWindup(), getDuration()));
    }

    public static class Type extends AbstractMove.Type<StandbyDeactivationMove<?>> {
        public static final Type INSTANCE = new Type();

        @Override
        protected @NonNull App<RecordCodecBuilder.Mu<StandbyDeactivationMove<?>>, StandbyDeactivationMove<?>> buildCodec(RecordCodecBuilder.Instance<StandbyDeactivationMove<?>> instance) {
            return instance.group(cooldown(), windup(), duration()).apply(instance, StandbyDeactivationMove::new);
        }
    }
}
