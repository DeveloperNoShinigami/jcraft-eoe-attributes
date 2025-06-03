package net.arna.jcraft.common.attack.moves.goldexperience.requiem;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import lombok.NonNull;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.api.attack.MoveType;
import net.arna.jcraft.common.attack.moves.base.AbstractMove;
import net.arna.jcraft.common.entity.GERScorpionEntity;
import net.arna.jcraft.common.entity.stand.GEREntity;
import net.arna.jcraft.registry.JEntityTypeRegistry;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

@Getter
public final class LifeBeamAttack extends AbstractMove<LifeBeamAttack, GEREntity> {
    public LifeBeamAttack(final int cooldown, final int windup, final int duration, final float moveDistance) {
        super(cooldown, windup, duration, moveDistance);
        ranged = true;
    }

    @Override
    public @NotNull MoveType<LifeBeamAttack> getMoveType() {
        return Type.INSTANCE;
    }

    @Override
    public @NonNull Set<LivingEntity> perform(final GEREntity attacker, final LivingEntity user, final MoveContext ctx) {
        final GERScorpionEntity scorpion = new GERScorpionEntity(JEntityTypeRegistry.GER_SCORPION.get(), attacker.level());
        if (getChargeTime(attacker) >= 18) {
            scorpion.charge();
        }
        scorpion.setInitialVel(user.getLookAngle().scale(2));
        final Vec3 ePos = attacker.getEyePosition();
        scorpion.moveTo(ePos.x, ePos.y, ePos.z, -user.getYRot() - 90f, attacker.getXRot());
        scorpion.setMaster(user);
        attacker.level().addFreshEntity(scorpion);

        return Set.of();
    }

    @Override
    protected @NonNull LifeBeamAttack getThis() {
        return this;
    }

    @Override
    public @NonNull LifeBeamAttack copy() {
        return copyExtras(new LifeBeamAttack(getCooldown(), getWindup(), getDuration(), getMoveDistance()));
    }

    public static class Type extends AbstractMove.Type<LifeBeamAttack> {
        public static final Type INSTANCE = new Type();

        @Override
        protected @NotNull App<RecordCodecBuilder.Mu<LifeBeamAttack>, LifeBeamAttack> buildCodec(RecordCodecBuilder.Instance<LifeBeamAttack> instance) {
            return baseDefault(instance, LifeBeamAttack::new);
        }
    }
}
