package net.arna.jcraft.common.attack.moves.mandom;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.arna.jcraft.api.attack.MoveType;
import net.arna.jcraft.api.attack.moves.AbstractMove;
import net.arna.jcraft.common.entity.stand.MandomEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class RewindMove extends AbstractMove<RewindMove, MandomEntity> {

    public RewindMove(final int cooldown, final int windup, final int duration, final float moveDistance) {
        super(cooldown, windup, duration, moveDistance);
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

            // MODIFY NBT rotation data BEFORE loading for LivingEntities
            if (ent instanceof LivingEntity livingEntity) {
                Float savedYaw = savedUserYaw.get(livingEntity);
                Float savedPitch = savedUserPitch.get(livingEntity);

                if (savedYaw != null && savedPitch != null) {
                    // Update the NBT rotation data directly - these are the correct NBT keys
                    nbt.putFloat("yHeadRot", savedYaw);
                    nbt.putFloat("yHeadRotO", savedYaw);
                    nbt.putFloat("YRot", savedYaw);
                    nbt.putFloat("yRotO", savedYaw);
                    nbt.putFloat("XRot", savedPitch);
                    nbt.putFloat("xRotO", savedPitch);

                    // Also update the Rotation list (used by some entities)
                    ListTag rotationList = new ListTag();
                    rotationList.add(net.minecraft.nbt.FloatTag.valueOf(savedYaw));
                    rotationList.add(net.minecraft.nbt.FloatTag.valueOf(savedPitch));
                    nbt.put("Rotation", rotationList);
                }
            }

            if (ent instanceof final ServerPlayer serverPlayer) {
                nbt.put("Inventory", serverPlayer.getInventory().save(new ListTag()));
                nbt.put("EnderItems", serverPlayer.getEnderChestInventory().createTag());

                serverPlayer.connection.send(new ClientboundSetEntityMotionPacket(ent));
                ListTag list = nbt.getList("Pos", 6);
                serverPlayer.teleportToWithTicket(list.getDouble(0), list.getDouble(1), list.getDouble(2));
            } else {
                final CompoundTag modernNbt = new CompoundTag();
                ent.saveWithoutId(modernNbt);
                applyModernNBT(nbt, modernNbt, Set.of("Items", "Inventory", "HandItems", "ArmorItems"));
            }

            ent.load(nbt);
        }

        // Clean up
        savedData.clear();
        savedUserYaw.clear();
        savedUserPitch.clear();
        countdownMove.getRewindInfo().clear();
        countdownMove.setCountdownActive(false);

        return Set.of();
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
        return copyExtras(new RewindMove(getCooldown(), getWindup(), getDuration(), getMoveDistance()));
    }

    public static final class Type extends AbstractMove.Type<RewindMove> {
        public static final Type INSTANCE = new Type();

        @Override
        protected @NotNull App<RecordCodecBuilder.Mu<RewindMove>, RewindMove> buildCodec(RecordCodecBuilder.Instance<RewindMove> instance) {
            return baseDefault(instance, RewindMove::new);
        }
    }
}