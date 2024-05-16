package net.arna.jcraft.common.attack.moves.kingcrimson;

import com.google.common.reflect.TypeToken;
import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import lombok.NonNull;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.core.ctx.MoveVariable;
import net.arna.jcraft.common.attack.moves.base.AbstractMove;
import net.arna.jcraft.common.entity.stand.KingCrimsonEntity;
import net.arna.jcraft.common.entity.stand.StandEntity;
import net.arna.jcraft.common.gravity.api.GravityChangerAPI;
import net.arna.jcraft.platform.JComponentPlatformUtils;
import net.arna.jcraft.registry.JPacketRegistry;
import net.minecraft.core.Vec3i;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import java.util.*;
import java.util.stream.Collectors;

public class PredictionMove extends AbstractMove<PredictionMove, KingCrimsonEntity> {
    public static final MoveVariable<Map<Entity, Vec3>> PREDICTION_INFO = new MoveVariable<>(new TypeToken<>() {
    });

    public PredictionMove(int cooldown, int windup, int duration, float moveDistance) {
        super(cooldown, windup, duration, moveDistance);
    }

    @Override
    public void onInitiate(KingCrimsonEntity attacker) {
        super.onInitiate(attacker);

        attacker.getMoveContext().get(PREDICTION_INFO).clear();

        // Send epitaph state start
        if (attacker.getUser() instanceof ServerPlayer player) {
            NetworkManager.sendToPlayer(player, JPacketRegistry.S2C_EPITAPH_STATE,
                    new FriendlyByteBuf(Unpooled.buffer().writeBoolean(true)));
        }
    }

