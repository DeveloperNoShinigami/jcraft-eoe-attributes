package net.arna.jcraft.common.attack.moves.theworld.overheaven;

import com.google.common.reflect.TypeToken;
import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import lombok.NonNull;
import net.arna.jcraft.common.attack.core.MoveClass;
import net.arna.jcraft.api.attack.MoveType;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.core.ctx.MoveVariable;
import net.arna.jcraft.common.attack.moves.base.AbstractSimpleAttack;
import net.arna.jcraft.common.entity.stand.TheWorldOverHeavenEntity;
import net.arna.jcraft.platform.JComponentPlatformUtils;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import java.util.ArrayList;
import java.util.List;

public final class OverwriteAttack extends AbstractSimpleAttack<OverwriteAttack, TheWorldOverHeavenEntity> {
    public static final double NO_LOOK_RANGE = 512.0;
    public static final MoveVariable<IntList> OVERWRITE_TIMES = new MoveVariable<>(IntList.class);
    public static final MoveVariable<List<LivingEntity>> OVERWRITE_TARGETS = new MoveVariable<>(new TypeToken<>() {});

    public OverwriteAttack(final int cooldown, final int windup, final int duration, final float moveDistance, final float damage, final int stun,
                           final float hitboxSize, final float knockback, final float offset) {
        super(cooldown, windup, duration, moveDistance, damage, stun, hitboxSize, knockback, offset);
    }

    @Override
    public void tick(final TheWorldOverHeavenEntity attacker) {
        if (!attacker.hasUser()) {
            return;
        }

        final IntList overwriteTimes = attacker.getMoveContext().get(OVERWRITE_TIMES);
        final List<LivingEntity> overwriteTargets = attacker.getMoveContext().get(OVERWRITE_TARGETS);
        final LivingEntity user = attacker.getUserOrThrow();

        // Mob TW:OH users normally don't swing after charging overwrite. This fixes that.
        RandomSource random = attacker.getRandom();
        if (user instanceof Mob && attacker.getState() == TheWorldOverHeavenEntity.State.CHARGE_OVERWRITE && random.nextBoolean()) {
            attacker.initMove(random.nextBoolean() ? MoveClass.SPECIAL1 : MoveClass.SPECIAL2);
        }

        final int moveStun = attacker.getMoveStun();
        if (moveStun <= 0 && attacker.getOverwriteType() != 0) {
            attacker.setOverwriteType(0);
        }

        for (int i = 0; i < overwriteTimes.size(); i++) {
            int time = overwriteTimes.getInt(i);
            overwriteTimes.set(i, time - 1);

            if (time < 1) {
                overwriteTimes.removeInt(i);
                overwriteTargets.remove(i);
                i--;
                continue;
            }

            // Inability to look at master
            final LivingEntity entity = overwriteTargets.get(i);
            final AABB box = entity
                    .getBoundingBox()
                    .expandTowards(entity.getViewVector(1.0F).scale(NO_LOOK_RANGE))
                    .inflate(1.0D);
            final EntityHitResult hitResult = ProjectileUtil.getEntityHitResult(
                    entity, entity.getEyePosition(),
                    entity.getEyePosition().add(entity.getLookAngle().scale(NO_LOOK_RANGE)),
                    box, EntitySelector.NO_CREATIVE_OR_SPECTATOR, NO_LOOK_RANGE);

            if (hitResult == null) {
                continue;
            }
            final Entity lookEntity = hitResult.getEntity();

            if (lookEntity != user && lookEntity != attacker) {
                continue;
            }
            entity.lookAt(EntityAnchorArgument.Anchor.EYES, attacker.getEyePosition().add(
                    random.nextInt() * 10,
                    random.nextInt() * 10,
                    random.nextInt() * 10));
        }
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
    public @NonNull MoveType<OverwriteAttack> getMoveType() {
        return Type.INSTANCE;
    }

    @Override
    public void registerExtraContextEntries(final MoveContext ctx) {
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

    public static class Type extends AbstractSimpleAttack.Type<OverwriteAttack> {
        public static final Type INSTANCE = new Type();

        @Override
        protected @NonNull App<RecordCodecBuilder.Mu<OverwriteAttack>, OverwriteAttack> buildCodec(RecordCodecBuilder.Instance<OverwriteAttack> instance) {
            return attackDefault(instance, OverwriteAttack::new);
        }
    }
}
