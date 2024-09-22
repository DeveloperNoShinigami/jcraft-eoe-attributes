package net.arna.jcraft.common.attack.moves.thefool;

import io.netty.buffer.Unpooled;
import lombok.NonNull;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.core.ctx.MoveVariable;
import net.arna.jcraft.common.attack.moves.base.AbstractMove;
import net.arna.jcraft.common.entity.PlayerCloneEntity;
import net.arna.jcraft.common.entity.stand.StandType;
import net.arna.jcraft.common.entity.stand.TheFoolEntity;
import net.arna.jcraft.common.network.s2c.ServerChannelFeedbackPacket;
import net.arna.jcraft.common.util.JUtils;
import net.arna.jcraft.platform.JComponentPlatformUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;
import java.util.Set;

public final class SandCloneMove extends AbstractMove<SandCloneMove, TheFoolEntity> {
    private static final MoveVariable<Mob> SAND_CLONE = new MoveVariable<>(Mob.class);

    public SandCloneMove(int cooldown, int windup, int duration, float moveDistance) {
        super(cooldown, windup, duration, moveDistance);
        ranged = true;
    }

    @Override
    public @NonNull Set<LivingEntity> perform(TheFoolEntity attacker, LivingEntity user, MoveContext ctx) {
        final Vec3 pos = user.getEyePosition();

        // Display sand effect
        final FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());

        buf.writeShort(11);
        buf.writeDouble(pos.x);
        buf.writeDouble(pos.y);
        buf.writeDouble(pos.z);
        buf.writeDouble(2);

        for (ServerPlayer sendPlayer : ((ServerLevel) attacker.level()).players()) {
            ServerChannelFeedbackPacket.send(sendPlayer, buf);
            if (sendPlayer == user) {
                continue;
            }
            if (sendPlayer.closerThan(user, 4)) // Blind players caught in the cloud
            {
                sendPlayer.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 40, 0, true, false));
            }
        }

        if (user.isShiftKeyDown()) {
            for (int i = 0; i < 32; i++) {
                double y = 0.4;
                double h = i * 3.1415 / 8;
                double hDiv = 5;
                if (i >= 16) {
                    y = 0.8;
                    hDiv = 9.5;
                }
                TheFoolEntity.createFoolishSand(attacker.level(), attacker, attacker.blockPosition(),
                        new Vec3(Math.sin(h) / hDiv, y, Math.cos(h) / hDiv));
            }
            return Set.of();
        }

        // Summon clone
        if (user instanceof ServerPlayer player) {
            final PlayerCloneEntity playerCloneEntity = new PlayerCloneEntity(attacker.level());
            playerCloneEntity.copyPosition(player);
            playerCloneEntity.setMaster(player);
            playerCloneEntity.markSand();
            playerCloneEntity.disableDrops();

            playerCloneEntity.setItemSlot(EquipmentSlot.HEAD, user.getItemBySlot(EquipmentSlot.HEAD).copy());
            playerCloneEntity.setItemSlot(EquipmentSlot.CHEST, user.getItemBySlot(EquipmentSlot.CHEST).copy());
            playerCloneEntity.setItemSlot(EquipmentSlot.LEGS, user.getItemBySlot(EquipmentSlot.LEGS).copy());
            playerCloneEntity.setItemSlot(EquipmentSlot.FEET, user.getItemBySlot(EquipmentSlot.FEET).copy());

            setSandClone(ctx, playerCloneEntity);
        } else if (user instanceof Mob mob) {
            setSandClone(ctx, JUtils.mobCloneOf(mob));
        }

        attacker.level().addFreshEntity(ctx.get(SAND_CLONE));
        return Set.of();
    }

    public void tickClone(TheFoolEntity attacker) {
        final Mob sandClone = attacker.getMoveContext().get(SAND_CLONE);
        if (sandClone != null && sandClone.tickCount > 200) {
            setSandClone(attacker.getMoveContext(), null);
        }
    }

    public void discardClone(TheFoolEntity attacker) {
        final Mob sandClone = attacker.getMoveContext().get(SAND_CLONE);
        if (sandClone != null) {
            sandClone.discard();
        }
    }

    private void setSandClone(MoveContext ctx, Mob clone) {
        final Mob currentSandClone = ctx.get(SAND_CLONE);
        if (currentSandClone != null) {
            currentSandClone.kill();
        }
        ctx.set(SAND_CLONE, clone);
        if (clone == null) {
            return;
        }
        JComponentPlatformUtils.getStandData(clone).setType(StandType.NONE);
        applySandCloneModifiers(clone);
    }

    public static void applySandCloneModifiers(LivingEntity entity) {
        if (entity == null) {
            JCraft.LOGGER.error("Tried to apply sand clone attribute modifiers to invalid entity!");
            return;
        }
        final AttributeInstance maxHealthAttribute = entity.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealthAttribute == null) {
            JCraft.LOGGER.error("Tried to apply sand clone attribute modifiers to entity with no max health attribute!");
            return;
        }

        maxHealthAttribute.addPermanentModifier(
                new AttributeModifier("Sand Clone Max Health Modifier", -1.0, AttributeModifier.Operation.MULTIPLY_TOTAL)
        );
    }

    @Override
    public void registerContextEntries(MoveContext ctx) {
        ctx.register(SAND_CLONE);
    }

    @Override
    protected @NonNull SandCloneMove getThis() {
        return this;
    }

    @Override
    public @NonNull SandCloneMove copy() {
        return copyExtras(new SandCloneMove(getCooldown(), getWindup(), getDuration(), getMoveDistance()));
    }
}
