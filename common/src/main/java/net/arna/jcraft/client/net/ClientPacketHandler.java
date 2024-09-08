package net.arna.jcraft.client.net;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.architectury.networking.NetworkManager;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.api.layered.modifier.SpeedModifier;
import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import lombok.experimental.UtilityClass;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.client.JClientConfig;
import net.arna.jcraft.client.JCraftClient;
import net.arna.jcraft.client.gui.ServerConfigUI;
import net.arna.jcraft.client.gui.hud.EpitaphOverlay;
import net.arna.jcraft.client.renderer.effects.AttackHitboxEffectRenderer;
import net.arna.jcraft.client.renderer.effects.TimeErasePredictionEffectRenderer;
import net.arna.jcraft.client.rendering.handler.CrimsonShaderHandler;
import net.arna.jcraft.client.rendering.handler.ZaWarudoShaderHandler;
import net.arna.jcraft.client.util.JClientUtils;
import net.arna.jcraft.common.config.ConfigOption;
import net.arna.jcraft.common.entity.stand.MadeInHeavenEntity;
import net.arna.jcraft.common.network.s2c.ShaderActivationPacket;
import net.arna.jcraft.common.network.s2c.TimeAccelStatePacket;
import net.arna.jcraft.common.spec.JSpec;
import net.arna.jcraft.common.splatter.Splatter;
import net.arna.jcraft.common.util.*;
import net.arna.jcraft.registry.JParticleTypeRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import static net.arna.jcraft.registry.JPacketRegistry.*;


@UtilityClass
public class ClientPacketHandler {

    public static void init() {
        register(S2C_SERVER_CHANNEL_FEEDBACK, ClientPacketHandler::handleChannelFeedback);
        register(S2C_PLAYER_ANIMATION, ClientPacketHandler::handleAnimation);
        register(S2C_SHADER_ACTIVATION, ClientPacketHandler::handleShaderActivation);
        register(S2C_SHADER_DEACTIVATION, ClientPacketHandler::handleShaderDeactivation);
        register(S2C_TIME_ACCELERATION_STATE, ClientPacketHandler::handleTimeAccelState);
        register(S2C_EPITAPH_STATE, ClientPacketHandler::handleEpitaphOverlayState);
        register(S2C_TIME_ERASE_PREDICTION_STATE, ClientPacketHandler::handlePredictionState);
        register(S2C_SERVER_CONFIG, ClientPacketHandler::handleServerConfig);
        register(S2C_J_EXPLOSION, ClientPacketHandler::handleJExplosion);
        register(S2C_COMBO_COUNTER, ClientPacketHandler::handleComboCounter);
        register(S2C_TIME_STOP, ClientPacketHandler::handleTimeStop);
        register(S2C_SPLATTER, ClientPacketHandler::handleSplatter);
        register(S2C_STAND_HURT, ClientPacketHandler::handleStandHurt);
        register(S2C_PREDICTION_UPDATE, ClientPacketHandler::handlePrediction);
    }

