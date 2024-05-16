package net.arna.jcraft;

import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.networking.NetworkManager;
import dev.architectury.registry.registries.DeferredRegister;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import lombok.Getter;
import lombok.Setter;
import net.arna.jcraft.common.component.living.CommonCooldownsComponent;
import net.arna.jcraft.common.component.living.CommonStandComponent;
import net.arna.jcraft.common.config.JServerConfig;
import net.arna.jcraft.common.effects.DazedStatusEffect;
import net.arna.jcraft.common.entity.projectile.KnifeProjectile;
import net.arna.jcraft.common.entity.stand.StandEntity;
import net.arna.jcraft.common.entity.stand.StandType;
import net.arna.jcraft.common.gravity.config.GravityChangerConfig;
import net.arna.jcraft.common.gravity.util.GravityChannel;
import net.arna.jcraft.common.loot.JLootTableHelper;
import net.arna.jcraft.common.network.RemoteStandInteractPacket;
import net.arna.jcraft.common.network.c2s.*;
import net.arna.jcraft.common.network.s2c.ServerChannelFeedbackPacket;
import net.arna.jcraft.common.network.s2c.ShaderActivationPacket;
import net.arna.jcraft.common.network.s2c.ShaderDeactivationPacket;
import net.arna.jcraft.common.network.s2c.TimeStopStatePacket;
import net.arna.jcraft.common.tickable.JEnemies;
import net.arna.jcraft.common.tickable.PastDimensions;
import net.arna.jcraft.common.tickable.Timestops;
import net.arna.jcraft.common.util.*;
import net.arna.jcraft.platform.JComponentPlatformUtils;
import net.arna.jcraft.registry.*;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.CombatTracker;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static net.arna.jcraft.common.entity.stand.StandEntity.stun;
import static net.arna.jcraft.registry.JItemRegistry.KNIFE;

