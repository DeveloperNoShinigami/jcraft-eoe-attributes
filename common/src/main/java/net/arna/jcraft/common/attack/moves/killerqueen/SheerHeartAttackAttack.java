package net.arna.jcraft.common.attack.moves.killerqueen;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.arna.jcraft.common.attack.core.data.MoveType;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.moves.base.AbstractMove;
import net.arna.jcraft.common.entity.SheerHeartAttackEntity;
import net.arna.jcraft.common.entity.stand.KillerQueenEntity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public final class SheerHeartAttackAttack extends AbstractMove<SheerHeartAttackAttack, KillerQueenEntity> {
    public SheerHeartAttackAttack(final int cooldown, final int windup, final int duration, final float moveDistance) {
        super(cooldown, windup, duration, moveDistance);
        ranged = true;
    }

    @Override
    public @NotNull MoveType<SheerHeartAttackAttack> getMoveType() {
        return Type.INSTANCE;
    }

    @Override
    public @NonNull Set<LivingEntity> perform(final KillerQueenEntity attacker, final LivingEntity user, final MoveContext ctx) {
        final SheerHeartAttackEntity sha = new SheerHeartAttackEntity(attacker.level());
        sha.setMaster(user);
        sha.moveTo(attacker.getX(), attacker.getY() + 0.5, attacker.getZ(), attacker.getYRot(), attacker.getXRot());
        attacker.level().addFreshEntity(sha);

        return Set.of();
    }

    @Override
    protected @NonNull SheerHeartAttackAttack getThis() {
        return this;
    }

    @Override
    public @NonNull SheerHeartAttackAttack copy() {
        return copyExtras(new SheerHeartAttackAttack(getCooldown(), getWindup(), getDuration(), getMoveDistance()));
    }

    public static class Type extends AbstractMove.Type<SheerHeartAttackAttack> {
        public static final Type INSTANCE = new Type();

        @Override
        protected @NonNull App<RecordCodecBuilder.Mu<SheerHeartAttackAttack>, SheerHeartAttackAttack> buildCodec(RecordCodecBuilder.Instance<SheerHeartAttackAttack> instance) {
            return baseDefault(instance, SheerHeartAttackAttack::new);
        }
    }
}
