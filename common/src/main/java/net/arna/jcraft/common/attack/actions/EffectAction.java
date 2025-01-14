package net.arna.jcraft.common.attack.actions;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.arna.jcraft.common.attack.core.IAttacker;
import net.arna.jcraft.common.attack.core.MoveAction;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.core.data.MoveActionType;
import net.arna.jcraft.common.util.JCodecUtils;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class EffectAction extends MoveAction<EffectAction, IAttacker<?, ?>> {
    private final List<Supplier<MobEffectInstance>> effects;

    public static EffectAction inflict(final MobEffect effect, final int duration, final int amplifier) {
        return inflict(new MobEffectInstance(effect, duration, amplifier));
    }

    public static EffectAction inflict(final MobEffect effect, final int duration, final int amplifier,
                                       final boolean ambient, final boolean visible) {
        return inflict(new MobEffectInstance(effect, duration, amplifier, ambient, visible));
    }

    public static EffectAction inflict(final MobEffect effect, final int duration, final int amplifier,
                                       final boolean ambient, final boolean visible, final boolean showIcon) {
        return inflict(new MobEffectInstance(effect, duration, amplifier, ambient, visible, showIcon));
    }

    public static EffectAction inflict(final Supplier<MobEffect> effect, final int duration, final int amplifier) {
        return inflict(() -> new MobEffectInstance(effect.get(), duration, amplifier));
    }

    public static EffectAction inflict(final Supplier<MobEffect> effect, final int duration, final int amplifier,
                                       final boolean ambient, final boolean visible) {
        return inflict(() -> new MobEffectInstance(effect.get(), duration, amplifier, ambient, visible));
    }

    public static EffectAction inflict(final Supplier<MobEffect> effect, final int duration, final int amplifier,
                                       final boolean ambient, final boolean visible, final boolean showIcon) {
        return inflict(() -> new MobEffectInstance(effect.get(), duration, amplifier, ambient, visible, showIcon));
    }

    public static EffectAction inflict(final MobEffectInstance effect) {
        return new SingleEffectAction(() -> effect);
    }

    public static EffectAction inflict(final MobEffectInstance... effects) {
        return effects.length == 1
                ? new SingleEffectAction(Suppliers.memoize(() -> effects[0]))
                : new EffectAction(Stream.of(effects)
                    .map(inst -> (Supplier<MobEffectInstance>) () -> inst)
                    .toList());
    }

    public static EffectAction inflict(final Supplier<MobEffectInstance> effect) {
        return new SingleEffectAction(effect);
    }

    public static EffectAction inflict(final Collection<Supplier<MobEffectInstance>> effects) {
        return effects.size() == 1
                ? new SingleEffectAction(effects.iterator().next())
                : new EffectAction(ImmutableList.copyOf(effects));
    }

    @Override
    public void perform(final IAttacker<?, ?> attacker, final LivingEntity user, final MoveContext ctx, final Set<LivingEntity> targets) {
        // Clone effects and add them to the targets
        for (LivingEntity target : targets) effects.stream()
                .map(Supplier::get)
                .map(MobEffectInstance::new)
                .forEach(target::addEffect);
    }

    @Override
    public @NonNull MoveActionType<EffectAction> getType() {
        return Type.INSTANCE;
    }

    // Small optimization for actions that only inflict a single effect.
    // Most of the time, only one effect is inflicted, so this can save a bit of performance.
    private static class SingleEffectAction extends EffectAction {
        private final Supplier<MobEffectInstance> effect;

        public SingleEffectAction(final Supplier<MobEffectInstance> effect) {
            super(ImmutableList.of(effect));
            this.effect = effect;
        }

        @Override
        public void perform(final IAttacker<?, ?> attacker, final LivingEntity user, final MoveContext ctx, final Set<LivingEntity> targets) {
            for (LivingEntity target : targets) target.addEffect(new MobEffectInstance(effect.get()));
        }
    }

    public static class Type implements MoveActionType<EffectAction> {
        public static final Type INSTANCE = new Type();

        @Override
        public Codec<EffectAction> getCodec() {
            return RecordCodecBuilder.create(instance -> instance.group(
                    JCodecUtils.MOB_EFFECT_INSTANCE_CODEC.listOf().fieldOf("effects").forGetter(a ->
                            a.getEffects().stream().map(Supplier::get).toList())
            ).apply(instance, effects -> inflict(effects.toArray(MobEffectInstance[]::new))));
        }
    }
}
