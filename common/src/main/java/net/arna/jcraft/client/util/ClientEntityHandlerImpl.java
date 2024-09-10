package net.arna.jcraft.client.util;

import net.arna.jcraft.client.JClientConfig;
import net.arna.jcraft.client.particle.AuraArcParticle;
import net.arna.jcraft.client.particle.AuraBlobParticle;
import net.arna.jcraft.common.component.living.CommonBombTrackerComponent;
import net.arna.jcraft.common.entity.SheerHeartAttackEntity;
import net.arna.jcraft.common.entity.stand.AbstractPurpleHazeEntity;
import net.arna.jcraft.common.entity.stand.HGEntity;
import net.arna.jcraft.common.entity.stand.StandEntity;
import net.arna.jcraft.common.entity.stand.WhiteSnakeEntity;
import net.arna.jcraft.common.gravity.api.GravityChangerAPI;
import net.arna.jcraft.common.gravity.util.RotationUtil;
import net.arna.jcraft.common.util.IClientEntityHandler;
import net.arna.jcraft.common.util.JUtils;
import net.arna.jcraft.registry.JParticleTypeRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.List;
import java.util.UUID;

public class ClientEntityHandlerImpl implements IClientEntityHandler {
    public static final ClientEntityHandlerImpl INSTANCE = new ClientEntityHandlerImpl();

    private ClientEntityHandlerImpl() {
    }

    @Override
    public void bombTrackerParticleTick(Entity entity, CommonBombTrackerComponent.BombData bombData) {
        Vec3 bombPos = bombData.getBombPos();
        if (bombPos == null) {
            return;
        }
        ClientLevel clientWorld = (ClientLevel) entity.level();

        SimpleParticleType particleType = ParticleTypes.WITCH; // Far particle
        Vec3 v1 = bombPos.add(3, 3, 3);
        Vec3 v2 = bombPos.add(-3, -3, -3);
        List<LivingEntity> list = clientWorld.getEntitiesOfClass(LivingEntity.class, new AABB(v1, v2), EntitySelector.LIVING_ENTITY_STILL_ALIVE);

        double xLength = 0, yLength = 0, zLength = 0;
        if (!bombData.isBlock) {
            Entity bombEntity = bombData.bombEntity;
            if (bombEntity == null) {
                bombEntity = bombData.bombItem.getEntityRepresentation();
            }
            if (bombEntity == null) {
                return;
            }
            list.remove(bombEntity);
            xLength = bombEntity.getBoundingBox().getXsize();
            yLength = bombEntity.getBoundingBox().getYsize();
            zLength = bombEntity.getBoundingBox().getZsize();
        }

        for (LivingEntity l : list) {
            if (l.distanceToSqr(bombPos) < 9) {
                particleType = ParticleTypes.WAX_ON; // Near particle
                break;
            }
        }

        RandomSource random = clientWorld.getRandom();

        //TODO: fix bomb particle rendering in other gravities
        if (bombData.isEntity) {
            for (int h = 0; h < 16; ++h) {
                clientWorld.addParticle(particleType,
                        bombPos.x + random.triangle(0, 1) * xLength,
                        bombPos.y + random.triangle(0, 1) * yLength,
                        bombPos.z + random.triangle(0, 1) * zLength,
                        0, 0, 0);
            }
        }

        if (bombData.isBlock) {
            for (int h = 0; h < 16; ++h) {
                clientWorld.addParticle(particleType,
                        bombPos.x + random.nextDouble(),
                        bombPos.y + random.nextDouble(),
                        bombPos.z + random.nextDouble(),
                        0, 0, 0);
            }
        }
    }

    @Override
    public void standEntityClientTick(StandEntity<?, ?> stand) {
        // This won't be called if the stand has no user; see StandEntity#tick()
        final LivingEntity user = stand.getUserOrThrow();
        final Minecraft client = Minecraft.getInstance();

        // Stand Auras
        if (JClientConfig.getInstance().isStandAuras()) {
            if (stand.distanceToSqr(client.player) > 6400) {
                return; // 5 chunk aura render distance
            }
            if (user.isInvisible()) {
                return;
            }

            final boolean isFP = client.options.getCameraType().isFirstPerson();
            final boolean isOwnerAndFP = user == client.player && isFP;

            final ClientLevel clientWorld = (ClientLevel) stand.level();
            final RandomSource random = clientWorld.getRandom();

            final Direction gravity = GravityChangerAPI.getGravityDirection(stand);

            final Vector3f auraColor = stand.getAuraColor();

            /*
            Basically,
            any stand you do not own should have an Aura drawn.
            Stands you do own should have an aura drawn EITHER if you are not in first person, or it is detached.
             */

            if ((!isOwnerAndFP || stand.isFree())
                    && !(stand.isRemoteAndControllable() && isFP)
                    && random.nextBoolean()) {
                displayAuraParticles(clientWorld, random, stand, RotationUtil.vecPlayerToWorld(stand.getBbWidth(), stand.getBbHeight(), stand.getBbWidth(), gravity), gravity, auraColor);
            }
            if (!isOwnerAndFP && random.nextBoolean() && !JClientUtils.shouldNotRender(user)) {
                displayAuraParticles(clientWorld, random, user, RotationUtil.vecPlayerToWorld(user.getBbWidth(), user.getBbHeight(), user.getBbWidth(), gravity), gravity, auraColor);
            }
        }
    }

