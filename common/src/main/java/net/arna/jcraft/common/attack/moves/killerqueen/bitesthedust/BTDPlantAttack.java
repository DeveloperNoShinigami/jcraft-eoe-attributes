package net.arna.jcraft.common.attack.moves.killerqueen.bitesthedust;

import io.netty.buffer.Unpooled;
import lombok.NonNull;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.core.ctx.MoveVariable;
import net.arna.jcraft.common.attack.moves.base.AbstractSimpleAttack;
import net.arna.jcraft.common.entity.stand.KQBTDEntity;
import net.arna.jcraft.common.network.s2c.ServerChannelFeedbackPacket;
import net.arna.jcraft.common.util.JUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import java.util.List;
import java.util.Set;

public final class BTDPlantAttack extends AbstractSimpleAttack<BTDPlantAttack, KQBTDEntity> {
    public static final MoveVariable<LivingEntity> BTD_ENTITY = new MoveVariable<>(LivingEntity.class);
    public static final MoveVariable<Vec3> BTD_POS = new MoveVariable<>(Vec3.class);

    public BTDPlantAttack(final int cooldown, final int windup, final int duration, final float attackDistance, final int stun, final float hitboxSize, final float offset) {
        super(cooldown, windup, duration, attackDistance, 0f, stun, hitboxSize, 0f, offset);
    }

    @Override
    public @NonNull Set<LivingEntity> perform(final KQBTDEntity attacker, final LivingEntity user, final MoveContext ctx) {
        final Set<LivingEntity> targets = super.perform(attacker, user, ctx);
        if (targets.isEmpty()) {
            return Set.of();
        }

        final Entity btdEntity = JUtils.getUserIfStand(targets.stream().findFirst().orElseThrow());
        if (btdEntity instanceof LivingEntity living) {
            ctx.set(BTD_ENTITY, living);
            ctx.set(BTD_POS, btdEntity.position());
        }
        return targets;
    }

    public void tickBomb(final KQBTDEntity stand) {
        if (stand.getUser() instanceof ServerPlayer player) {
            displayBTDParticles(stand, player);
        }
    }

    private void displayBTDParticles(final KQBTDEntity stand, final ServerPlayer playerEntity) {
        final Entity bombEntity = stand.getMoveContext().get(BTD_ENTITY);
        final Vec3 bombPos = stand.getMoveContext().get(BTD_POS);
        final boolean bombExists = bombEntity != null;

        double dX1 = 0;
        double dY1 = 0;
        double dZ1 = 0;
        double dX2 = 0;
        double dY2 = 0;
        double dZ2 = 0;

        AABB bBox = null;

        if (bombEntity != null) { // If the bomb isn't a block
            dX1 = bombEntity.getX();
            dY1 = bombEntity.getY();
            dZ1 = bombEntity.getZ();

            bBox = bombEntity.getBoundingBox();

            dX2 = bBox.getXsize();
            dY2 = bBox.getYsize();
            dZ2 = bBox.getZsize();
        }

        if (!bombExists) {
            return;
        }
        final FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeShort(9);

        buf.writeDouble(dX1);
        buf.writeDouble(dY1);
        buf.writeDouble(dZ1);
        buf.writeDouble(dX2);
        buf.writeDouble(dY2);
        buf.writeDouble(dZ2);

        buf.writeDouble(bombPos.x);
        buf.writeDouble(bombPos.y);
        buf.writeDouble(bombPos.z);

        boolean anyInRange = false;
        final Vec3 pos = bombEntity.position();
        final Vec3 v1 = pos.add(3, 3, 3);
        final Vec3 v2 = pos.add(-3, -3, -3);
        List<LivingEntity> list = stand.level().getEntitiesOfClass(LivingEntity.class, new AABB(v1, v2),
                EntitySelector.LIVING_ENTITY_STILL_ALIVE.and(e -> e != bombEntity));
        for (LivingEntity l : list) {
            if (l.distanceToSqr(pos) < 9) {
                anyInRange = true;
                break;
            }
        }

        buf.writeBoolean(anyInRange);

        if (bBox.getSize() > 0) {
            ServerChannelFeedbackPacket.send(playerEntity, buf);
        }
    }

    @Override
    public void registerContextEntries(final MoveContext ctx) {
        ctx.register(BTD_ENTITY);
        ctx.register(BTD_POS);
    }

    @Override
    protected @NonNull BTDPlantAttack getThis() {
        return this;
    }

    @Override
    public @NonNull BTDPlantAttack copy() {
        return copyExtras(new BTDPlantAttack(getCooldown(), getWindup(), getDuration(), getMoveDistance(), getStun(), getHitboxSize(),
                getOffset()));
    }
}
