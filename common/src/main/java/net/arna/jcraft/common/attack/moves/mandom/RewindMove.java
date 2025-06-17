package net.arna.jcraft.common.attack.moves.mandom;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import lombok.NonNull;
import net.arna.jcraft.api.attack.MoveType;
import net.arna.jcraft.api.attack.moves.AbstractMove;
import net.arna.jcraft.common.entity.stand.MandomEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.network.protocol.game.ClientboundRotateHeadPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class RewindMove extends AbstractMove<RewindMove, MandomEntity> {

    @Getter
    private final int reach; // in Euclidian distance in meters

    public RewindMove(final int cooldown, final int windup, final int duration, final float moveDistance, final int reach) {
        super(cooldown, windup, duration, moveDistance);
        if (reach < 0) {
            throw new IllegalArgumentException("Teleport reach cannot be negative!");
        }
        this.reach = reach;
    }

    @Override
    public @NotNull MoveType<RewindMove> getMoveType() {
        return Type.INSTANCE;
    }

    @Override
    public @NonNull Set<LivingEntity> perform(final MandomEntity attacker, final LivingEntity user) {
        CountdownMove countdownMove = findCountdownMove(attacker);
        if (countdownMove == null || !countdownMove.isCountdownActive()) {
            return Set.of();
        }

        final Map<Entity, CompoundTag> savedData = countdownMove.getTimeMarkerData();
        final Map<LivingEntity, Float> savedUserYaw = countdownMove.getUserHeadYawData();
        final Map<LivingEntity, Float> savedUserPitch = countdownMove.getUserHeadPitchData();

        if (savedData.isEmpty()) {
            return Set.of();
        }

        for (Map.Entry<Entity, CompoundTag> data : savedData.entrySet()) {
            final Entity ent = data.getKey();
            if (!ent.isAlive()) {
                continue;
            }
            final CompoundTag nbt = data.getValue();

            if (ent instanceof final ServerPlayer serverPlayer) {
                if (serverPlayer.isCreative() || serverPlayer.isSpectator()) {
                    continue;
                }
                performOnServerPlayer(serverPlayer, nbt, savedUserYaw, savedUserPitch);
            } else if (ent instanceof LivingEntity livingEntity) {
                performOnLivingEntity(livingEntity, nbt, savedUserYaw, savedUserPitch);
            } else {
                // For non-living entities, just load the NBT
                final CompoundTag modernNbt = new CompoundTag();
                ent.saveWithoutId(modernNbt);
                applyModernNBT(nbt, modernNbt, Set.of("Items", "Inventory", "HandItems", "ArmorItems"));
                ent.load(nbt);
            }
        }

        // Clean up
        savedData.clear();
        savedUserYaw.clear();
        savedUserPitch.clear();
        countdownMove.getRewindInfo().clear();
        countdownMove.setCountdownActive(false);

        return Set.of();
    }

    private void performOnServerPlayer(final ServerPlayer serverPlayer, final CompoundTag nbt, final Map<LivingEntity, Float> savedUserYaw, final Map<LivingEntity, Float> savedUserPitch) {
        // Get saved position from NBT
        ListTag posList = nbt.getList("Pos", 6);
        double x = posList.getDouble(0);
        double y = posList.getDouble(1);
        double z = posList.getDouble(2);
        if (serverPlayer.position().distanceTo(new Vec3(x, y, z)) > getReach()) {
            return;
        }

        // Get saved rotations
        Float savedYaw = savedUserYaw.get(serverPlayer);
        Float savedPitch = savedUserPitch.get(serverPlayer);

        if (savedYaw != null && savedPitch != null) {
            // Load inventory and ender chest
            nbt.put("Inventory", serverPlayer.getInventory().save(new ListTag()));
            nbt.put("EnderItems", serverPlayer.getEnderChestInventory().createTag());

            // Load the NBT data first (for inventory, etc.)
            serverPlayer.load(nbt);

            // Use teleportTo with proper rotation handling
            serverPlayer.teleportTo(serverPlayer.serverLevel(), x, y, z,
                    EnumSet.noneOf(RelativeMovement.class), savedYaw, savedPitch);

            // Force update head rotation for other players
            serverPlayer.setYHeadRot(savedYaw);
            serverPlayer.connection.send(new ClientboundRotateHeadPacket(serverPlayer, (byte)((int)(savedYaw * 256.0F / 360.0F))));
            serverPlayer.connection.send(new ClientboundTeleportEntityPacket(serverPlayer));

            // Update motion
            serverPlayer.connection.send(new ClientboundSetEntityMotionPacket(serverPlayer));
        }
    }

    private void performOnLivingEntity(final LivingEntity livingEntity, final CompoundTag nbt, final Map<LivingEntity, Float> savedUserYaw, Map<LivingEntity, Float> savedUserPitch) {
        // Get saved position from NBT
        ListTag posList = nbt.getList("Pos", 6);
        double x = posList.getDouble(0);
        double y = posList.getDouble(1);
        double z = posList.getDouble(2);
        if (livingEntity.position().distanceTo(new Vec3(x, y, z)) > getReach()) {
            return;
        }

        Float savedYaw = savedUserYaw.get(livingEntity);
        Float savedPitch = savedUserPitch.get(livingEntity);

        if (savedYaw != null && savedPitch != null) {
            // Load the NBT data first
            livingEntity.load(nbt);

            // Teleport with proper rotation
            livingEntity.teleportTo(x, y, z);

            // Set all rotation values
            livingEntity.setYRot(savedYaw);
            livingEntity.setXRot(savedPitch);
            livingEntity.setYHeadRot(savedYaw);
            livingEntity.setYBodyRot(savedYaw);

            // Set previous rotation values for smooth interpolation
            livingEntity.yRotO = savedYaw;
            livingEntity.xRotO = savedPitch;
            livingEntity.yHeadRotO = savedYaw;
            livingEntity.yBodyRotO = savedYaw;
        }
    }

    private CountdownMove findCountdownMove(MandomEntity attacker) {
        return attacker.getMoveMap().asMovesList().stream()
                .filter(move -> move instanceof CountdownMove)
                .map(CountdownMove.class::cast)
                .findFirst()
                .orElse(null);
    }

    private void applyModernNBT(final CompoundTag receiver, final CompoundTag sender, final Set<String> identifiers) {
        for (String identifier : identifiers) {
            if (sender.contains(identifier)) {
                receiver.put(identifier, Objects.requireNonNull(sender.get(identifier)));
            }
        }
    }

    @Override
    protected @NonNull RewindMove getThis() {
        return this;
    }

    @Override
    public @NonNull RewindMove copy() {
        return copyExtras(new RewindMove(getCooldown(), getWindup(), getDuration(), getMoveDistance(), 200));
    }

    public static final class Type extends AbstractMove.Type<RewindMove> {
        public static final Type INSTANCE = new Type();

        @Override
        protected @NotNull App<RecordCodecBuilder.Mu<RewindMove>, RewindMove> buildCodec(RecordCodecBuilder.Instance<RewindMove> instance) {
            return instance.group(extras(), cooldown(), windup(), duration(), moveDistance(), ExtraCodecs.NON_NEGATIVE_INT.fieldOf("reach").forGetter(RewindMove::getReach)).apply(instance, applyExtras(RewindMove::new));
        }
    }
}