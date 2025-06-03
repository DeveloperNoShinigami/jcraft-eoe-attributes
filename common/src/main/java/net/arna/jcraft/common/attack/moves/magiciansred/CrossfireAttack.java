package net.arna.jcraft.common.attack.moves.magiciansred;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.arna.jcraft.api.attack.MoveType;
import net.arna.jcraft.common.attack.moves.base.AbstractMove;
import net.arna.jcraft.common.entity.projectile.AnkhProjectile;
import net.arna.jcraft.common.entity.stand.MagiciansRedEntity;
import net.minecraft.world.entity.LivingEntity;
import java.util.Set;

public final class CrossfireAttack extends AbstractMove<CrossfireAttack, MagiciansRedEntity> {
    public CrossfireAttack(final int cooldown, final int windup, final int duration, final float moveDistance) {
        super(cooldown, windup, duration, moveDistance);
        ranged = true;
    }

    @Override
    public @NonNull MoveType<CrossfireAttack> getMoveType() {
        return Type.INSTANCE;
    }

    @Override
    public @NonNull Set<LivingEntity> perform(final MagiciansRedEntity attacker, final LivingEntity user) {
        for (int i = 0; i < 3; i++) {
            final AnkhProjectile ankh = new AnkhProjectile(attacker.level(), user);
            ankh.shootFromRotation(user, user.getXRot(), user.getYRot(), 0.0F, 1F, 5F);
            ankh.setPos(getOffsetHeightPos(attacker));
            attacker.level().addFreshEntity(ankh);
        }

        return Set.of();
    }

    @Override
    protected @NonNull CrossfireAttack getThis() {
        return this;
    }

    @Override
    public @NonNull CrossfireAttack copy() {
        return copyExtras(new CrossfireAttack(getCooldown(), getWindup(), getDuration(), getMoveDistance()));
    }

    public static class Type extends AbstractMove.Type<CrossfireAttack> {
        public static final Type INSTANCE = new Type();

        @Override
        protected @NonNull App<RecordCodecBuilder.Mu<CrossfireAttack>, CrossfireAttack> buildCodec(RecordCodecBuilder.Instance<CrossfireAttack> instance) {
            return baseDefault(instance, CrossfireAttack::new);
        }
    }
}
