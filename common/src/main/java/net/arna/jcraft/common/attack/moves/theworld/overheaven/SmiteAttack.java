package net.arna.jcraft.common.attack.moves.theworld.overheaven;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import lombok.NonNull;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.core.ctx.MoveVariable;
import net.arna.jcraft.api.attack.MoveType;
import net.arna.jcraft.common.attack.moves.base.AbstractSimpleAttack;
import net.arna.jcraft.common.entity.stand.TheWorldOverHeavenEntity;
import net.arna.jcraft.registry.JSoundRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Objects;
import java.util.Set;

@Getter
public final class SmiteAttack extends AbstractSimpleAttack<SmiteAttack, TheWorldOverHeavenEntity> {
    public static final MoveVariable<Vec3> LIGHTNING_POS = new MoveVariable<>(Vec3.class);
    private static final MoveVariable<LightningBolt> BOLT = new MoveVariable<>(LightningBolt.class);
    private final boolean aerial;
    private final int levitationDuration, levitationAmplifier;

    public SmiteAttack(final int cooldown, final int windup, final int duration, final float moveDistance,
                       final float damage, final int stun, final float hitboxSize, final float knockback,
                       final float offset, final boolean aerial, final int levitationDuration,
                       final int levitationAmplifier) {
        super(cooldown, windup, duration, moveDistance, damage, stun, hitboxSize, knockback, offset);
        this.aerial = ranged = aerial;
        this.levitationDuration = levitationDuration;
        this.levitationAmplifier = levitationAmplifier;
        this.withHitSpark(null);
    }

    @Override
    public @NonNull MoveType<SmiteAttack> getMoveType() {
        return Type.INSTANCE;
    }

    @Override
    public void onInitiate(final TheWorldOverHeavenEntity attacker) {
        super.onInitiate(attacker);

        LivingEntity user = attacker.getUserOrThrow();
        MoveContext ctx = attacker.getMoveContext();
        if (!aerial) {
            ctx.set(LIGHTNING_POS, user.position());
        } else {
            Vec3 eP = user.getEyePosition();
            Vec3 rangeMod = user.getLookAngle().scale(24);
            EntityHitResult eHit = ProjectileUtil.getEntityHitResult(user, eP, eP.add(rangeMod),
                    user.getBoundingBox().inflate(24),
                    EntitySelector.NO_CREATIVE_OR_SPECTATOR,
                    576 // Squared
            );

            ctx.set(LIGHTNING_POS,
                    Objects.requireNonNullElseGet(
                            eHit,
                            () -> attacker.level().clip(new ClipContext(eP, eP.add(rangeMod), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, user))
                    ).getLocation()
            );
        }

        Vec3 lightningPos = ctx.get(LIGHTNING_POS);
        AreaEffectCloud effectCloud = new AreaEffectCloud(attacker.level(),
                lightningPos.x, lightningPos.y, lightningPos.z);
        effectCloud.setOwner(user);
        effectCloud.setRadius(getDamage() / 2f);
        effectCloud.setWaitTime(10);
        effectCloud.setRadiusPerTick(-0.5f);

        attacker.level().addFreshEntity(effectCloud);

        attacker.level().playSound(null, lightningPos.x, lightningPos.y, lightningPos.z,
                JSoundRegistry.TWOH_CHARGE.get(), SoundSource.PLAYERS, 1, 1);
    }

    @Override
    public @NonNull Set<LivingEntity> perform(final TheWorldOverHeavenEntity attacker, final LivingEntity user, final MoveContext ctx) {
        Vec3 lP = ctx.get(LIGHTNING_POS);

        LightningBolt bolt = new LightningBolt(EntityType.LIGHTNING_BOLT, attacker.level());
        bolt.setVisualOnly(true);
        bolt.setPos(lP);
        ctx.set(BOLT, bolt);

        Set<LivingEntity> targets = super.perform(attacker, user, ctx);

        attacker.level().addFreshEntity(bolt);
        return targets;
    }

    @Override
    protected Set<AABB> calculateBoxes(final TheWorldOverHeavenEntity attacker, final LivingEntity user, final Vec3 rotVec, final Vec3 upVec, final Vec3 hPos, final Vec3 fPos) {
        return Set.of(createBox(attacker.getMoveContext().get(LIGHTNING_POS), getHitboxSize()));
    }

    @Override
    protected void processTarget(final TheWorldOverHeavenEntity attacker, final LivingEntity target, final Vec3 kbVec, final DamageSource damageSource) {
        super.processTarget(attacker, target, kbVec, damageSource);

        target.thunderHit((ServerLevel) attacker.level(), attacker.getMoveContext().get(BOLT));
        target.addEffect(new MobEffectInstance(MobEffects.LEVITATION, levitationDuration, levitationAmplifier, true, false));
    }

    @Override
    public void registerExtraContextEntries(final MoveContext ctx) {
        ctx.register(LIGHTNING_POS);
        ctx.register(BOLT);
    }

    @Override
    protected @NonNull SmiteAttack getThis() {
        return this;
    }

    @Override
    public @NonNull SmiteAttack copy() {
        return copyExtras(new SmiteAttack(getCooldown(), getWindup(), getDuration(), getMoveDistance(), getDamage(),
                getStun(), getHitboxSize(), getKnockback(), getOffset(), isAerial(), levitationDuration, levitationAmplifier));
    }

    public static class Type extends AbstractSimpleAttack.Type<SmiteAttack> {
        public static final Type INSTANCE = new Type();

        @Override
        protected @NonNull App<RecordCodecBuilder.Mu<SmiteAttack>, SmiteAttack> buildCodec(RecordCodecBuilder.Instance<SmiteAttack> instance) {
            return instance.group(extras(), attackExtras(), cooldown(), windup(), duration(), moveDistance(), damage(),
                    stun(), hitboxSize(), knockback(), offset(), Codec.BOOL.fieldOf("aerial").forGetter(SmiteAttack::isAerial),
                            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("levitation_duration").forGetter(SmiteAttack::getLevitationDuration),
                            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("levitation_amplifier").forGetter(SmiteAttack::getLevitationAmplifier))
                    .apply(instance, applyAttackExtras(SmiteAttack::new));
        }
    }
}
