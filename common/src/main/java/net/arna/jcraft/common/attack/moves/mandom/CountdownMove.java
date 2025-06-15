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
import net.arna.jcraft.common.util.CooldownType;
import net.arna.jcraft.platform.JComponentPlatformUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class CountdownMove extends AbstractMove<CountdownMove, MandomEntity> {
    private static final int COUNTDOWN_COOLDOWN_TICKS = 120; // 6 seconds
    @Getter
    private final Map<Entity, CompoundTag> timeMarkerData = new WeakHashMap<>();
    // Store the USER's head rotation, not the stand's rotation
    @Getter
    private final Map<LivingEntity, Float> userHeadYawData = new WeakHashMap<>();
    @Getter
    private final Map<LivingEntity, Float> userHeadPitchData = new WeakHashMap<>();
    @Getter
    private final List<RewindData> rewindInfo = new ArrayList<>();
    @Getter
    @Setter
    private boolean countdownActive = false;

    public CountdownMove(final int cooldown, final int windup, final int duration, final float moveDistance) {
        super(cooldown, windup, duration, moveDistance);
    }

    @Override
    public @NotNull MoveType<CountdownMove> getMoveType() {
        return Type.INSTANCE;
    }

    @Override
    public void tick(final MandomEntity attacker) {
        super.tick(attacker);

        if (!countdownActive) {
            return;
        }

        tickCountdownInfo(attacker);
    }

    @Override
    public @NonNull Set<LivingEntity> perform(final MandomEntity attacker, final LivingEntity user) {
        final List<Entity> toCapture = attacker.level().getEntitiesOfClass(Entity.class,
                attacker.getBoundingBox().inflate(64),
                EntitySelector.NO_CREATIVE_OR_SPECTATOR.and(e -> e != attacker));

        // Also include the user in the rewind
        toCapture.add(user);

        timeMarkerData.clear();
        userHeadYawData.clear();
        userHeadPitchData.clear();
        rewindInfo.clear();

        // Follow ReturnToZeroMove pattern for saving entity data
        for (Entity e : toCapture) {
            final CompoundTag data = new CompoundTag();
            e.saveWithoutId(data);
            timeMarkerData.put(e, data);

            // Save USER rotation data separately - this is the key fix
            if (e instanceof LivingEntity livingEntity) {
                userHeadYawData.put(livingEntity, livingEntity.getYHeadRot());
                userHeadPitchData.put(livingEntity, livingEntity.getXRot());
            }

            rewindInfo.add(new RewindData(e.getEyePosition(), e));
        }

        countdownActive = true;

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
        return copyExtras(new CountdownMove(getCooldown(), getWindup(), getDuration(), getMoveDistance()));
    }

    public record RewindData(Vec3 originalPos, Entity entity) {
    }

    public static class Type extends AbstractMove.Type<CountdownMove> {
        public static final Type INSTANCE = new Type();

        @Override
        protected @NotNull App<RecordCodecBuilder.Mu<CountdownMove>, CountdownMove> buildCodec(RecordCodecBuilder.Instance<CountdownMove> instance) {
            return baseDefault(instance, CountdownMove::new);
        }
    }
}