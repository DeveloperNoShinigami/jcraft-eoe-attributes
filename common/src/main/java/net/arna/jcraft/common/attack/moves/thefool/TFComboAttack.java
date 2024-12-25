package net.arna.jcraft.common.attack.moves.thefool;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.IntCollection;
import lombok.NonNull;
import net.arna.jcraft.common.attack.core.data.MoveType;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.moves.base.AbstractMultiHitAttack;
import net.arna.jcraft.common.entity.stand.TheFoolEntity;
import net.arna.jcraft.registry.JStatusRegistry;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import java.util.Set;

public final class TFComboAttack extends AbstractMultiHitAttack<TFComboAttack, TheFoolEntity> {
    public TFComboAttack(final int cooldown, final int duration, final float moveDistance, final float damage, int stun, final float hitboxSize,
                         final float knockback, final float offset, final @NonNull IntCollection hitMoments) {
        super(cooldown, duration, moveDistance, damage, stun, hitboxSize, knockback, offset, hitMoments);
    }

    @Override
    public @NonNull MoveType<TFComboAttack> getMoveType() {
        return Type.INSTANCE;
    }

    @Override
    public @NonNull Set<LivingEntity> perform(final TheFoolEntity attacker, final LivingEntity user, final MoveContext ctx) {
        final Set<LivingEntity> targets = super.perform(attacker, user, ctx);

        if (getBlow(attacker) == 2) {
            for (LivingEntity ent : targets) {
                ent.addEffect(new MobEffectInstance(JStatusRegistry.KNOCKDOWN.get(), 20, 0, true, false));
            }
        }

        return targets;
    }

    @Override
    protected @NonNull TFComboAttack getThis() {
        return this;
    }

    @Override
    public @NonNull TFComboAttack copy() {
        return copyExtras(new TFComboAttack(getCooldown(), getDuration(), getMoveDistance(), getDamage(), getStun(),
                getHitboxSize(), getKnockback(), getOffset(), getHitMoments()));
    }

    public static class Type extends AbstractMultiHitAttack.Type<TFComboAttack> {
        public static final Type INSTANCE = new Type();

        @Override
        protected @NonNull App<RecordCodecBuilder.Mu<TFComboAttack>, TFComboAttack> buildCodec(RecordCodecBuilder.Instance<TFComboAttack> instance) {
            return multiHitDefault(instance, TFComboAttack::new);
        }
    }
}
