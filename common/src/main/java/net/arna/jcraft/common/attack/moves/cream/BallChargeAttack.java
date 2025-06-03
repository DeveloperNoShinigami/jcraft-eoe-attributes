package net.arna.jcraft.common.attack.moves.cream;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import lombok.NonNull;
import net.arna.jcraft.api.attack.MoveType;
import net.arna.jcraft.common.attack.moves.base.AbstractMove;
import net.arna.jcraft.common.entity.stand.CreamEntity;
import net.arna.jcraft.common.gravity.api.GravityChangerAPI;
import net.arna.jcraft.registry.JSoundRegistry;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

@Getter
public final class BallChargeAttack extends AbstractMove<BallChargeAttack, CreamEntity> {
    private final boolean downward;

    public BallChargeAttack(final int cooldown, final int windup, final int duration, final float moveDistance, boolean downward) {
        super(cooldown, windup, duration, moveDistance);
        this.downward = downward;
    }

    @Override
    public @NotNull MoveType<BallChargeAttack> getMoveType() {
        return Type.INSTANCE;
    }

    @Override
    public @NonNull Set<LivingEntity> perform(final CreamEntity attacker, final LivingEntity user) {
        attacker.playSound(JSoundRegistry.CREAM_CHARGE.get(), 1, 1);
        attacker.setCharging(true);
        attacker.setChargeDir(user.getLookAngle().scale(0.5));
        attacker.setVoidTime(15);

        return Set.of();
    }

    @Override
    public void activeTick(CreamEntity attacker, int moveStun) {
        super.activeTick(attacker, moveStun);

        // If we're on the server side, voiding and charging and the user and charge direction are not null
        // and the attacker is not free (not doing surprise move), we will update the charge direction.
        LivingEntity user = attacker.getUser();
        if (downward && !attacker.level().isClientSide() && attacker.getVoidTime() > 0 && user != null &&
                attacker.isCharging() && attacker.getChargeDir() != null && !attacker.isFree()) {
            attacker.setChargeDir(attacker.getChargeDir().add(
                    new Vec3(GravityChangerAPI.getGravityDirection(user).step()).scale(0.1)
            ).normalize().scale(0.5));
        }
    }

    @Override
    protected @NonNull BallChargeAttack getThis() {
        return this;
    }

    @Override
    public @NonNull BallChargeAttack copy() {
        return copyExtras(new BallChargeAttack(getCooldown(), getWindup(), getDuration(), getMoveDistance(), isDownward()));
    }

    public static class Type extends AbstractMove.Type<BallChargeAttack> {
        public static final Type INSTANCE = new Type();

        @Override
        protected @NotNull App<RecordCodecBuilder.Mu<BallChargeAttack>, BallChargeAttack> buildCodec(RecordCodecBuilder.Instance<BallChargeAttack> instance) {
            return baseDefault(instance)
                    .and(Codec.BOOL.fieldOf("downward").forGetter(BallChargeAttack::isDownward))
                    .apply(instance, applyExtras(BallChargeAttack::new));
        }
    }
}
