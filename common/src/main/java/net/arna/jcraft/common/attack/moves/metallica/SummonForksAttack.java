package net.arna.jcraft.common.attack.moves.metallica;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.core.data.MoveType;
import net.arna.jcraft.common.attack.moves.base.AbstractMove;
import net.arna.jcraft.common.entity.projectile.MetallicaForksEntity;
import net.arna.jcraft.common.entity.stand.MetallicaEntity;
import net.arna.jcraft.common.gravity.api.GravityChangerAPI;
import net.arna.jcraft.common.util.CooldownType;
import net.arna.jcraft.common.util.JUtils;
import net.arna.jcraft.platform.JComponentPlatformUtils;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Set;

public class SummonForksAttack extends AbstractMove<SummonForksAttack, MetallicaEntity> {
    public SummonForksAttack(int cooldown, int windup, int duration) {
        super(cooldown, windup, duration, 0);
    }

    @Override
    public @NonNull MoveType<SummonForksAttack> getMoveType() {
        return Type.INSTANCE;
    }

    @Override
    public @NonNull Set<LivingEntity> perform(MetallicaEntity attacker, LivingEntity user, MoveContext ctx) {
        final Vec3 eyePos = user.position().add(GravityChangerAPI.getEyeOffset(user));
        final Vec3 rotVec = user.getLookAngle();
        final HitResult hitResult = JUtils.raycastAll(user, eyePos, eyePos.add(rotVec.scale(12.0)), ClipContext.Fluid.NONE, EntitySelector.LIVING_ENTITY_STILL_ALIVE);
        // JCraft.createParticle((ServerLevel) user.level(), hitResult.getLocation().x, hitResult.getLocation().y, hitResult.getLocation().z, JParticleType.STUN_PIERCE);
        if (hitResult.getType() == HitResult.Type.MISS) return Set.of();

        final MetallicaForksEntity forks = MetallicaForksEntity.fromMetallica(attacker);
        if (forks == null) return Set.of();

        final Vec3 hitPos = hitResult.getLocation();
        forks.moveTo(hitPos.x, hitPos.y, hitPos.z, user.getYRot(), user.getXRot());
        forks.setOnGround(hitResult.getType() == HitResult.Type.BLOCK);
        GravityChangerAPI.setDefaultGravityDirection(forks, GravityChangerAPI.getGravityDirection(user));
        attacker.level().addFreshEntity(forks);
        JComponentPlatformUtils.getCooldowns(user).setCooldown(CooldownType.SPECIAL2, 200);

        return Set.of();
    }

    @Override
    protected @NonNull SummonForksAttack getThis() {
        return this;
    }

    @Override
    public @NonNull SummonForksAttack copy() {
        return copyExtras(new SummonForksAttack(getCooldown(), getWindup(), getDuration()));
    }

    public static class Type extends AbstractMove.Type<SummonForksAttack> {
        public static final Type INSTANCE = new Type();

        @Override
        protected @NonNull App<RecordCodecBuilder.Mu<SummonForksAttack>, SummonForksAttack> buildCodec(RecordCodecBuilder.Instance<SummonForksAttack> instance) {
            return instance.group(extras(), cooldown(), windup(), duration()).apply(instance, applyExtras(SummonForksAttack::new));
        }
    }
}
