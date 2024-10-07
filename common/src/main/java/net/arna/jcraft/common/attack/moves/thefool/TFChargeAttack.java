package net.arna.jcraft.common.attack.moves.thefool;

import io.netty.buffer.Unpooled;
import lombok.NonNull;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.moves.base.AbstractChargeAttack;
import net.arna.jcraft.common.entity.stand.TheFoolEntity;
import net.arna.jcraft.common.network.s2c.ServerChannelFeedbackPacket;
import net.arna.jcraft.common.util.JUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import java.util.Set;

public final class TFChargeAttack extends AbstractChargeAttack<TFChargeAttack, TheFoolEntity, TheFoolEntity.State> {
    public TFChargeAttack(final int cooldown, final int windup, final int duration, final float moveDistance, final float damage, final int stun,
                          final float hitboxSize, final float knockback, final float offset, final TheFoolEntity.State hitAnimState) {
        super(cooldown, windup, duration, moveDistance, damage, stun, hitboxSize, knockback, offset, hitAnimState);
    }

    @Override
    public @NonNull Set<LivingEntity> perform(final TheFoolEntity attacker, final LivingEntity user, final MoveContext ctx) {
        final Set<LivingEntity> targets = super.perform(attacker, user, ctx);
        if (targets.isEmpty()) {
            return targets;
        }

        attacker.setSand(true);
        final Vec3 pos = attacker.getEyePosition();

        // Display sand effect
        final FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());

        buf.writeShort(11);
        buf.writeDouble(pos.x);
        buf.writeDouble(pos.y);
        buf.writeDouble(pos.z);
        buf.writeDouble(0.5);

        for (ServerPlayer sendPlayer : JUtils.around((ServerLevel) attacker.level(), pos, 96)) {
            ServerChannelFeedbackPacket.send(sendPlayer, buf);
        }

        return targets;
    }

    @Override
    protected @NonNull TFChargeAttack getThis() {
        return this;
    }

    @Override
    public @NonNull TFChargeAttack copy() {
        return copyExtras(new TFChargeAttack(getCooldown(), getWindup(), getDuration(), getMoveDistance(), getDamage(),
                getStun(), getHitboxSize(), getKnockback(), getOffset(), getHitAnimState()));
    }
}