public final class JCraft {
    // Unchanging mod values
    public static final String MOD_ID = "jcraft";
    public static final int STAND_COUNT = 11;
    public static final int EVOLUTION_COUNT = 5;
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);


    public static final int SPEC_QUEUE_MOVESTUN_LIMIT = 11; // exclusive, 10 -> 0.5s window for queueing moves
    public static final int QUEUE_MOVESTUN_LIMIT = 7; // exclusive, 6 -> 0.3s window for queueing moves

    public static final GravityChangerConfig gravityConfig = new GravityChangerConfig(); // TODO incorporate this into our own config

    //Obligatory lazy Registry
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPE_REGISTRY = DeferredRegister.create(JCraft.MOD_ID, Registries.ENTITY_TYPE);
    public static final DeferredRegister<Item> ITEM_REGISTRY = DeferredRegister.create(JCraft.MOD_ID, Registries.ITEM);
    public static final DeferredRegister<Block> BLOCK_REGISTRY = DeferredRegister.create(JCraft.MOD_ID, Registries.BLOCK);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPE_REGISTRY = DeferredRegister.create(JCraft.MOD_ID, Registries.BLOCK_ENTITY_TYPE);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TAB_REGISTRY = DeferredRegister.create(MOD_ID, Registries.CREATIVE_MODE_TAB);
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(MOD_ID, Registries.PARTICLE_TYPE);
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(MOD_ID, Registries.MOB_EFFECT);
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(MOD_ID, Registries.SOUND_EVENT);
    public static final DeferredRegister<Enchantment> ENCHANTMENT = DeferredRegister.create(MOD_ID, Registries.ENCHANTMENT);

    // Gamerules
    //public static final GameRules.Key<GameRules.BooleanRule> KINGCRIMSON_TELEPORT_EFFECT = GameRuleRegistry.register("kingCrimsonTeleportEffect", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(false));
    public static final GameRules.Key<GameRules.BooleanValue> COMBO_COUNTER = GameRules.register("comboCounter", GameRules.Category.MISC, GameRules.BooleanValue.create(true));
    public static final GameRules.Key<GameRules.IntegerValue> CHANCE_MOB_SPAWNS_WITH_STAND = GameRules.register("chanceMobSpawnsWithStand", GameRules.Category.MOBS, GameRules.IntegerValue.create(5));
    public static final GameRules.Key<GameRules.BooleanValue> ALLOW_MOB_EVOLVED_STANDS = GameRules.register("allowMobEvolvedStands", GameRules.Category.MOBS, GameRules.BooleanValue.create(false));
    public static final GameRules.Key<GameRules.BooleanValue> STAND_GRIEFING = GameRules.register("standGriefing", GameRules.Category.MISC, GameRules.BooleanValue.create(true));
    public static final GameRules.Key<GameRules.BooleanValue> KEEP_STAND = GameRules.register("keepStand", GameRules.Category.MISC, GameRules.BooleanValue.create(true));
    public static final GameRules.Key<GameRules.BooleanValue> KEEP_SPEC = GameRules.register("keepSpec", GameRules.Category.MISC, GameRules.BooleanValue.create(true));
    //public static GameRules.Key<GameRules.IntRule> DAMAGE_MULT = GameRuleRegistry.register("jcraftDamageMult", GameRules.Category.MISC, GameRuleFactory.createIntRule(0, 0, 100));


    // Dimensional travel bullshit
    /**
     * Used to lock the AU chunks from being unloaded automatically by JServerTickEvents
     */
    public static int preloadLockTicks = 0;
    public static ServerLevel auWorld;
    private static final List<ChunkPos> preloadedChunks = new ArrayList<>();

    public static final Object2IntMap<LivingEntity> burstTimers = new Object2IntOpenHashMap<>();

    public static final Map<LivingEntity, DashData> dashes = new WeakHashMap<>();

    @Getter
    private static final Map<Entity, EntityInterest> entitiesOfInterest = new HashMap<>();

    // Standardized cooldowns
    public static final int dashCooldown = 40;
    public static final int LIGHT_COOLDOWN = 20;

    @Getter
    @Setter
    private static IClientEntityHandler clientEntityHandler = DummyClientEntityHandler.INSTANCE;

    public static void init() {
        GravityChannel.init();

        // Particle registration (serverside)
        JParticleTypeRegistry.initParticleTypes();
        PARTICLES.register();

        // Registration
        JEntityTypeRegistry.init();
        ENTITY_TYPE_REGISTRY.register();
        JBlockRegistry.init();
        BLOCK_REGISTRY.register();
        JItemRegistry.init();
        ITEM_REGISTRY.register();
        JBlockEntityTypeRegistry.init();
        BLOCK_ENTITY_TYPE_REGISTRY.register();
        JTagRegistry.init();
        JCreativeMenuTabRegistry.init();
        CREATIVE_TAB_REGISTRY.register();


        CommandRegistrationEvent.EVENT.register(JCommandRegistry::registerCommands);
        JEventsRegistry.registerEvents();
        JStatusRegistry.init();
        EFFECTS.register();
        JSoundRegistry.registerSounds();
        SOUNDS.register();
        JEntityTypeRegistry.registerAttributes();
        JDimensionRegistry.registerDimensions();
        JArgumentTypeRegistry.registerArgumentTypes();
        JEnchantmentRegistry.init();
        ENCHANTMENT.register();
        JLootTableHelper.init();
        JServerConfig.init();
        JStatRegistry.init();

        NetworkManager.registerReceiver(NetworkManager.Side.C2S, JPacketRegistry.C2S_PLAYER_INPUT, PlayerInputPacket::handle);
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, JPacketRegistry.C2S_PLAYER_INPUT_HOLD, PlayerInputPacket::handleHold);
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, ConfigUpdatePacket.ID, ConfigUpdatePacket::handle);
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, JPacketRegistry.C2S_STAND_BLOCK, StandBlockPacket::handle);
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, JPacketRegistry.C2S_COOLDOWN_CANCEL, CooldownCancelPacket::handle);
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, JPacketRegistry.C2S_REMOTE_STAND_INTERACT, RemoteStandInteractPacket::handle);
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, JPacketRegistry.C2S_PREDICTION_TRIGGER, PredictionTriggerPacket::handle);


        /*TODO move
        DispenserBlock.registerBehavior(KNIFE.get(), new ProjectileDispenserBehavior() {
            @Override
            protected ProjectileEntity createProjectile(World world, Position position, ItemStack stack) {
                KnifeProjectile knife = new KnifeProjectile(world);
                knife.setPosition(position.getX(), position.getY(), position.getZ());
                return knife;
            }
        });

         */
    }

    public static void markItemOfInterest(@NotNull Entity entity, @NotNull EntityInterest interest) {
        entitiesOfInterest.put(entity, interest);
    }

    /**
     * Starts tracking a timestop on the server.
     * Synchronizes with clients (upon timestop creation, not repeatedly)
     * Puts nearby players' items on cooldown.
     *
     * @param position in world
     */
    //todo: make TS stop animated textures
    public static void beginTimestop(LivingEntity timestopper, Vec3 position, ServerLevel world, int duration) {
        //JCraft.LOGGER.info(timestopper + " is stopping time in world " + world + " for " + duration + " ticks.");

        // Registration
        ResourceKey<Level> worldRegistryKey = world.dimension();
        Timestops.enqueue(new DimensionData(timestopper, position, worldRegistryKey, duration));

        // Synchronization
        FriendlyByteBuf buf = TimeStopStatePacket.createStartPacket(timestopper.getId(), position, worldRegistryKey, duration);
        world.players().forEach(playerEntity -> TimeStopStatePacket.send(playerEntity, buf)); // Sends to unaffected players because they may walk into range


        List<ServerPlayer> toStop = world.getEntitiesOfClass(ServerPlayer.class,
                new AABB(position.add(96.0, 96.0, 96.0), position.subtract(96.0, 96.0, 96.0)), EntitySelector.LIVING_ENTITY_STILL_ALIVE);

        for (ServerPlayer serverPlayer : toStop) {
            // Shader handling
            ShaderActivationPacket.send(serverPlayer, timestopper, 0, duration, ShaderActivationPacket.Type.ZA_WARUDO);
            if (serverPlayer == timestopper || serverPlayer.isCreative()) {
                continue;
            }

            // Puts all player items besides armor into cooldown for entire duration of timestop
            for (int i = 0; i < serverPlayer.getInventory().items.size(); i++) {
                serverPlayer.getCooldowns().addCooldown(serverPlayer.getInventory().items.get(i).getItem(), duration);
            }
            serverPlayer.getCooldowns().addCooldown(serverPlayer.getOffhandItem().getItem(), duration);
        }
    }

    public static void stopTimestop(Entity timestopper) {
        DimensionData timestop = Timestops.getTimestop(timestopper);
        Level world = timestopper.level();

        if (timestop == null || !(world instanceof ServerLevel serverWorld)) {
            return;
        }

        // Synchronization
        FriendlyByteBuf buf = TimeStopStatePacket.createStopPacket(timestopper.getId());
        serverWorld.players().forEach(playerEntity -> TimeStopStatePacket.send(playerEntity, buf));

        Vec3 position = timestop.pos;

        List<ServerPlayer> toUnfreeze = serverWorld.getEntitiesOfClass(ServerPlayer.class,
                new AABB(position.add(96.0, 96.0, 96.0), position.subtract(96.0, 96.0, 96.0)), EntitySelector.LIVING_ENTITY_STILL_ALIVE);

        for (ServerPlayer serverPlayer : toUnfreeze) {
            // Shader handling
            ShaderDeactivationPacket.send(serverPlayer, ShaderActivationPacket.Type.ZA_WARUDO);

            // Removes cooldowns
            for (int i = 0; i < serverPlayer.getInventory().items.size(); i++) {
                serverPlayer.getCooldowns().removeCooldown(serverPlayer.getInventory().items.get(i).getItem());
            }
            serverPlayer.getCooldowns().removeCooldown(serverPlayer.getOffhandItem().getItem());
        }

        Timestops.remove(timestop);
    }

    /**
     * Clears pre/force loaded chunks in the AU
     */
    public static void clearPreloadedChunks() {
        if (preloadedChunks.isEmpty()) {
            return;
        }
        for (ChunkPos p : preloadedChunks) {
            auWorld.setChunkForced(p.x, p.z, false);
        }
        preloadedChunks.clear();
    }

    public static void preloadChunk(ServerLevel auWorld, int chunkX, int chunkZ) {
        // Already loaded, no need to do so again.
        if (auWorld.getForcedChunks().contains(new ChunkPos(chunkX, chunkZ).toLong())) {
            return;
        }

        preloadedChunks.add(new ChunkPos(chunkX, chunkZ));
        auWorld.setChunkForced(chunkX, chunkZ, true);
    }

    public static StandEntity<?, ?> summon(Level world, LivingEntity user) {
        if (user.hasEffect(JStatusRegistry.STANDLESS.get())) {
            return null;
        }

        CommonStandComponent standData = JComponentPlatformUtils.getStandData(user);
        StandType type = standData.getType();
        if (type == StandType.NONE) {
            return null;
        }
        StandEntity<?, ?> stand = type == null ? null : type.createNew(world);

        if (stand == null) {
            return null;
        }

        int skin = standData.getSkin();
        stand.setSkin(skin);
        stand.setPos(user.position().subtract(user.getLookAngle()));
        stand.startRiding(user);
        stand.setUser(user);

        if (user instanceof ServerPlayer player) {
            if (JUtils.canAct(user) && StandBlockPacket.isBlocking(player)) {
                stand.wantToBlock = true;
                stand.tryBlock();
            }
        } else if (user instanceof Mob mob) {
            JEnemies.add(mob);
        }

        world.addFreshEntity(stand);

        standData.setStand(stand);
        return stand;
    }

    public static void createParticle(ServerLevel world, double x, double y, double z, JParticleType type) {
        if (world == null || type == null) {
            return;
        }
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());

        buf.writeShort(3);
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
        buf.writeEnum(type);

        JUtils.around(world, new Vec3(x, y, z), 128).forEach(
                serverPlayer -> ServerChannelFeedbackPacket.send(serverPlayer, buf)
        );

    }

    public static void createHitsparks(ServerLevel world, double x, double y, double z, JParticleType type, int sparkCount, double sparkSpeed) {
        if (world == null || type == null) {
            return;
        }
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());

        buf.writeShort(5);
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
        buf.writeEnum(type);
        buf.writeInt(sparkCount);
        buf.writeDouble(sparkSpeed);

        JUtils.around(world, new Vec3(x, y, z), 128).forEach(
                serverPlayer -> ServerChannelFeedbackPacket.send(serverPlayer, buf)
        );
    }

    /**
     * Breaks out of a combo using a slightly delayed attack centered at the player.
     * This attack is blockable, launches and stuns on hit.
     */
    public static void comboBreak(ServerLevel world, LivingEntity player, MobEffectInstance stun) {
        if (player.isSpectator()) {
            return;
        }
        CommonCooldownsComponent cooldowns = JComponentPlatformUtils.getCooldowns(player);

        if (stun.getDuration() > 1 && DazedStatusEffect.canBeComboBroken(stun.getAmplifier()) && cooldowns.getCooldown(CooldownType.COMBO_BREAKER) <= 0) {
            cooldowns.startCooldown(CooldownType.COMBO_BREAKER);

            stun(player, 5, 2); // Player is slowed down considerably pre-burst

            world.playSound(null, player, JSoundRegistry.COMBO_BREAK.get(), SoundSource.PLAYERS, 1, 1);

            Vec3 pPos = player.getEyePosition();
            burstTimers.put(player, 4);
            createParticle(world, pPos.x, pPos.y, pPos.z, JParticleType.COMBO_BREAK);
        }
    }

    @SuppressWarnings("unchecked")
    public static @Nullable <T extends Entity> T teleportToWorld(T e, ServerLevel w, double x, double y, double z) {
        if (!e.isRemoved()) {
            e.unRide();
            T entity = (T) e.getType().create(w);
            if (entity != null) {
                entity.restoreFrom(e);
                entity.moveTo(x, y, z, e.getYRot(), e.getXRot());
                entity.setDeltaMovement(e.getDeltaMovement());
                w.addDuringTeleport(entity);
                e.setRemoved(Entity.RemovalReason.CHANGED_DIMENSION);
                w.resetEmptyTime();
                return entity;
            }
        }
        return null;
    }

    public static void dimensionHop(LivingEntity entity, int heightOffset) {
        ServerLevel original = (ServerLevel) entity.level();
        MinecraftServer server = original.getServer();
        ServerLevel au = server.getLevel(JDimensionRegistry.AU_DIMENSION_KEY);
        if (au == null) {
            JCraft.LOGGER.fatal("Alternate universe world does not exist!");
            return;
        }
        if (original == au) {
            return;
        }

        Vec3 pos = entity.position();
        LivingEntity finalEnt = entity;

        if (entity instanceof ServerPlayer player) {
            player.teleportTo(au, pos.x, pos.y - heightOffset, pos.z, entity.getYRot(), entity.getXRot());
            player.connection.send(
                    new ClientboundSoundPacket(BuiltInRegistries.SOUND_EVENT.wrapAsHolder(JSoundRegistry.D4C_ALT_UNIVERSE_AMBIENCE.get()), SoundSource.MUSIC, pos.x, pos.y - heightOffset, pos.z, 1.0F, 1.0F, 0)
            );
        } else {
            finalEnt = teleportToWorld(entity, au, entity.getX(), entity.getY() - heightOffset, entity.getZ());
        }

        if (finalEnt == null) {
            JCraft.LOGGER.error("Failed to teleport " + entity + " to alternate universe!");
            return;
        }

        finalEnt.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 100, 9, true, false, true));
        PastDimensions.enqueue(new DimensionData(finalEnt, pos, original.dimension()));
    }

    public static boolean wasRecentlyAttacked(CombatTracker tracker) {
        tracker.recheckStatus();
        return tracker.inCombat;
    }

    public static ResourceLocation id(String name) {
        return new ResourceLocation(MOD_ID, name);
    }
}
