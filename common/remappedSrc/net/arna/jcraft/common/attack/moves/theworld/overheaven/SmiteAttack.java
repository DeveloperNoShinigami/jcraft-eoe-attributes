package net.arna.jcraft.common.attack.moves.theworld.overheaven;

import lombok.Getter;
import lombok.NonNull;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.core.ctx.MoveVariable;
import net.arna.jcraft.common.attack.moves.base.AbstractEffectInflictingAttack;
import net.arna.jcraft.common.entity.stand.TheWorldOverHeavenEntity;
import net.arna.jcraft.registry.JSoundRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import java.util.List;
import java.util.Set;

@Getter
public class SmiteAttack extends AbstractEffectInflictingAttack<SmiteAttack, TheWorldOverHeavenEntity> {
    public static final MoveVariable<Vec3> LIGHTNING_POS = new MoveVariable<>(Vec3.class);
    private static final MoveVariable<LightningBolt> BOLT = new MoveVariable<>(LightningBolt.class);
    private final boolean aerial;

    public SmiteAttack(int cooldown, int windup, int duration, float moveDistance, float damage, int stun,
                       float hitboxSize, float knockback, float offset, boolean aerial) {
        super(cooldown, windup, duration, moveDistance, damage, stun, hitboxSize, knockback, offset,
                List.of(new MobEffectInstance(MobEffects.LEVITATION, 7, 9, true, false)));
        this.aerial = ranged = aerial;
        this.withHitSpark(null);
    }

    @Override
    public void onInitiate(TheWorldOverHeavenEntity attacker) {
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

            if (eHit != null) {
                ctx.set(LIGHTNING_POS, eHit.getLocation());
            } else {
                ctx.set(LIGHTNING_POS, attacker.level().clip(new ClipContext(eP, eP.add(rangeMod),
                        ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, user)).getLocation());
            }
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
    public @NonNull Set<LivingEntity> perform(TheWorldOverHeavenEntity attacker, LivingEntity user, MoveContext ctx) {
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
    protected Set<AABB> calculateBoxes(TheWorldOverHeavenEntity attacker, LivingEntity user, Vec3 rotVec, Vec3 upVec, Vec3 hPos, Vec3 fPos) {
        return Set.of(createBox(attacker.getMoveContext().get(LIGHTNING_POS), getHitboxSize()));
    }

    @Override
    protected void processTarget(TheWorldOverHeavenEntity attacker, LivingEntity target, Vec3 kbVec, DamageSource damageSource) {
        super.processTarget(attacker, target, kbVec, damageSource);

        target.thunderHit((ServerLevel) attacker.level(), attacker.getMoveContext().get(BOLT));
    }

    @Override
    public void registerContextEntries(MoveContext ctx) {
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
                getStun(), getHitboxSize(), getKnockback(), getOffset(), isAerial()));
    }
}
