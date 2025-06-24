package net.arna.jcraft.common.attack.moves.mandom;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.arna.jcraft.api.attack.MoveType;
import net.arna.jcraft.api.attack.moves.AbstractMove;
import net.arna.jcraft.api.component.living.CommonCooldownsComponent;
import net.arna.jcraft.api.registry.JPacketRegistry;
import net.arna.jcraft.common.entity.stand.MandomEntity;
import net.arna.jcraft.common.marker.EntityDataHandler;
import net.arna.jcraft.common.marker.EntityMarker;
import net.arna.jcraft.common.marker.EntityMarkerType;
import net.arna.jcraft.common.marker.Extractors;
import net.arna.jcraft.common.marker.Identifiers;
import net.arna.jcraft.common.marker.Injectors;
import net.arna.jcraft.common.marker.Predicates;
import net.arna.jcraft.common.util.CooldownType;
import net.arna.jcraft.platform.JComponentPlatformUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class CountdownMove extends AbstractMove<CountdownMove, MandomEntity> {
    private static final int COUNTDOWN_COOLDOWN_TICKS = 120; // 6 seconds
    private static final Set<ResourceLocation> ENTITY_STUFF_TO_SAVE = Set.of(
            Identifiers.POSITION,
            Identifiers.YAW,
            Identifiers.YAW_HEAD,
            Identifiers.PITCH,
            Identifiers.VELOCITY,
            Identifiers.FOOD_DATA,
            Identifiers.BLOOD_GAUGE,
            Identifiers.HEALTH
    );
    static final EntityMarkerType ENTITY_MARKER_TYPE = new EntityMarkerType(
            // we catch all entities to save earlier in the code
            entity -> true,
            // but we don't know their state when loading
            (entityMarker, serverLevel) -> {
                final Entity entity = serverLevel.getEntity(entityMarker.id());
                return entity != null && entity.isAlive() && (!(entity instanceof ServerPlayer player) || (!(player.isSpectator() || player.isCreative())));
            },
            // this is all we need to check when saving/loading
            ENTITY_STUFF_TO_SAVE,
            Set.of(new EntityDataHandler(Predicates.fromSet(ENTITY_STUFF_TO_SAVE), Extractors.ALL, Injectors.ALL)));
    @Getter
    private final int radius;
    @Getter
    private final int maxCountdownTicks;
    @Getter
    private final Map<UUID, EntityMarker> timeMarkerData = new WeakHashMap<>();
    @Getter
    private final List<RewindData> rewindInfo = new ArrayList<>();
    @Getter
    @Setter
    private boolean countdownActive = false;
    @Getter
    private int countdownTicks;

    public CountdownMove(final int cooldown, final int windup, final int duration, final float moveDistance, final int radius, final int maxCountdownTicks) {
        super(cooldown, windup, duration, moveDistance);
        if (radius < 0) {
            throw new IllegalArgumentException("radius cannot be negative!");
        }
        this.radius = radius;
        if (maxCountdownTicks < 0) {
            throw new IllegalArgumentException("maxCountdownTicks cannot be negative!");
        }
        this.maxCountdownTicks = maxCountdownTicks;
    }

    @Override
    public @NotNull MoveType<CountdownMove> getMoveType() {
        return Type.INSTANCE;
    }

    @Override
    public void tick(final MandomEntity attacker) {
        super.tick(attacker);
        if (++countdownTicks > maxCountdownTicks) {
            countdownActive = false;
        }
        if (!countdownActive) {
            countdownTicks = 0;
            return;
        }
        tickCountdownInfo(attacker);
    }

    @Override
    public @NonNull Set<LivingEntity> perform(final MandomEntity attacker, final LivingEntity user) {
        final List<Entity> toCapture = attacker.level().getEntitiesOfClass(Entity.class,
                attacker.getBoundingBox().inflate(radius),
                EntitySelector.NO_CREATIVE_OR_SPECTATOR.and(e -> e != attacker));

        // Also include the user in the rewind
        if (!(user instanceof Player player && (player.isCreative() || player.isSpectator()))) {
            toCapture.add(user);
        }

        timeMarkerData.clear();
        rewindInfo.clear();

        // Follow ReturnToZeroMove pattern for saving entity data
        for (final Entity entity : toCapture) {
            if (ENTITY_MARKER_TYPE.shouldSave(entity.getUUID(), entity)) {
                timeMarkerData.put(entity.getUUID(), ENTITY_MARKER_TYPE.save(entity.getUUID(), entity));
                rewindInfo.add(new RewindData(entity.getEyePosition(), entity));
            }
        }

        countdownActive = true;
        countdownTicks = 0;

        // Put both UTILITY and ULTIMATE on cooldown for 6 seconds
        CommonCooldownsComponent cooldowns = JComponentPlatformUtils.getCooldowns(user);
        cooldowns.setCooldown(CooldownType.UTILITY, COUNTDOWN_COOLDOWN_TICKS);
        cooldowns.setCooldown(CooldownType.STAND_ULTIMATE, COUNTDOWN_COOLDOWN_TICKS);

        return Set.of();
    }


    public void tickCountdownInfo(final MandomEntity attacker) {
        if (!(attacker.getUser() instanceof ServerPlayer serverPlayer)) {
            return;
        }

        if (rewindInfo.isEmpty()) {
            return;
        }

        for (RewindData data : rewindInfo) {
            final Entity entity = data.entity();
            if (entity == null || !entity.isAlive()) {
                continue;
            }
            final Vec3 position = data.originalPos();

            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());

            buf.writeInt(entity.getId());
            buf.writeDouble(position.x());
            buf.writeDouble(position.y());
            buf.writeDouble(position.z());

            NetworkManager.sendToPlayer(serverPlayer, JPacketRegistry.S2C_MANDOM_DATA, buf);
        }
    }

    @Override
    protected @NonNull CountdownMove getThis() {
        return this;
    }

    @Override
    public @NonNull CountdownMove copy() {
        return copyExtras(new CountdownMove(getCooldown(), getWindup(), getDuration(), getMoveDistance(), getRadius(), getMaxCountdownTicks()));
    }

    public record RewindData(Vec3 originalPos, Entity entity) {
    }

    public static class Type extends AbstractMove.Type<CountdownMove> {
        public static final Type INSTANCE = new Type();

        @Override
        protected @NotNull App<RecordCodecBuilder.Mu<CountdownMove>, CountdownMove> buildCodec(RecordCodecBuilder.Instance<CountdownMove> instance) {
            return instance.group(extras(), cooldown(), windup(), duration(), moveDistance(), ExtraCodecs.NON_NEGATIVE_INT.fieldOf("radius").forGetter(CountdownMove::getRadius), ExtraCodecs.NON_NEGATIVE_INT.fieldOf("maxCountdownTicks").forGetter(CountdownMove::getMaxCountdownTicks)).apply(instance, applyExtras(CountdownMove::new));
        }
    }
}