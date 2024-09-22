package net.arna.jcraft.common.attack.moves.goldexperience.requiem;

import com.google.common.reflect.TypeToken;
import io.netty.buffer.Unpooled;
import lombok.Data;
import lombok.NonNull;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.core.ctx.MoveVariable;
import net.arna.jcraft.common.attack.moves.base.AbstractMove;
import net.arna.jcraft.common.entity.stand.GEREntity;
import net.arna.jcraft.common.network.s2c.ServerChannelFeedbackPacket;
import net.arna.jcraft.registry.JSoundRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.*;

public final class ReturnToZeroMove extends AbstractMove<ReturnToZeroMove, GEREntity> {
    public static final MoveVariable<Map<Entity, CompoundTag>> ENTITY_DATA = new MoveVariable<>(new TypeToken<>() {
    });
    public static final MoveVariable<List<ReturnData>> RETURN_INFO = new MoveVariable<>(new TypeToken<>() {
    });

    public ReturnToZeroMove(int cooldown, int windup, int duration, float moveDistance) {
        super(cooldown, windup, duration, moveDistance);
    }

    @Override
    public @NonNull Set<LivingEntity> perform(GEREntity attacker, LivingEntity user, MoveContext ctx) {
        final List<Entity> toReturn = attacker.level().getEntitiesOfClass(Entity.class, attacker.getBoundingBox().inflate(64),
                EntitySelector.NO_CREATIVE_OR_SPECTATOR.and(e -> e != attacker && e != user));

        final Map<Entity, CompoundTag> entityData = ctx.get(ENTITY_DATA);
        final List<ReturnData> returnInfo = ctx.get(RETURN_INFO);

        for (Entity e : toReturn) {
            final CompoundTag data = new CompoundTag();
            e.saveWithoutId(data);
            entityData.put(e, data);
            returnInfo.add(new ReturnData(e.getEyePosition(), e));
        }

        return Set.of();
    }

    public void returnToZero(GEREntity attacker) {
        final MoveContext ctx = attacker.getMoveContext();
        final Map<Entity, CompoundTag> entityData = ctx.get(ENTITY_DATA);
        final List<ReturnData> returnInfo = ctx.get(RETURN_INFO);

        for (Map.Entry<Entity, CompoundTag> data : entityData.entrySet()) {
            final Entity ent = data.getKey();
            if (!ent.isAlive()) {
                continue;
            }
            final CompoundTag nbt = data.getValue();

            if (ent instanceof final ServerPlayer serverPlayer) {
                // Backs up the player items before restoring other parts of their NBT
                nbt.put("Inventory", serverPlayer.getInventory().save(new ListTag()));
                nbt.put("EnderItems", serverPlayer.getEnderChestInventory().createTag());

                serverPlayer.connection.send(new ClientboundSetEntityMotionPacket(ent));
                ListTag list = nbt.getList("Pos", 6);
                serverPlayer.teleportToWithTicket(list.getDouble(0), list.getDouble(1), list.getDouble(2));
            }

            ent.load(nbt);
        }

        entityData.clear();
        returnInfo.clear();

        attacker.playSound(JSoundRegistry.GER_RTZ.get(), 1, 1);
    }

    public void tickReturnInfo(GEREntity attacker) {
        if (!(attacker.getUser() instanceof ServerPlayer serverPlayer)) {
            return;
        }
        for (ReturnData data : attacker.getMoveContext().get(RETURN_INFO)) {
            final Entity entity = data.getEntity();
            if (entity == null || !entity.isAlive()) {
                continue;
            }
            final Vec3 position = data.getOriginalPos();
            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
            buf.writeShort(7);

            buf.writeInt(entity.getId());

            buf.writeDouble(position.x());
            buf.writeDouble(position.y());
            buf.writeDouble(position.z());

            ServerChannelFeedbackPacket.send(serverPlayer, buf);
        }
    }

    @Override
    public void registerContextEntries(MoveContext ctx) {
        ctx.register(ENTITY_DATA, new WeakHashMap<>());
        ctx.register(RETURN_INFO, new ArrayList<>());
    }

    @Override
    protected @NonNull ReturnToZeroMove getThis() {
        return this;
    }

    @Override
    public @NonNull ReturnToZeroMove copy() {
        return copyExtras(new ReturnToZeroMove(getCooldown(), getWindup(), getDuration(), getMoveDistance()));
    }

    @Data
    public static class ReturnData {
        Vec3 originalPos;
        Entity entity;

        public ReturnData(Vec3 originalPos, Entity entity) {
            this.originalPos = originalPos;
            this.entity = entity;
        }
    }
}
