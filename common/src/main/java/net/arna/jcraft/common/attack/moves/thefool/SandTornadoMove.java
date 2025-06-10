package net.arna.jcraft.common.attack.moves.thefool;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.arna.jcraft.api.attack.MoveType;
import net.arna.jcraft.api.attack.moves.AbstractMove;
import net.arna.jcraft.common.entity.projectile.SandTornadoEntity;
import net.arna.jcraft.common.entity.stand.TheFoolEntity;
import net.minecraft.world.entity.LivingEntity;

import java.util.Set;

public final class SandTornadoMove extends AbstractMove<SandTornadoMove, TheFoolEntity> {
    public SandTornadoMove(final int cooldown, final int windup, final int duration, final float moveDistance) {
        super(cooldown, windup, duration, moveDistance);
        ranged = true;
    }

    @Override
    public @NonNull MoveType<SandTornadoMove> getMoveType() {
        return Type.INSTANCE;
    }

    @Override
    public @NonNull Set<LivingEntity> perform(final TheFoolEntity attacker, final LivingEntity user) {
        final SandTornadoEntity sandTornado = new SandTornadoEntity(attacker.level());
        sandTornado.setMaster(user);
        sandTornado.moveTo(attacker.getX(), attacker.getY() + 1.5, attacker.getZ(), attacker.getYRot(), attacker.getXRot());
        attacker.level().addFreshEntity(sandTornado);

        return Set.of();
    }

    @Override
    protected @NonNull SandTornadoMove getThis() {
        return this;
    }

    @Override
    public @NonNull SandTornadoMove copy() {
        return copyExtras(new SandTornadoMove(getCooldown(), getWindup(), getDuration(), getMoveDistance()));
    }

    public static class Type extends AbstractMove.Type<SandTornadoMove> {
        public static final Type INSTANCE = new Type();

        @Override
        protected @NonNull App<RecordCodecBuilder.Mu<SandTornadoMove>, SandTornadoMove> buildCodec(RecordCodecBuilder.Instance<SandTornadoMove> instance) {
            return baseDefault(instance, SandTornadoMove::new);
        }
    }
}
