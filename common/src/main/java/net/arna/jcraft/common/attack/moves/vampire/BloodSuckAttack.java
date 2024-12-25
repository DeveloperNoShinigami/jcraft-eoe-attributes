package net.arna.jcraft.common.attack.moves.vampire;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.arna.jcraft.common.attack.core.data.MoveType;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.core.ctx.MoveVariable;
import net.arna.jcraft.common.attack.moves.base.AbstractMove;
import net.arna.jcraft.common.attack.moves.base.AbstractSpecGrabAttack;
import net.arna.jcraft.common.spec.VampireSpec;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public final class BloodSuckAttack extends AbstractSpecGrabAttack<BloodSuckAttack, VampireSpec, VampireSpec.State> {
    public static final MoveVariable<LivingEntity> TARGET = new MoveVariable<>(LivingEntity.class);

    public BloodSuckAttack(final int cooldown, final int windup, final int duration, final float moveDistance,
                           final float damage, final int stun, final float hitboxSize, final float knockback,
                           final float offset, final AbstractMove<?, ? super VampireSpec> hitMove,
                           final int grabDuration, final double grabOffset) {
        super(cooldown, windup, duration, moveDistance, damage, stun, hitboxSize, knockback,
                offset, hitMove, VampireSpec.State.BLOODSUCK_HIT, grabDuration, grabOffset);
    }

    public BloodSuckAttack(final int cooldown, final int windup, final int duration, final float moveDistance,
                           final float damage, final int stun, final float hitboxSize, final float knockback,
                           final float offset, final AbstractMove<?, ? super VampireSpec> hitMove) {
        super(cooldown, windup, duration, moveDistance, damage, stun, hitboxSize, knockback,
                offset, hitMove, VampireSpec.State.BLOODSUCK_HIT);
    }

    @Override
    public @NotNull MoveType<BloodSuckAttack> getMoveType() {
        return Type.INSTANCE;
    }

    @Override
    public void registerExtraContextEntries(final MoveContext ctx) {
        ctx.register(TARGET, null);
    }

    @Override
    public void performHook(final VampireSpec attacker, final Set<LivingEntity> targets, final Set<AABB> boxes,
                            final DamageSource damageSource, final Vec3 forwardPos, final Vec3 rotationVector, final MoveContext ctx) {
        super.performHook(attacker, targets, boxes, damageSource, forwardPos, rotationVector, ctx);
        if (!targets.isEmpty()) {
            ctx.set(TARGET, targets.stream().findFirst().get());
        }
    }

    @Override
    protected @NonNull BloodSuckAttack getThis() {
        return this;
    }

    @Override
    public @NonNull BloodSuckAttack copy() {
        return copyExtras(new BloodSuckAttack(getCooldown(), getWindup(), getDuration(), getMoveDistance(), getDamage(), getStun(),
                getHitboxSize(), getKnockback(), getOffset(), getHitMove(), getGrabDuration(), getGrabOffset()));
    }

    public static class Type extends AbstractSpecGrabAttack.Type<BloodSuckAttack> {
        public static final Type INSTANCE = new Type();

        @Override
        protected @NotNull App<RecordCodecBuilder.Mu<BloodSuckAttack>, BloodSuckAttack> buildCodec(RecordCodecBuilder.Instance<BloodSuckAttack> instance) {
            return this.<VampireSpec>grabDefault(instance, BloodSuckAttack::new);
        }
    }
}
