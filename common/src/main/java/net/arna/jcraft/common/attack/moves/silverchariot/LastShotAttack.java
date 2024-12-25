package net.arna.jcraft.common.attack.moves.silverchariot;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.arna.jcraft.common.attack.core.data.MoveType;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.moves.base.AbstractMove;
import net.arna.jcraft.common.entity.projectile.RapierProjectile;
import net.arna.jcraft.common.entity.stand.SilverChariotEntity;
import net.minecraft.world.entity.LivingEntity;
import java.util.Set;

public final class LastShotAttack extends AbstractMove<LastShotAttack, SilverChariotEntity> {
    public LastShotAttack(final int cooldown, final int windup, final int duration, final float moveDistance) {
        super(cooldown, windup, duration, moveDistance);
        ranged = true;
    }

    @Override
    public @NonNull MoveType<LastShotAttack> getMoveType() {
        return Type.INSTANCE;
    }

    @Override
    public @NonNull Set<LivingEntity> perform(final SilverChariotEntity attacker, final LivingEntity user, final MoveContext ctx) {
        if (!attacker.hasRapier()) {
            return Set.of();
        }

        final RapierProjectile rapier = new RapierProjectile(attacker.level(), user, attacker);
        rapier.shootFromRotation(attacker, user.getXRot(), user.getYRot(), 0, 2, 1);
        rapier.setSkin(attacker.getMode() != SilverChariotEntity.Mode.ARMORLESS ?
                -attacker.getMode().ordinal() : // Armorless and possessed output -1 and -2
                attacker.getSkin());
        attacker.level().addFreshEntity(rapier);
        attacker.setHasRapier(false);

        return Set.of();
    }

    @Override
    protected @NonNull LastShotAttack getThis() {
        return this;
    }

    @Override
    public @NonNull LastShotAttack copy() {
        return copyExtras(new LastShotAttack(getCooldown(), getWindup(), getDuration(), getMoveDistance()));
    }

    public static class Type extends AbstractMove.Type<LastShotAttack> {
        public static final Type INSTANCE = new Type();

        @Override
        protected @NonNull App<RecordCodecBuilder.Mu<LastShotAttack>, LastShotAttack> buildCodec(RecordCodecBuilder.Instance<LastShotAttack> instance) {
            return baseDefault(instance, LastShotAttack::new);
        }
    }
}
