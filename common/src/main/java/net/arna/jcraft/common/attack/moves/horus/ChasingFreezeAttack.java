package net.arna.jcraft.common.attack.moves.horus;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.api.attack.MoveType;
import net.arna.jcraft.common.attack.moves.base.AbstractMove;
import net.arna.jcraft.common.entity.projectile.IceBranchProjectile;
import net.arna.jcraft.common.entity.stand.HorusEntity;
import net.minecraft.world.entity.LivingEntity;

import java.util.Set;

public class ChasingFreezeAttack extends AbstractMove<ChasingFreezeAttack, HorusEntity> {
    public ChasingFreezeAttack(int cooldown, int windup, int duration, float moveDistance) {
        super(cooldown, windup, duration, moveDistance);
        ranged = true;
    }

    @Override
    public @NonNull MoveType<ChasingFreezeAttack> getMoveType() {
        return Type.INSTANCE;
    }

    @Override
    public @NonNull Set<LivingEntity> perform(HorusEntity attacker, LivingEntity user, MoveContext ctx) {
        final IceBranchProjectile iceBranchProjectile = new IceBranchProjectile(attacker.level(), user, 0);
        iceBranchProjectile.moveTo(attacker.getX(), attacker.getY(), attacker.getZ(), -attacker.getYRot() + 180, -attacker.getXRot());
        attacker.level().addFreshEntity(iceBranchProjectile);

        return Set.of();
    }

    @Override
    protected @NonNull ChasingFreezeAttack getThis() {
        return this;
    }

    @Override
    public @NonNull ChasingFreezeAttack copy() {
        return copyExtras(new ChasingFreezeAttack(getCooldown(), getWindup(), getDuration(), getMoveDistance()));
    }

    public static class Type extends AbstractMove.Type<ChasingFreezeAttack> {
        public static final Type INSTANCE = new Type();

        @Override
        protected @NonNull App<RecordCodecBuilder.Mu<ChasingFreezeAttack>, ChasingFreezeAttack> buildCodec(RecordCodecBuilder.Instance<ChasingFreezeAttack> instance) {
            return baseDefault(instance, ChasingFreezeAttack::new);
        }
    }
}
