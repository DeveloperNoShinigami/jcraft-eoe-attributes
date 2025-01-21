    package net.arna.jcraft.common.attack.moves.metallica;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.core.data.MoveType;
import net.arna.jcraft.common.attack.moves.base.AbstractMove;
import net.arna.jcraft.common.entity.projectile.ScalpelProjectile;
import net.arna.jcraft.common.entity.stand.MetallicaEntity;
import net.arna.jcraft.common.gravity.api.GravityChangerAPI;
import net.arna.jcraft.common.util.JUtils;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Set;

public class RemoteScalpelMove extends AbstractMove<RemoteScalpelMove, MetallicaEntity> {
    public RemoteScalpelMove(int cooldown, int windup, int duration, float moveDistance) {
        super(cooldown, windup, duration, moveDistance);
        ranged = true;
    }

    @Override
    public @NonNull MoveType<RemoteScalpelMove> getMoveType() {
        return Type.INSTANCE;
    }

    @Override
    public @NonNull Set<LivingEntity> perform(MetallicaEntity attacker, LivingEntity user, MoveContext ctx) {
        final Vec3 eyePos = user.position().add(GravityChangerAPI.getEyeOffset(user));
        final Vec3 rotVec = user.getLookAngle();
        final HitResult hitResult = JUtils.raycastAll(user, eyePos, eyePos.add(rotVec.scale(12.0)), ClipContext.Fluid.NONE, EntitySelector.LIVING_ENTITY_STILL_ALIVE);
        if (hitResult.getType() == HitResult.Type.MISS) return Set.of();

        final Vec3 hitPos = hitResult.getLocation();
        final Vec3 upVec = GravityChangerAPI.getEyeOffset(user);

        for (int i = 1; i < 4; i++) {
            final ScalpelProjectile scalpel = ScalpelProjectile.fromMetallica(attacker);
            if (scalpel == null) continue;
            scalpel.setTempNoGrav();
            scalpel.setPos(hitPos.add(upVec.scale(0.25 * i)));
            scalpel.setDeltaMovement(upVec.scale(0.25));
            attacker.level().addFreshEntity(scalpel);
        }

        return Set.of();
    }

    @Override
    protected @NonNull RemoteScalpelMove getThis() {
        return this;
    }

    @Override
    public @NonNull RemoteScalpelMove copy() {
        return copyExtras(new RemoteScalpelMove(getCooldown(), getWindup(), getDuration(), getMoveDistance()));
    }

    public static class Type extends AbstractMove.Type<RemoteScalpelMove> {
        public static final Type INSTANCE = new Type();

        @Override
        protected @NonNull App<RecordCodecBuilder.Mu<RemoteScalpelMove>, RemoteScalpelMove> buildCodec(RecordCodecBuilder.Instance<RemoteScalpelMove> instance) {
            return baseDefault(instance, RemoteScalpelMove::new);
        }
    }
}