    private static final double metersPerTickSquared = 9.81 / 400;

    private void displayAuraParticles(ClientLevel clientWorld, RandomSource random, Entity entity, Vector3f maxBox, Direction gravity, Vector3f color) {
        if (JClientUtils.shouldNotRender(entity)) {
            return;
        }

        Vec3 pos = entity.position();
        Vec3 vel = Vec3.atLowerCornerOf(gravity.getNormal()).scale(-metersPerTickSquared);
        /*
        Vec3d vel = entity.getVelocity();
        if (entity instanceof ClientPlayerEntity)
            vel = entity.getPos().subtract(entity.prevX, entity.prevY, entity.prevZ);
        vel = vel.subtract(Vec3d.of(gravity.getVector()).multiply(metersPerTickSquared));
         */

        // minecraft is single-threaded :)
        AuraArcParticle.Factory.parent = entity;
        AuraArcParticle.Factory.color = color;
        AuraBlobParticle.Factory.parent = entity;
        AuraBlobParticle.Factory.color = color;

        clientWorld.addParticle(JParticleTypeRegistry.AURA_ARC.get(), false,
                pos.x + maxBox.x() * random.triangle(0, 1),
                pos.y + maxBox.y() * random.triangle(0.5, 0.5),
                pos.z + maxBox.z() * random.triangle(0, 1),
                vel.x, vel.y, vel.z);

        clientWorld.addParticle(JParticleTypeRegistry.AURA_BLOB.get(), false,
                pos.x + maxBox.x() * random.triangle(0, 1),
                pos.y + maxBox.y() * random.triangle(0.5, 0.5),
                pos.z + maxBox.z() * random.triangle(0, 1),
                vel.x, vel.y, vel.z);
    }

    @Override
    public void whiteSnakeRemoteClientTick(@NotNull WhiteSnakeEntity whiteSnakeEntity) {
        Minecraft client = Minecraft.getInstance();
        if (JUtils.getStand(client.player) != whiteSnakeEntity) {
            return;
        }

        Options options = client.options;
        float f = 0, s = 0;
        boolean jump = options.keyJump.isDown();
        if (options.keyUp.isDown()) {
            f += 1.0f;
        }
        if (options.keyDown.isDown()) {
            f += 1.0f;
        }
        if (options.keyLeft.isDown()) {
            s -= 1.0f;
        }
        if (options.keyRight.isDown()) {
            s += 1.0f;
        }

        //JCraft.LOGGER.info("Handling remote movement for: " + whiteSnakeEntity + " with " + f + " " + s + " " + jump);
        whiteSnakeEntity.tickRemoteMovement(f, s, jump);
    }

    @Override
    public void purpleHazeRemoteClientTick(@NotNull AbstractPurpleHazeEntity<?, ?> purpleHazeEntity) {
        Minecraft client = Minecraft.getInstance();
        if (JUtils.getStand(client.player) != purpleHazeEntity) {
            return;
        }

        Options options = client.options;
        float f = 0, s = 0;
        boolean jump = options.keyJump.isDown();
        if (options.keyUp.isDown()) {
            f += 1.0f;
        }
        if (options.keyDown.isDown()) {
            f += 1.0f;
        }
        if (options.keyLeft.isDown()) {
            s -= 1.0f;
        }
        if (options.keyRight.isDown()) {
            s += 1.0f;
        }

        //JCraft.LOGGER.info("Handling remote movement for: " + purpleHazeEntity + " with " + f + " " + s + " " + jump);
        purpleHazeEntity.tickRemoteMovement(f, s, jump);
    }

    @Override
    public void hierophantGreenRemoteClientTick(@NotNull HGEntity hgEntity) {
        Minecraft client = Minecraft.getInstance();
        if (JUtils.getStand(client.player) != hgEntity) {
            return;
        }

        Options options = client.options;
        float f = 0, s = 0;
        boolean jump = options.keyJump.isDown();
        boolean sneak = options.keyShift.isDown();
        if (options.keyUp.isDown()) {
            f += 1.0f;
        }
        if (options.keyDown.isDown()) {
            f += 1.0f;
        }
        if (options.keyLeft.isDown()) {
            s -= 1.0f;
        }
        if (options.keyRight.isDown()) {
            s += 1.0f;
        }

        hgEntity.tickRemoteMovement(f, s, jump, sneak);
    }

    @Override
    public void sheerHeartAttackEntityTick(SheerHeartAttackEntity sHAEntity) {
        Minecraft client = Minecraft.getInstance();
        UUID ownerId = sHAEntity.getOwnerId();
        if (ownerId == null) {
            return;
        }
        if (ownerId.equals(client.player.getUUID()) && sHAEntity.tickCount <= 300) {
            sHAEntity.setCustomName(Component.literal(15 - sHAEntity.tickCount / 20 + "s"));
        }
    }
}