    private static void handlePrediction(@NotNull Minecraft client, FriendlyByteBuf buf) {
        int size = buf.readInt();
        if (size == 0) {
            return;
        }
        for (int i = 0; i < size; i++) {
            int entID = buf.readInt();
            Vec3 predictedPos = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());

            client.execute(() -> {
                Entity ent = client.level.getEntity(entID);
                if (ent == null) {
                    return;
                }
                // ent.setPos() is awful in tandem with getTrackedPosition().setPos();
                ent.getPositionCodec().setBase(predictedPos);
            });
        }
    }

    private static void handleTimeStop(@NotNull Minecraft client, FriendlyByteBuf buf) {
        if (client.level == null || client.player == null) {
            return;
        }

        boolean isStart = buf.readBoolean();
        int entID = buf.readInt();

        if (isStart) {
            Vec3 position = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
            ResourceKey<Level> registryKey = buf.readResourceKey(Registries.DIMENSION);
            int time = buf.readInt();

            client.execute(() -> {
                Entity ent = client.level.getEntity(entID);
                if (!(ent instanceof LivingEntity livingEntity)) {
                    return;
                }
                JClientUtils.activeTimestops.add(new DimensionData(livingEntity, position, registryKey, time));
            });
        } else {
            JClientUtils.removeTimestop(entID);
        }
    }

    private static void register(ResourceLocation id, Consumer<FriendlyByteBuf> handler) {
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, id, (buf, context) -> {
            handler.accept(buf);
        });
    }

    private static void register(ResourceLocation id, BiConsumer<Minecraft, FriendlyByteBuf> handler) {
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, id, (buf, context) -> {
            handler.accept(Minecraft.getInstance(), buf);
        });
    }

    public static void handleAnimation(@NotNull Minecraft client, FriendlyByteBuf buf) {
        if (client.level == null || client.player == null) {
            return;
        }

        int entID = buf.readInt();
        String animID = buf.readUtf(); // I know exactly how unoptimized this is, but I fail to care
        boolean isSpec = buf.readBoolean();

        //JCraft.LOGGER.info("JCRAFT CLIENT:\nRecieving animation packet of animID: " + animID + " for entity ID: " + entID);

        int moveStun;
        float animationSpeed;

        if (isSpec) {
            moveStun = buf.readInt();
            animationSpeed = buf.readFloat();
            //JCraft.LOGGER.info("Animation packet is for specs, and has attached moveStun: " + moveStun + " and attackID: " + attackID);
        } else {
            moveStun = 0;
            animationSpeed = 0f;
        }

        client.execute(() -> {
            Entity ent = client.level.getEntity(entID);
            //JCraft.LOGGER.info("Animation is to be applied to: " + ent);
            if (ent instanceof Player player) {
                // Animate
                ModifierLayer<IAnimation> animationContainer = ((IJCraftAnimatedPlayer) player).jcraft_getModAnimation();
                KeyframeAnimation anim = PlayerAnimationRegistry.getAnimation(JCraft.id(animID));
                if (anim == null) {
                    JCraft.LOGGER.error("Tried to play null animation on player: " + player + ", in world " + client.level);
                    return;
                }

                // Remove last speed modifier, this is rather primitive but will do for now
                if (animationContainer.size() > 0) {
                    animationContainer.removeModifier(0);
                }

                // Synchronize spec values
                if (isSpec) {
                    JSpec<?, ?> spec = JUtils.getSpec(player);
                    if (spec == null) {
                        JCraft.LOGGER.error("Tried to set spec animation values on player without spec: " + player + ", in world " + client.level);
                    } else {
                        //JCraft.LOGGER.info("Spec: " + spec.getName());
                        spec.moveStun = moveStun;

                        //JCraft.LOGGER.info("Speed: " + animationSpeed);
                        animationContainer.addModifierBefore(new SpeedModifier(animationSpeed));
                    }
                }

                //JCraft.LOGGER.info("Animation to be applied: " + anim);
                animationContainer.setAnimation(new KeyframeAnimationPlayer(anim));
            }
        });
    }

    public static void handleChannelFeedback(@NotNull Minecraft client, FriendlyByteBuf buf) {
        if (client.level == null || client.player == null) {
            return;
        }

        short control = buf.readShort();
        switch (control) {
            // Attack hit boxes
            case (1) -> {
                int count = buf.readVarInt();

                List<AABB> boxes = IntStream.range(0, count)
                        .mapToObj(i -> {
                            double minX = buf.readDouble();
                            double minY = buf.readDouble();
                            double minZ = buf.readDouble();

                            double maxX = buf.readDouble();
                            double maxY = buf.readDouble();
                            double maxZ = buf.readDouble();

                            return new AABB(minX, minY, minZ, maxX, maxY, maxZ);
                        })
                        .toList();

                // Run on render thread to avoid concurrency issues.
                RenderSystem.recordRenderCall(() -> AttackHitboxEffectRenderer.addHitboxes(boxes));
            }

            // Time erase trackers
            case (2) -> {
                double posX = buf.readDouble();
                double posY = buf.readDouble();
                double posZ = buf.readDouble();
                double sizeX = Mth.clamp(buf.readDouble(), 0.1, 100);
                double sizeY = Mth.clamp(buf.readDouble(), 0.1, 100);
                double sizeZ = Mth.clamp(buf.readDouble(), 0.1, 100);

                client.execute(() -> {
                    Random random = new Random();

                    for (int h = 0; h < 8; ++h) {
                        client.level.addParticle(
                                JParticleTypeRegistry.KCPARTICLE.get(),
                                posX + random.nextDouble(sizeX) - sizeX / 2,
                                posY + random.nextDouble(sizeY),
                                posZ + random.nextDouble(sizeZ) - sizeZ / 2,
                                0.0, 0.0, 0.0
                        );
                    }
                });
            }

            // Generic single particle
            case (3) -> {
                double x = buf.readDouble();
                double y = buf.readDouble();
                double z = buf.readDouble();
                JParticleType particleType = buf.readEnum(JParticleType.class);

                client.execute(() -> client.level.addParticle(particleType.getParticleType(), true, x, y, z,
                        0, 0, 0));
            }

            // Complex hit spark
            case (5) -> {
                double x = buf.readDouble();
                double y = buf.readDouble();
                double z = buf.readDouble();
                JParticleType particleType = buf.readEnum(JParticleType.class);
                int sparkCount = buf.readInt();
                double speed = buf.readDouble();

                client.execute(() -> {
                    Random random = new Random();
                    SimpleParticleType type = particleType.getParticleType();
                    for (int i = 0; i < sparkCount; i++) {
                        Vec3 vel = JUtils.randUnitVec(random);
                        client.level.addParticle(type, false,
                                x + random.nextGaussian() * 0.33, y + random.nextGaussian() * 0.33, z + random.nextGaussian() * 0.33,
                                vel.x * speed, vel.y * speed, vel.z * speed);
                    }
                });
            }

            // Return to Zero trackers
            case (7) -> {
                int entID = buf.readInt();
                Vec3 originalPos = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());

                client.execute(() -> {
                    Entity ent = client.level.getEntity(entID);
                    if (ent == null) {
                        return;
                    }
                    Vec3 currentPos = ent.getEyePosition();
                    Vec3 originalToCurrent = currentPos.subtract(originalPos).normalize();
                    for (double h = 0; h < currentPos.distanceTo(originalPos); ++h) {
                        client.level.addParticle(
                                ParticleTypes.ELECTRIC_SPARK,
                                originalPos.x + originalToCurrent.x * h, originalPos.y + originalToCurrent.y * h, originalPos.z + originalToCurrent.z * h,
                                -originalToCurrent.x, -originalToCurrent.y, -originalToCurrent.z
                        );
                    }
                });
            }

            // Bites the Dust tracker
            case (9) -> {
                double v1x = buf.readDouble();
                double v1y = buf.readDouble();
                double v1z = buf.readDouble();

                double v2x = buf.readDouble();
                double v2y = buf.readDouble();
                double v2z = buf.readDouble();

                double oX = buf.readDouble();
                double oY = buf.readDouble();
                double oZ = buf.readDouble();

                boolean inRange = buf.readBoolean();

                client.execute(() -> {
                    Random random = new Random();

                    for (int h = 0; h < 16; ++h) {
                        double x = v1x + random.nextDouble(v2x) - v2x / 2;
                        double y = v1y + random.nextDouble(v2y) - v2y / 2;
                        double z = v1z + random.nextDouble(v2z) - v2z / 2;

                        client.level.addParticle(
                                inRange ? ParticleTypes.WAX_OFF : ParticleTypes.GLOW,
                                x, y, z, 0, 0, 0);
                    }

                    for (int h = 0; h < 8; ++h) {
                        double x = oX + random.nextDouble(v2x) - v2x / 2;
                        double y = oY + random.nextDouble(v2y) - v2y / 2;
                        double z = oZ + random.nextDouble(v2z) - v2z / 2;

                        client.level.addParticle(
                                ParticleTypes.GLOW,
                                x, y, z, 0, 0, 0);
                    }
                });
            }

            // Crossfire hurricane
            case (10) -> {
                Random random = new Random();
                double x = buf.readDouble();
                double y = buf.readDouble();
                double z = buf.readDouble();

                client.execute(() -> {
                    for (int h = 0; h < 360; ++h) {
                        client.level.addParticle(
                                random.nextInt(0, 5) > 3 ? ParticleTypes.LAVA : ParticleTypes.FLAME,
                                x + Math.sin(h) * 4 + random.nextGaussian() * 2, y + random.nextGaussian() * 1.5, z + Math.cos(h) * 4 + random.nextGaussian() * 2,
                                Math.sin(h + 1.57) / 4, 0, Math.cos(h + 1.57) / 4);
                    }
                });
            }

            // Fool Dust Cloud
            case (11) -> {
                double x = buf.readDouble();
                double y = buf.readDouble();
                double z = buf.readDouble();
                double size = buf.readDouble();

                client.execute(() -> {
                    Random random = new Random();
                    for (int h = 0; h < size * 128; ++h) {
                        client.level.addParticle(
                                new BlockParticleOption(ParticleTypes.FALLING_DUST, Blocks.SAND.defaultBlockState()),
                                x + random.nextGaussian() * size,
                                y + random.nextGaussian() * size,
                                z + random.nextGaussian() * size,
                                0, 0, 0);
                    }
                });
            }

            // Reset Player Animation
            case (13) -> {
                int entID = buf.readInt();

                client.execute(() -> {
                    Entity ent = client.level.getEntity(entID);
                    if (ent instanceof Player player) {
                        ModifierLayer<IAnimation> animationContainer = ((IJCraftAnimatedPlayer) player).jcraft_getModAnimation();
                        animationContainer.setAnimation(null);
                    }
                });
            }
        }
    }

    public static void handleShaderActivation(@NotNull Minecraft client, FriendlyByteBuf buf) {
        int delay = buf.readInt();
        int duration = buf.readInt();
        ShaderActivationPacket.Type type = ShaderActivationPacket.Type.byName(buf.readUtf());
        Level world = client.level;
        if (world == null) {
            return;
        }

        switch (type) {
            case NONE -> {
            }
            case ZA_WARUDO -> {
                int id = buf.readInt();
                client.execute(() -> {
                    Entity sourceShader = world.getEntity(id);
                    if (sourceShader instanceof LivingEntity livingEntity) {

                        ZaWarudoShaderHandler zaWarudoShaderHandler = ZaWarudoShaderHandler.INSTANCE;
                        zaWarudoShaderHandler.shaderSourceEntity = Optional.of(livingEntity).orElse(client.player);
                        zaWarudoShaderHandler.effectLength = duration;
                        zaWarudoShaderHandler.shouldRender = true;

                    }
                });
            }
            case CRIMSON -> client.execute(() -> {
                if (!JClientConfig.getInstance().isTimeEraseShader()) {
                    return;
                }

                CrimsonShaderHandler crimsonShaderHandler = CrimsonShaderHandler.INSTANCE;
                crimsonShaderHandler.effectLength = duration;
                crimsonShaderHandler.shouldRender = true;


            });
        }
    }

    public static void handleShaderDeactivation(@NotNull Minecraft client, FriendlyByteBuf buf) {
        ShaderActivationPacket.Type type = ShaderActivationPacket.Type.byName(buf.readUtf());
        Level world = client.level;
        if (world != null) {
            switch (type) {
                case NONE -> {
                }
                case ZA_WARUDO -> client.execute(() -> {

                    ZaWarudoShaderHandler zaWarudoShaderHandler = ZaWarudoShaderHandler.INSTANCE;
                    zaWarudoShaderHandler.shouldRender = false;
                    zaWarudoShaderHandler.renderingEffect = false;

                });
                case CRIMSON -> client.execute(() -> {

                    CrimsonShaderHandler crimsonShaderHandler = CrimsonShaderHandler.INSTANCE;
                    crimsonShaderHandler.shouldRender = false;
                    crimsonShaderHandler.renderingEffect = false;

                });
            }
        }
    }

    public static void handleTimeAccelState(@NotNull Minecraft client, FriendlyByteBuf buf) {
        TimeAccelStatePacket.State state = TimeAccelStatePacket.State.values()[buf.readVarInt()];
        Entity e = client.level == null ? null : client.level.getEntity(buf.readVarInt());

        if (!(e instanceof MadeInHeavenEntity mih) || !mih.isAlive()) {
            return;
        }

        switch (state) {
            case START ->
                    TimeAccelStatePacket.accelerations.put(mih.getId(), new TimeAccelStatePacket.TimeAcceleration(buf.readVarInt(), mih.getId()));
            case STOP -> TimeAccelStatePacket.accelerations.remove(mih.getId());
        }
    }

    public static void handleEpitaphOverlayState(@NotNull Minecraft client, FriendlyByteBuf buf) {
        boolean start = buf.readBoolean();
        client.execute(() -> {
            if (start) {
                EpitaphOverlay.start();
            } else {
                EpitaphOverlay.stop();
            }
        });
    }

    public static void handlePredictionState(@NotNull Minecraft client, FriendlyByteBuf buf) {
        boolean start = buf.readBoolean();
        int length = start ? buf.readVarInt() : 0;

        client.execute(() -> {
            if (start) {
                TimeErasePredictionEffectRenderer.startEffect(length);
            } else {
                TimeErasePredictionEffectRenderer.stopEffect();
            }
        });
    }

    public static void handleServerConfig(@NotNull Minecraft client, FriendlyByteBuf buf) {
        boolean editable = buf.readBoolean();
        boolean show = buf.readBoolean();

        ConfigOption.readOptions(buf);

        if (show) {
            client.execute(() -> ServerConfigUI.show(editable));
        }
    }

    private static void handleJExplosion(@NotNull Minecraft client, FriendlyByteBuf buf) {
        ClientboundExplodePacket nativePacket = new ClientboundExplodePacket(buf);
        JExplosionModifier modifier = buf.readBoolean() ? JExplosionModifier.read(buf) : null;

        client.execute(() -> {
            Explosion explosion = new Explosion(client.level, null, nativePacket.getX(), nativePacket.getY(), nativePacket.getZ(),
                    nativePacket.getPower(), nativePacket.getToBlow());
            ((IJExplosion) explosion).jcraft$setModifier(modifier);
            explosion.finalizeExplosion(true);
            Objects.requireNonNull(client.player).setDeltaMovement(client.player.getDeltaMovement()
                    .add(nativePacket.getKnockbackX(), nativePacket.getKnockbackY(), nativePacket.getKnockbackZ()));
        });
    }

    private static void handleComboCounter(@NotNull Minecraft minecraftClient, FriendlyByteBuf buf) {
        JCraftClient.comboCounter = buf.readInt();
        if (JCraftClient.comboCounter == 1) {
            JCraftClient.markComboStarted();
        }

        JCraftClient.damageScaling = buf.readFloat();

        JCraftClient.framesSinceCounted = 0;
    }

    private static void handleSplatter(Minecraft client, FriendlyByteBuf buf) {
        ClientLevel world = client.level;
        if (world == null) {
            return;
        }

        Splatter splatter = JUtils.getSplatterManager(world).readSplatter(buf);

        long ageMs = splatter.getType().getMaxAge() * 50L;
        AttackHitboxEffectRenderer.addHitbox(splatter.getMainBox(), ageMs, true);
        splatter.getSections().stream()
                .filter(section -> !section.isRemoved())
                .forEach(section -> AttackHitboxEffectRenderer.addHitbox(section.getHitBox(), ageMs, true));
    }

    private static void handleStandHurt(Minecraft client, FriendlyByteBuf buf) {
        int entityId = buf.readVarInt();
        client.execute(() -> {
            if (client.level == null) {
                return;
            }

            Entity entity = client.level.getEntity(entityId);
            if (!(entity instanceof LivingEntity living)) {
                return;
            }

            // LivingEntity#handleStatus(byte) case 2, but without the sound
            //living.limbDistance = 1.5f; TODO check this
            living.invulnerableTime = 20;
            living.hurtTime = living.hurtDuration = 10;
            //living.knockbackVelocity = 0f;
        });
    }
}
