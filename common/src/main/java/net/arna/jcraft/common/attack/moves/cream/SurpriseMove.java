package net.arna.jcraft.common.attack.moves.cream;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.arna.jcraft.api.attack.MoveType;
import net.arna.jcraft.common.entity.stand.CreamEntity;
import net.arna.jcraft.common.gravity.api.GravityChangerAPI;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Set;

public class SurpriseMove extends AbstractSurpriseMove<SurpriseMove> {
    public SurpriseMove(int cooldown, int windup, int duration, float moveDistance) {
        super(cooldown, windup, duration, moveDistance);
    }

    @Override
    public @NonNull MoveType<SurpriseMove> getMoveType() {
        return Type.INSTANCE;
    }

    @Override
    public void onInitiate(CreamEntity attacker) {
        super.onInitiate(attacker);

        LivingEntity user = attacker.getUser();
        if (user == null) {
            return;
        }

        Vec3 rotVec = user.getLookAngle();
        if (user.isShiftKeyDown()) {
            outPos = user.position().add(rotVec).toVector3f();
        } else {
            final Vec3 eyePos = user.getEyePosition();
            HitResult hitResult = attacker.level().clip(new ClipContext(eyePos, eyePos.add(rotVec.scale(16)),
                    ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, user));
            outPos = hitResult.getLocation().toVector3f();
        }
    }

    @Override
    public @NonNull Set<LivingEntity> perform(CreamEntity attacker, LivingEntity user) {
        Set<LivingEntity> targets = super.perform(attacker, user);

        outDir = GravityChangerAPI.getGravityDirection(attacker).step();
        outDir.mul(-1f);

        return targets;
    }

    @Override
    protected @NonNull SurpriseMove getThis() {
        return this;
    }

    @Override
    public @NonNull SurpriseMove copy() {
        return copyExtras(new SurpriseMove(getCooldown(), getWindup(), getDuration(), getMoveDistance()));
    }

    public static class Type extends AbstractSurpriseMove.Type<SurpriseMove> {
        public static final Type INSTANCE = new Type();

        @Override
        protected @NonNull App<RecordCodecBuilder.Mu<SurpriseMove>, SurpriseMove> buildCodec(RecordCodecBuilder.Instance<SurpriseMove> instance) {
            return baseDefault(instance, SurpriseMove::new);
        }
    }
}