    @Override
    public void tick(KingCrimsonEntity attacker) {
        super.tick(attacker);

        if (attacker.getMoveStun() == getWindupPoint()) {
            beginPrediction(attacker); // Clientside prediction, serverside is in specialAttack()
        }

        if (attacker.tickCount % 2 == 0) {
            tickPredictions(attacker);
            if (attacker.hasUser()) {
                attacker.getUserOrThrow().addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,
                        10, 2, true, false));
            }
        }
    }

    @Override
    public @NonNull Set<LivingEntity> perform(KingCrimsonEntity attacker, LivingEntity user, MoveContext ctx) {
        return Set.of();
    }

    public void beginPrediction(KingCrimsonEntity attacker) {
        if (!(attacker.getUser() instanceof ServerPlayer player)) {
            return;
        }

        Map<Entity, Vec3> predictionInfo = attacker.getMoveContext().get(PREDICTION_INFO);
        for (Entity entity : getEntitiesToCatch(attacker.level(), attacker, player)) {
            predictionInfo.put(entity, entity.position());
        }

        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeBoolean(true);
        buf.writeVarInt(getWindupPoint());
        NetworkManager.sendToPlayer(player, JPacketRegistry.S2C_TIME_ERASE_PREDICTION_STATE, buf);
    }

    public static void finishPrediction(KingCrimsonEntity attacker) {
        Map<Entity, Vec3> predictionInfo = attacker.getMoveContext().get(PREDICTION_INFO);
        for (Map.Entry<Entity, Vec3> prediction : predictionInfo.entrySet()) {
            Entity entity = prediction.getKey();
            if (entity == null) {
                continue;
            }

            Vec3 pos = prediction.getValue();
            entity.teleportToWithTicket(pos.x, pos.y, pos.z);
        }

        cancelPrediction(attacker, predictionInfo);
        attacker.cancelMove();
    }

    public static void cancelPrediction(KingCrimsonEntity attacker) {
        Map<Entity, Vec3> predictionInfo = attacker.getMoveContext().get(PREDICTION_INFO);
        cancelPrediction(attacker, predictionInfo);
    }

    public static void cancelPrediction(KingCrimsonEntity attacker, Map<Entity, Vec3> predictionInfo) {
        if (attacker.getUser() instanceof ServerPlayer player) {
            NetworkManager.sendToPlayer(player, JPacketRegistry.S2C_EPITAPH_STATE, new FriendlyByteBuf(Unpooled.buffer().writeBoolean(false)));
            NetworkManager.sendToPlayer(player, JPacketRegistry.S2C_TIME_ERASE_PREDICTION_STATE, new FriendlyByteBuf(Unpooled.buffer().writeBoolean(false)));
        }

        predictionInfo.clear();
    }

    public void tickPredictions(KingCrimsonEntity attacker) {
        Map<Entity, Vec3> predictionInfo = attacker.getMoveContext().get(PREDICTION_INFO);
        Map<Entity, Vec3> predictions = new HashMap<>(predictionInfo);
        updatePredictions(predictions.entrySet(), attacker.getMoveStun());
        predictionInfo.clear();
        predictionInfo.putAll(predictions);
    }

    public static List<Entity> getEntitiesToCatch(Level world, StandEntity<?, ?> stand, Player player) {
        if (world == null || stand == null) {
            return List.of();
        }

        return world.getEntitiesOfClass(Entity.class, stand.getBoundingBox().inflate(64),
                EntitySelector.NO_CREATIVE_OR_SPECTATOR.and(e -> e != stand && e != player));
    }

    public static void updatePredictions(Set<Map.Entry<Entity, Vec3>> predictionsSet, int ticksLeft) {
        Map<Entity, Map.Entry<Entity, Vec3>> predictions = predictionsSet.stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e));
        Set<Entity> updated = new HashSet<>();

        for (Map.Entry<Entity, Map.Entry<Entity, Vec3>> prediction : predictions.entrySet()) {
            updatePrediction(predictions, prediction.getValue(), updated, ticksLeft);
        }
    }

    private static void updatePrediction(Map<Entity, Map.Entry<Entity, Vec3>> predictions, Map.Entry<Entity, Vec3> prediction,
                                         Set<Entity> updated, int ticksLeft) {
        Entity entity = prediction.getKey();
        if (updated.contains(entity)) {
            return;
        }

        updated.add(entity);
        if (entity == null || !entity.isAlive()) {
            return;
        }

        Level world = entity.level();

        Vec3 currentPos = entity.position().add(0, 0.1, 0);
        Vec3 futurePos = currentPos;
        boolean changed = false;

        Vec3i gravity = GravityChangerAPI.getGravityDirection(entity).getNormal();
        Vec3 drop = new Vec3(gravity.getX(), gravity.getY(), gravity.getZ()).scale(9.81 / 400 * ticksLeft * ticksLeft);

        // If in air and not in a liquid, account for drop
        if (!entity.onGround() && !entity.isUnderWater() && !entity.isInLava()) {
            //JCraft.LOGGER.info("Target is in air");
            futurePos = futurePos.add(drop);
            changed = true;
        }

        // If moving faster than 0.01 m/s, account for distance traveled
        Vec3 velocity = entity.getDeltaMovement();
        if (entity instanceof Player player) // EXTREMELY cursed implementation of player velocity because NOTHING ELSE WORKS
        {
            velocity = JComponentPlatformUtils.getMiscData(player).getDesiredVelocity();
        }
        //JCraft.LOGGER.info("Target is moving at a velocity of: " + velocity);
        if (velocity.lengthSqr() > 0.0001) {
            Vec3 velocityComp = new Vec3(velocity.x * ticksLeft, Math.max(0, velocity.y * ticksLeft), velocity.z * ticksLeft);
            //JCraft.LOGGER.info("Modified velocity: " + velocityComp);
            futurePos = futurePos.add(velocityComp);
            changed = true;
        }

        Entity vehicle = entity.getVehicle();
        if (vehicle != null) {
            if (!predictions.containsKey(vehicle)) {
                return;
            }

            // Ensure vehicle is updated.
            Map.Entry<Entity, Vec3> vehiclePrediction = predictions.get(vehicle);
            updatePrediction(predictions, vehiclePrediction, updated, ticksLeft);
            // Account for change in position of vehicle.
            futurePos = futurePos.add(vehiclePrediction.getValue().subtract(vehiclePrediction.getKey().position()));
        }

        // Collision check between current and extrapolated future position
        if (!changed) {
            return;
        }

        //JCraft.LOGGER.info("Predicted position changed, time left: " + timeLeft);
        BlockHitResult hitResult = world.clip(new ClipContext(currentPos, futurePos, ClipContext.Block.COLLIDER, ClipContext.Fluid.SOURCE_ONLY, entity));
        prediction.setValue(hitResult.getLocation());
    }

    @Override
    public void registerContextEntries(MoveContext ctx) {
        ctx.register(PREDICTION_INFO, new WeakHashMap<>());
    }

    @Override
    protected @NonNull PredictionMove getThis() {
        return this;
    }

    @Override
    public @NonNull PredictionMove copy() {
        return copyExtras(new PredictionMove(getCooldown(), getWindup(), getDuration(), getMoveDistance()));
    }
}
