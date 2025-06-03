package net.arna.jcraft.common.attack.moves.magiciansred;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.arna.jcraft.api.attack.MoveType;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.moves.base.AbstractMove;
import net.arna.jcraft.common.entity.projectile.AnkhProjectile;
import net.arna.jcraft.common.entity.stand.MagiciansRedEntity;
import net.minecraft.world.entity.LivingEntity;
import java.util.Set;

public final class CrossfireVariationAttack extends AbstractMove<CrossfireVariationAttack, MagiciansRedEntity> {
    private static final int variationAnkhs = 6;

    public CrossfireVariationAttack(final int cooldown, final int windup, final int moveStunTicks, final float moveDistance) {
        super(cooldown, windup, moveStunTicks, moveDistance);
        ranged = true;
    }

    @Override
    public @NonNull MoveType<CrossfireVariationAttack> getMoveType() {
        return Type.INSTANCE;
    }

    @Override
    public @NonNull Set<LivingEntity> perform(final MagiciansRedEntity attacker, final LivingEntity user, final MoveContext ctx) {
        int orbitRange = user.isShiftKeyDown() ? 7 : 5;
        for (int i = 0; i < variationAnkhs; i++) {
            final AnkhProjectile ankh = new AnkhProjectile(attacker.level(), user);
            ankh.setDeltaMovement(0.0, 1.0, 0.0);
            ankh.setPos(getOffsetHeightPos(attacker).add(0.0, 1.0, 0.0));
            ankh.setVariation(true);
            ankh.setOrbitRange(orbitRange);
            ankh.setOrbitOffset((360f / variationAnkhs) * i);
            attacker.level().addFreshEntity(ankh);
        }

        return Set.of();
    }

    @Override
    protected @NonNull CrossfireVariationAttack getThis() {
        return this;
    }

    @Override
    public @NonNull CrossfireVariationAttack copy() {
        return copyExtras(new CrossfireVariationAttack(getCooldown(), getWindup(), getDuration(), getMoveDistance()));
    }

    public static class Type extends AbstractMove.Type<CrossfireVariationAttack> {
        public static final Type INSTANCE = new Type();

        @Override
        protected @NonNull App<RecordCodecBuilder.Mu<CrossfireVariationAttack>, CrossfireVariationAttack> buildCodec(RecordCodecBuilder.Instance<CrossfireVariationAttack> instance) {
            return baseDefault(instance, CrossfireVariationAttack::new);
        }
    }
}
