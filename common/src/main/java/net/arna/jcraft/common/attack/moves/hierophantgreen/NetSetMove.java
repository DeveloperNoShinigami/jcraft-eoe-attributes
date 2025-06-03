package net.arna.jcraft.common.attack.moves.hierophantgreen;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.arna.jcraft.api.attack.MoveType;
import net.arna.jcraft.common.attack.moves.base.AbstractMove;
import net.arna.jcraft.common.entity.projectile.HGNetEntity;
import net.arna.jcraft.common.entity.stand.HGEntity;
import net.arna.jcraft.common.gravity.api.GravityChangerAPI;
import net.arna.jcraft.common.gravity.util.Gravity;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public final class NetSetMove extends AbstractMove<NetSetMove, HGEntity> {
    public NetSetMove(final int cooldown, final int windup, final int duration, final float moveDistance) {
        super(cooldown, windup, duration, moveDistance);
    }

    @Override
    public @NotNull MoveType<NetSetMove> getMoveType() {
        return Type.INSTANCE;
    }

    @Override
    public @NonNull Set<LivingEntity> perform(final HGEntity attacker, final LivingEntity user) {
        final Direction gravity = GravityChangerAPI.getGravityDirection(attacker);

        final HGNetEntity net = new HGNetEntity(attacker.level());
        net.setSkin(attacker.getSkin());
        net.moveTo(
                attacker.getX() + gravity.getStepX(),
                attacker.getY() + gravity.getStepY(),
                attacker.getZ() + gravity.getStepZ(),
                attacker.getRandom().nextFloat() * 360f,
                attacker.getRandom().nextFloat() * 360f);
        net.setMaster(user);

        attacker.level().addFreshEntity(net);

        GravityChangerAPI.addGravity(net,
                new Gravity(gravity, 0, 32767, "_spawn")
        );

        return Set.of();
    }

    @Override
    protected @NonNull NetSetMove getThis() {
        return this;
    }

    @Override
    public @NonNull NetSetMove copy() {
        return copyExtras(new NetSetMove(getCooldown(), getWindup(), getDuration(), getMoveDistance()));
    }

    public static class Type extends AbstractMove.Type<NetSetMove> {
        public static final Type INSTANCE = new Type();

        @Override
        protected @NotNull App<RecordCodecBuilder.Mu<NetSetMove>, NetSetMove> buildCodec(RecordCodecBuilder.Instance<NetSetMove> instance) {
            return baseDefault(instance, NetSetMove::new);
        }
    }
}
