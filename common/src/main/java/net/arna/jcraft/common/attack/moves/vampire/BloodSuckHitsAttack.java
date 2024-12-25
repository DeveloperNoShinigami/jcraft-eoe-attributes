package net.arna.jcraft.common.attack.moves.vampire;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.IntCollection;
import lombok.NonNull;
import net.arna.jcraft.common.attack.core.data.MoveType;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.moves.base.AbstractMultiHitAttack;
import net.arna.jcraft.common.spec.VampireSpec;
import net.arna.jcraft.common.util.JUtils;
import net.arna.jcraft.registry.JSoundRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;

import java.util.Set;

public class BloodSuckHitsAttack extends AbstractMultiHitAttack<BloodSuckHitsAttack, VampireSpec> {
    public BloodSuckHitsAttack(int cooldown, int duration, float moveDistance, float damage, int stun, float hitboxSize,
                               float knockback, float offset, @NonNull IntCollection hitMoments) {
        super(cooldown, duration, moveDistance, damage, stun, hitboxSize, knockback, offset, hitMoments);
    }

    @Override
    public @NonNull MoveType<BloodSuckHitsAttack> getMoveType() {
        return Type.INSTANCE;
    }

    @Override
    public @NonNull Set<LivingEntity> perform(VampireSpec attacker, LivingEntity user, MoveContext ctx) {
        Set<LivingEntity> targets = super.perform(attacker, user, ctx);
        user.heal(1);
        float bloodMult = JUtils.getBloodMult(ctx.get(BloodSuckAttack.TARGET));
        if (bloodMult <= 0) return targets;

        attacker.getVampireComponent().setBlood(attacker.getVampireComponent().getBlood() + 2 * bloodMult);
        JUtils.serverPlaySound(JSoundRegistry.VAMPIRE_SUCK.get(), (ServerLevel) user.level(), user.position(), 32);
        return targets;
    }

    @Override
    protected @NonNull BloodSuckHitsAttack getThis() {
        return this;
    }

    @Override
    public @NonNull BloodSuckHitsAttack copy() {
        return copyExtras(new BloodSuckHitsAttack(getCooldown(), getDuration(), getMoveDistance(), getDamage(), getStun(),
                getHitboxSize(), getKnockback(), getOffset(), getHitMoments()));
    }

    public static class Type extends AbstractMultiHitAttack.Type<BloodSuckHitsAttack> {
        public static final Type INSTANCE = new Type();

        @Override
        protected @NonNull App<RecordCodecBuilder.Mu<BloodSuckHitsAttack>, BloodSuckHitsAttack>
        buildCodec(RecordCodecBuilder.Instance<BloodSuckHitsAttack> instance) {
            return multiHitDefault(instance, BloodSuckHitsAttack::new);
        }
    }
}
