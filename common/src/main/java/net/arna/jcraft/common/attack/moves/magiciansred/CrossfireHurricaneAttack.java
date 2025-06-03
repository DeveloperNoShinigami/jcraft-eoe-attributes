package net.arna.jcraft.common.attack.moves.magiciansred;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.Unpooled;
import lombok.NonNull;
import net.arna.jcraft.api.attack.MoveType;
import net.arna.jcraft.common.attack.core.ctx.IntMoveVariable;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.core.ctx.MoveVariable;
import net.arna.jcraft.common.attack.moves.base.AbstractMove;
import net.arna.jcraft.common.component.living.CommonHitPropertyComponent;
import net.arna.jcraft.common.entity.damage.JDamageSources;
import net.arna.jcraft.common.entity.stand.MagiciansRedEntity;
import net.arna.jcraft.common.entity.stand.StandEntity;
import net.arna.jcraft.common.network.s2c.ServerChannelFeedbackPacket;
import net.arna.jcraft.common.util.JUtils;
import net.arna.jcraft.registry.JStatusRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Set;

public final class CrossfireHurricaneAttack extends AbstractMove<CrossfireHurricaneAttack, MagiciansRedEntity> {
    public static final IntMoveVariable HURRICANE_TIME = new IntMoveVariable();
    public static final MoveVariable<Vec3> HURRICANE_POS = new MoveVariable<>(Vec3.class);

    public CrossfireHurricaneAttack(final int cooldown, final int windup, final int duration, final float moveDistance) {
        super(cooldown, windup, duration, moveDistance);
    }

    @Override
    public @NonNull MoveType<CrossfireHurricaneAttack> getMoveType() {
        return Type.INSTANCE;
    }

    @Override
    public void tick(final MagiciansRedEntity attacker) {
        tickHurricane(attacker);
    }

    @Override
    public @NonNull Set<LivingEntity> perform(final MagiciansRedEntity attacker, final LivingEntity user, final MoveContext ctx) {
        ctx.setInt(HURRICANE_TIME, 50);
        ctx.set(HURRICANE_POS, attacker.position());
        return Set.of();
    }

    public void tickHurricane(final MagiciansRedEntity stand) {
        // Init variables
        final MoveContext ctx = stand.getMoveContext();
        int hurricaneTime = ctx.getInt(HURRICANE_TIME);
        Vec3 hurricanePos = ctx.get(HURRICANE_POS);
        final LivingEntity user = stand.getUserOrThrow();
        final Entity vehicle = user.getVehicle();
        final Level world = stand.level();

        // Run every four ticks because the hurricane's meant to be slow, and it's convenient for CPU usage
        if (stand.tickCount % 4 != 0 || hurricaneTime <= 0) {
            return;
        }
        ctx.setInt(HURRICANE_TIME, --hurricaneTime);

        // Homing
        final List<LivingEntity> nearbyEnts = world.getEntitiesOfClass(LivingEntity.class,
                new AABB(hurricanePos.add(32.0, 32.0, 32.0), hurricanePos.subtract(32.0, 32.0, 32.0)),
                EntitySelector.NO_CREATIVE_OR_SPECTATOR.and(e -> e != vehicle && e != stand && e != user));

        if (!nearbyEnts.isEmpty()) {
            Vec3 avgPos = Vec3.ZERO;
            for (LivingEntity livingEntity : nearbyEnts) {
                avgPos = avgPos.add(livingEntity.getEyePosition());
            }
            avgPos = avgPos.scale(1.0 / nearbyEnts.size());

            ctx.set(HURRICANE_POS, hurricanePos = hurricanePos.add(avgPos.subtract(hurricanePos).normalize().scale(0.5)));
        }

        // Damage
        final List<LivingEntity> toHurt = world.getEntitiesOfClass(LivingEntity.class,
                new AABB(hurricanePos.add(2.5, 1, 2.5), hurricanePos.subtract(2.5, 1, 2.5)),
                EntitySelector.NO_CREATIVE_OR_SPECTATOR.and(e -> e != stand && e != user && e != vehicle));

        for (LivingEntity living : toHurt) {
            LivingEntity target = JUtils.getUserIfStand(living);
            if (hurricaneTime > 1) {
                StandEntity.damageLogic(world, target, new Vec3(Math.sin(stand.tickCount / 10.0) * 3, 0.0, Math.cos(stand.tickCount / 10.0) * 3),
                        10, 1, false, 0.5f, true, 5, JDamageSources.stand(stand), user, CommonHitPropertyComponent.HitAnimation.MID);
                if (hurricaneTime > 15) {
                    ctx.setInt(HURRICANE_TIME, 15); // Allows for zoning up until it hits something
                }
            } else {
                target.addEffect(new MobEffectInstance(JStatusRegistry.KNOCKDOWN.get(), 20, 0));
            }
        }

        // Particles
        final FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeShort(10);

        buf.writeDouble(hurricanePos.x);
        buf.writeDouble(hurricanePos.y);
        buf.writeDouble(hurricanePos.z);

        ServerChannelFeedbackPacket.send(JUtils.around((ServerLevel) world, hurricanePos, 128), buf);
    }

    @Override
    public void registerExtraContextEntries(final MoveContext ctx) {
        ctx.register(HURRICANE_TIME);
        ctx.register(HURRICANE_POS);
    }

    @Override
    protected @NonNull CrossfireHurricaneAttack getThis() {
        return this;
    }

    @Override
    public @NonNull CrossfireHurricaneAttack copy() {
        return copyExtras(new CrossfireHurricaneAttack(getCooldown(), getWindup(), getDuration(), getMoveDistance()));
    }

    public static class Type extends AbstractMove.Type<CrossfireHurricaneAttack> {
        public static final Type INSTANCE = new Type();

        @Override
        protected @NonNull App<RecordCodecBuilder.Mu<CrossfireHurricaneAttack>, CrossfireHurricaneAttack> buildCodec(RecordCodecBuilder.Instance<CrossfireHurricaneAttack> instance) {
            return baseDefault(instance, CrossfireHurricaneAttack::new);
        }
    }
}
