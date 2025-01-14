package net.arna.jcraft.common.attack.moves.whitesnake;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.arna.jcraft.common.attack.core.data.MoveType;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.moves.base.AbstractSimpleAttack;
import net.arna.jcraft.common.entity.projectile.WSAcidProjectile;
import net.arna.jcraft.common.entity.stand.WhiteSnakeEntity;
import net.arna.jcraft.common.gravity.api.GravityChangerAPI;
import net.arna.jcraft.common.gravity.util.RotationUtil;
import net.arna.jcraft.common.util.JUtils;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec2;
import java.util.Set;

public final class ChargedSpewAttack extends AbstractSimpleAttack<ChargedSpewAttack, WhiteSnakeEntity> {
    public ChargedSpewAttack(final int cooldown, final int windup, final int duration, final float moveDistance, final float damage, final int stun,
                             final float hitboxSize, final float knockback, final float offset) {
        super(cooldown, windup, duration, moveDistance, damage, stun, hitboxSize, knockback, offset);
        this.ranged = true;
    }

    @Override
    public @NonNull MoveType<ChargedSpewAttack> getMoveType() {
        return Type.INSTANCE;
    }

    @Override
    public @NonNull Set<LivingEntity> perform(final WhiteSnakeEntity attacker, final LivingEntity user, final MoveContext ctx) {
        final Set<LivingEntity> targets = super.perform(attacker, user, ctx);

        final Direction gravity = GravityChangerAPI.getGravityDirection(user);
        for (int i = 0; i < 5; i++) {
            final WSAcidProjectile acidProjectile = new WSAcidProjectile(attacker.level(), user);

            final Vec2 corrected = RotationUtil.rotPlayerToWorld(user.getYRot() - 75F + i * 37.5F, user.getXRot(), gravity);
            JUtils.shoot(acidProjectile, user, corrected.y, corrected.x, 0, 0.66F, 0);

            acidProjectile.setPos(attacker.getEyePosition());
            attacker.level().addFreshEntity(acidProjectile);
        }

        return targets;
    }

    @Override
    protected @NonNull ChargedSpewAttack getThis() {
        return this;
    }

    @Override
    public @NonNull ChargedSpewAttack copy() {
        return copyExtras(new ChargedSpewAttack(getCooldown(), getWindup(), getDuration(), getMoveDistance(), getDamage(), getStun(),
                getHitboxSize(), getKnockback(), getOffset()));
    }

    public static class Type extends AbstractSimpleAttack.Type<ChargedSpewAttack> {
        public static final Type INSTANCE = new Type();

        @Override
        protected @NonNull App<RecordCodecBuilder.Mu<ChargedSpewAttack>, ChargedSpewAttack> buildCodec(RecordCodecBuilder.Instance<ChargedSpewAttack> instance) {
            return attackDefault(instance, ChargedSpewAttack::new);
        }
    }
}
