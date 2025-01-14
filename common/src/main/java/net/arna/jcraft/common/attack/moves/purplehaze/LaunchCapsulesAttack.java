package net.arna.jcraft.common.attack.moves.purplehaze;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.arna.jcraft.common.attack.core.data.MoveType;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.moves.base.AbstractMove;
import net.arna.jcraft.common.entity.stand.AbstractPurpleHazeEntity;
import net.arna.jcraft.common.gravity.api.GravityChangerAPI;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;

import java.util.Set;

public class LaunchCapsulesAttack extends AbstractMove<LaunchCapsulesAttack, AbstractPurpleHazeEntity<?, ?>> {
    public LaunchCapsulesAttack(int cooldown, int windup, int duration, float moveDistance) {
        super(cooldown, windup, duration, moveDistance);
        ranged = true;
    }

    @Override
    public @NonNull MoveType<LaunchCapsulesAttack> getMoveType() {
        return Type.INSTANCE;
    }

    @Override
    public @NonNull Set<LivingEntity> perform(AbstractPurpleHazeEntity<?, ?> attacker, LivingEntity user, MoveContext ctx) {
        final LivingEntity shooter = (attacker.isRemote() && !attacker.remoteControllable()) ? attacker : user;
        final Direction gravity = GravityChangerAPI.getGravityDirection(shooter);
        for (int i = 0; i < 3; i++) {
            LaunchCapsuleAttack.launchCapsule(attacker, shooter, gravity, 0.4F, shooter.getYRot() - 45F + i * 45F);
        }

        return Set.of();
    }

    @Override
    protected @NonNull LaunchCapsulesAttack getThis() {
        return this;
    }

    @Override
    public @NonNull LaunchCapsulesAttack copy() {
        return copyExtras(new LaunchCapsulesAttack(getCooldown(), getWindup(), getDuration(), getMoveDistance()));
    }

    public static class Type extends AbstractMove.Type<LaunchCapsulesAttack> {
        public static final Type INSTANCE = new Type();

        @Override
        protected @NonNull App<RecordCodecBuilder.Mu<LaunchCapsulesAttack>, LaunchCapsulesAttack> buildCodec(RecordCodecBuilder.Instance<LaunchCapsulesAttack> instance) {
            return baseDefault(instance, LaunchCapsulesAttack::new);
        }
    }
}
