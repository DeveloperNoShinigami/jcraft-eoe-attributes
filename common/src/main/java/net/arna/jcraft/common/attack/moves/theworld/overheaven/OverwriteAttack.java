package net.arna.jcraft.common.attack.moves.theworld.overheaven;

import com.google.common.reflect.TypeToken;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import lombok.NonNull;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.core.ctx.MoveVariable;
import net.arna.jcraft.common.attack.moves.base.AbstractSimpleAttack;
import net.arna.jcraft.common.entity.stand.TheWorldOverHeavenEntity;
import net.arna.jcraft.platform.JComponentPlatformUtils;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;
import java.util.ArrayList;
import java.util.List;

public final class OverwriteAttack extends AbstractSimpleAttack<OverwriteAttack, TheWorldOverHeavenEntity> {
    public static final MoveVariable<IntList> OVERWRITE_TIMES = new MoveVariable<>(IntList.class);
    public static final MoveVariable<List<LivingEntity>> OVERWRITE_TARGETS = new MoveVariable<>(new TypeToken<>() {
    });

    public OverwriteAttack(final int cooldown, final int windup, final int duration, final float moveDistance, final float damage, final int stun,
                           final float hitboxSize, final float knockback, final float offset) {
        super(cooldown, windup, duration, moveDistance, damage, stun, hitboxSize, knockback, offset);
    }

    @Override
    protected void processTarget(final TheWorldOverHeavenEntity attacker, final LivingEntity target, final Vec3 kbVec, final DamageSource damageSource) {
        super.processTarget(attacker, target, kbVec, damageSource);

        final MoveContext ctx = attacker.getMoveContext();
        final IntList overwriteTimes = ctx.get(OVERWRITE_TIMES);
        final List<LivingEntity> overwriteTargets = ctx.get(OVERWRITE_TARGETS);

        switch (attacker.getOverwriteType()) {
            case 1 -> {
                overwriteTimes.add(200);
                overwriteTargets.add(target);
            }
            case 2 -> {
                target.setSecondsOnFire(5);
                target.addEffect(new MobEffectInstance(MobEffects.HUNGER, 100, 0, false, true));
                target.addEffect(new MobEffectInstance(MobEffects.POISON, 100, 0, false, true));
                target.addEffect(new MobEffectInstance(MobEffects.WITHER, 100, 0, false, true));
            }
            case 3 -> {
                target.heal(4f);

                if (!(target instanceof Mob)) {
                    return;
                }
                JComponentPlatformUtils.getMiscData(target).setSlavedTo(attacker.getUserOrThrow().getUUID());
                overwriteTimes.add(1048576);
                overwriteTargets.add(target);
            }
        }
    }

    @Override
    public void registerContextEntries(final MoveContext ctx) {
        ctx.register(OVERWRITE_TIMES, new IntArrayList());
        ctx.register(OVERWRITE_TARGETS, new ArrayList<>());
    }

    @Override
    protected @NonNull OverwriteAttack getThis() {
        return this;
    }

    @Override
    public @NonNull OverwriteAttack copy() {
        return copyExtras(new OverwriteAttack(getCooldown(), getWindup(), getDuration(), getMoveDistance(),
                getDamage(), getStun(), getHitboxSize(), getKnockback(), getOffset()));
    }
}
