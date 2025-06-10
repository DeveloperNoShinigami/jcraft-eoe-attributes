package net.arna.jcraft.common.attack.moves.anubis;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.arna.jcraft.api.attack.MoveType;
import net.arna.jcraft.api.attack.moves.AbstractUppercutAttack;
import net.arna.jcraft.common.spec.AnubisSpec;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.Set;

public class LowKickAttack extends AbstractUppercutAttack<LowKickAttack, AnubisSpec> {
    public LowKickAttack(int cooldown, int windup, int duration, float moveDistance, float damage, int stun,
                         float hitboxSize, float knockback, float offset, float strength) {
        super(cooldown, windup, duration, moveDistance, damage, stun, hitboxSize, knockback, offset, strength);
    }

    @Override
    public @NonNull MoveType<LowKickAttack> getMoveType() {
        return Type.INSTANCE;
    }

    @Override
    public @NonNull Set<LivingEntity> perform(AnubisSpec attacker, LivingEntity user) {
        Set<LivingEntity> targets = super.perform(attacker, user);

        if (!targets.isEmpty() && user instanceof Player) attacker.setTicksSinceLastHit(0);

        return targets;
    }

    @Override
    protected @NonNull LowKickAttack getThis() {
        return this;
    }

    @Override
    public @NonNull LowKickAttack copy() {
        return copyExtras(new LowKickAttack(getCooldown(), getWindup(), getDuration(), getMoveDistance(), getDamage(),
                getStun(), getHitboxSize(), getKnockback(), getOffset(), getStrength()));
    }

    public static class Type extends AbstractUppercutAttack.Type<LowKickAttack> {
        public static final Type INSTANCE = new Type();

        @Override
        protected @NonNull App<RecordCodecBuilder.Mu<LowKickAttack>, LowKickAttack> buildCodec(RecordCodecBuilder.Instance<LowKickAttack> instance) {
            return uppercutDefault(instance, LowKickAttack::new);
        }
    }
}
