package net.arna.jcraft.common.attack.moves.base;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.entity.stand.StandEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import java.util.Set;
import java.util.function.IntSupplier;

@Getter
public abstract class AbstractTimeStopMove<T extends AbstractTimeStopMove<T, A>, A extends StandEntity<? extends A, ?>> extends AbstractMove<T, A> {
    @Setter
    protected IntSupplier timeStopDuration;
    private static final MobEffectInstance tsBlind = new MobEffectInstance(MobEffects.BLINDNESS, 19, 0, true, false, false);

    protected AbstractTimeStopMove(int cooldown, int windup, int duration, float moveDistance, IntSupplier timeStopDuration) {
        super(cooldown, windup, duration, moveDistance);
        this.timeStopDuration = timeStopDuration;
    }

    @Override
    public @NonNull Set<LivingEntity> perform(A attacker, LivingEntity user, MoveContext ctx) {
        attacker.setTsTime(timeStopDuration.getAsInt());
        attacker.setCurrentMove(null);

        user.addEffect(new MobEffectInstance(tsBlind));

        JCraft.beginTimestop(user, attacker.position(), (ServerLevel) attacker.level(), timeStopDuration.getAsInt());
        return Set.of();
    }
}
