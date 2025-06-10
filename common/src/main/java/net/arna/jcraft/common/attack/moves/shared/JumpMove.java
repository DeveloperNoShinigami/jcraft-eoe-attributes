package net.arna.jcraft.common.attack.moves.shared;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import lombok.NonNull;
import net.arna.jcraft.api.attack.IAttacker;
import net.arna.jcraft.api.attack.MoveType;
import net.arna.jcraft.api.attack.moves.AbstractMove;
import net.arna.jcraft.common.gravity.api.GravityChangerAPI;
import net.arna.jcraft.common.util.JUtils;
import net.arna.jcraft.api.attack.enums.MobilityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import java.util.Set;

@Getter
public final class JumpMove<A extends IAttacker<? extends A, ?>> extends AbstractMove<JumpMove<A>, A> {
    private final float strength;

    public JumpMove(final int cooldown, final int windup, final int duration, final float moveDistance, final float strength) {
        super(cooldown, windup, duration, moveDistance);
        this.strength = strength;
        mobilityType = MobilityType.DASH;
    }

    @Override
    public @NonNull MoveType<JumpMove<A>> getMoveType() {
        return Type.INSTANCE.cast();
    }

    @Override
    public boolean conditionsMet(final A attacker) {
        return super.conditionsMet(attacker) && attacker.getUser().onGround();
    }

    @Override
    public @NonNull Set<LivingEntity> perform(final A attacker, final LivingEntity user) {
        if (user.onGround()) {
            final Vec3 upVel = Vec3.atLowerCornerOf(GravityChangerAPI.getGravityDirection(user).getNormal()).scale(-0.5);
            final Vec3 jumpVel = Vec3.directionFromRotation(user.getXRot(), user.getYRot()).scale(strength).add(upVel);
            JUtils.setVelocity(user, jumpVel.x, jumpVel.y, jumpVel.z);
        }

        return Set.of();
    }

    @Override
    protected @NonNull JumpMove<A> getThis() {
        return this;
    }

    @Override
    public @NonNull JumpMove<A> copy() {
        return copyExtras(new JumpMove<>(getCooldown(), getWindup(), getDuration(), getMoveDistance(), strength));
    }

    public static class Type extends AbstractMove.Type<JumpMove<?>> {
        public static final Type INSTANCE = new Type();

        @Override
        protected @NonNull App<RecordCodecBuilder.Mu<JumpMove<?>>, JumpMove<?>> buildCodec(RecordCodecBuilder.Instance<JumpMove<?>> instance) {
            return baseDefault(instance).and(Codec.FLOAT.fieldOf("strength").forGetter(JumpMove::getStrength))
                    .apply(instance, applyExtras(JumpMove::new));
        }
    }
}
