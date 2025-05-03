package net.arna.jcraft.common.attack.core;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.arna.jcraft.common.attack.moves.base.AbstractMove;
import net.arna.jcraft.common.util.JCodecUtils;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;
import java.util.Set;
import java.util.function.Supplier;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class RunMoment {
    public static RunMoment NEVER, AT_INIT, ON_STRIKE, ON_HIT, AT_END, EVERY_TICK;

    static {
        NEVER = create("never", RunMomentType.unit(() -> NEVER),
                (move, attacker, user, tick, targets) -> false);
        AT_INIT = create("at_init", RunMomentType.unit(() -> AT_INIT), // Special case
                (move, attacker, user, tick, targets) -> tick == 0);
        ON_STRIKE = create("on_strike", RunMomentType.unit(() -> ON_STRIKE),
                (move, attacker, user, tick, targets) -> tick == move.getWindup() && targets == null);
        ON_HIT = create("on_hit", RunMomentType.unit(() -> ON_HIT),
                (move, attacker, user, tick, targets) -> tick == move.getWindup() && targets != null && !targets.isEmpty());
        AT_END = create("on_end", RunMomentType.unit(() -> AT_END),
                (move, attacker, user, tick, targets) -> tick == move.getDuration());
        EVERY_TICK = create("every_tick", RunMomentType.unit(() -> EVERY_TICK),
                (move, attacker, user, tick, targets) -> true);
    }

    private static final BiMap<String, RunMomentType<?>> BY_NAME = HashBiMap.create();

    public static MapCodec<RunMoment> CODEC = JCodecUtils.codecFromMap(Codec.STRING, BY_NAME)
            .dispatchMap("run", RunMoment::getType, RunMomentType::getCodec);

    @Getter
    private final RunMomentType<?> type;

    private static RunMoment create(String name, RunMomentType<?> type, RunMomentFunc func) {
        RunMoment runMoment = new RunMoment(type) {
            @Override
            public boolean shouldRun(final AbstractMove<?, ?> move, final IAttacker<?, ?> attacker, final LivingEntity user,
                                     final int tick, final @Nullable Set<LivingEntity> targets) {
                return func.shouldRun(move, attacker, user, tick, targets);
            }
        };

        BY_NAME.put(name, type);
        return runMoment;
    }

    public static RunMoment atTick(final int tick) {
        return new TickRunMoment(tick);
    }

    public abstract boolean shouldRun(final AbstractMove<?, ?> move, final IAttacker<?, ?> attacker, final LivingEntity user,
                                      final int tick, final @Nullable Set<LivingEntity> targets);

    public interface RunMomentType<T extends RunMoment> {
        Codec<T> getCodec();

        static <T extends RunMoment> RunMomentType<T> unit(Supplier<T> runMoment) {
            return () -> Codec.unit(runMoment);
        }
    }

    @FunctionalInterface
    private interface RunMomentFunc {
        boolean shouldRun(final AbstractMove<?, ?> move, final IAttacker<?, ?> attacker, final LivingEntity user,
                          final int tick, final @Nullable Set<LivingEntity> targets);
    }

    private static class TickRunMoment extends RunMoment {
        private final int tick;

        public TickRunMoment(final int tick) {
            super(TickRunMomentType.INSTANCE);
            this.tick = tick;
        }

        @Override
        public boolean shouldRun(final AbstractMove<?, ?> move, final IAttacker<?, ?> attacker, final LivingEntity user,
                                 final int tick, final @Nullable Set<LivingEntity> targets) {
            return tick == this.tick;
        }
    }

    private static class TickRunMomentType implements RunMomentType<TickRunMoment> {
        public static final TickRunMomentType INSTANCE = new TickRunMomentType();
        @Getter
        private static final Codec<TickRunMoment> codec = RecordCodecBuilder.create(instance ->
                instance.group(
                        Codec.INT.fieldOf("tick").forGetter(runMoment -> runMoment.tick)
                ).apply(instance, TickRunMoment::new)
        );
    }
}
