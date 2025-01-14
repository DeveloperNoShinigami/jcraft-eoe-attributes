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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.Set;

public class PreciseTossAtack extends AbstractMove<PreciseTossAtack, MetallicaEntity> {
    public PreciseTossAtack(int cooldown, int windup, int duration, float moveDistance) {
        super(cooldown, windup, duration, moveDistance);
        ranged = true;
    }

    @Override
    public @NonNull MoveType<PreciseTossAtack> getMoveType() {
        return Type.INSTANCE;
    }

    @Override
    public @NonNull Set<LivingEntity> perform(MetallicaEntity attacker, LivingEntity user, MoveContext ctx) {
        Vec3 pos = attacker.position();
        Vec3 upVec = GravityChangerAPI.getEyeOffset(user);
        for (int i = 1; i < 4; i++) {
            ScalpelProjectile scalpel = ScalpelProjectile.fromMetallica(attacker);
            if (scalpel == null) continue;
            scalpel.setPos(pos.add(upVec.scale(0.25 * i)));
            scalpel.shootFromRotation(user, user.getXRot(), user.getYRot(), 0.0F, 1.25F, 0.0F);
            attacker.level().addFreshEntity(scalpel);
        }

        return Set.of();
    }

    @Override
    protected @NonNull PreciseTossAtack getThis() {
        return this;
    }

    @Override
    public @NonNull PreciseTossAtack copy() {
        return copyExtras(new PreciseTossAtack(getCooldown(), getWindup(), getDuration(), getMoveDistance()));
    }

    public static class Type extends AbstractMove.Type<PreciseTossAtack> {
        public static final Type INSTANCE = new Type();

        @Override
        protected @NonNull App<RecordCodecBuilder.Mu<PreciseTossAtack>, PreciseTossAtack> buildCodec(RecordCodecBuilder.Instance<PreciseTossAtack> instance) {
            return baseDefault(instance, PreciseTossAtack::new);
        }
    }
}
