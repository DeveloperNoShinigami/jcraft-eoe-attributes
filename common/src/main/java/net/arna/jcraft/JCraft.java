package net.arna.jcraft;

import com.google.common.base.Suppliers;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.networking.NetworkManager;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrarManager;
import dev.architectury.registry.registries.RegistrySupplier;
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
import net.arna.jcraft.mixin.EntityTrackerAccessor;
import net.arna.jcraft.mixin.ThreadedAnvilChunkStorageAccessor;
import net.arna.jcraft.platform.JComponentPlatformUtils;
import net.arna.jcraft.registry.*;
import net.minecraft.block.Block;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.ProjectileDispenserBehavior;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageTracker;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.EntityTrackingListener;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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
    public static DeferredRegister<EntityType<?>> ENTITY_TYPE_REGISTRY = DeferredRegister.create(JCraft.MOD_ID, RegistryKeys.ENTITY_TYPE);
    public static DeferredRegister<Item> ITEM_REGISTRY = DeferredRegister.create(JCraft.MOD_ID, RegistryKeys.ITEM);
    public static DeferredRegister<Block> BLOCK_REGISTRY = DeferredRegister.create(JCraft.MOD_ID, RegistryKeys.BLOCK);
    public static DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPE_REGISTRY = DeferredRegister.create(JCraft.MOD_ID, RegistryKeys.BLOCK_ENTITY_TYPE);

    public static final DeferredRegister<ItemGroup> TAB_REGISTER = DeferredRegister.create(MOD_ID, RegistryKeys.ITEM_GROUP);

    // Gamerules
    //public static final GameRules.Key<GameRules.BooleanRule> KINGCRIMSON_TELEPORT_EFFECT = GameRuleRegistry.register("kingCrimsonTeleportEffect", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(false));
    public static final GameRules.Key<GameRules.BooleanRule> COMBO_COUNTER = GameRules.register("comboCounter", GameRules.Category.MISC, GameRules.BooleanRule.create(true));
    public static final GameRules.Key<GameRules.IntRule> CHANCE_MOB_SPAWNS_WITH_STAND = GameRules.register("chanceMobSpawnsWithStand", GameRules.Category.MOBS, GameRules.IntRule.create(5));
    public static final GameRules.Key<GameRules.BooleanRule> ALLOW_MOB_EVOLVED_STANDS = GameRules.register("allowMobEvolvedStands", GameRules.Category.MOBS, GameRules.BooleanRule.create(false));
    public static final GameRules.Key<GameRules.BooleanRule> STAND_GRIEFING = GameRules.register("standGriefing", GameRules.Category.MISC, GameRules.BooleanRule.create(true));
    public static final GameRules.Key<GameRules.BooleanRule> KEEP_STAND = GameRules.register("keepStand", GameRules.Category.MISC, GameRules.BooleanRule.create(true));
    public static final GameRules.Key<GameRules.BooleanRule> KEEP_SPEC = GameRules.register("keepSpec", GameRules.Category.MISC, GameRules.BooleanRule.create(true));
    //public static GameRules.Key<GameRules.IntRule> DAMAGE_MULT = GameRuleRegistry.register("jcraftDamageMult", GameRules.Category.MISC, GameRuleFactory.createIntRule(0, 0, 100));


    // Dimensional travel bullshit
    /**
     * Used to lock the AU chunks from being unloaded automatically by JServerTickEvents
     */
    public static int preloadLockTicks = 0;
    public static ServerWorld auWorld;
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

        // Registration
        JEntityTypeRegistry.init();
        ENTITY_TYPE_REGISTRY.register();
        JBlockRegistry.init();
        BLOCK_REGISTRY.register();
        JItemRegistry.init();
        ITEM_REGISTRY.register();
        JBlockEntityTypeRegistry.init();
        BLOCK_ENTITY_TYPE_REGISTRY.register();


        CommandRegistrationEvent.EVENT.register(JCommandRegistry::registerCommands);
        JEventsRegistry.registerEvents();
        JStatusRegistry.registerStatuses();
        JSoundRegistry.registerSounds();
        JEntityTypeRegistry.registerAttributes();
        JDimensionRegistry.registerDimensions();
        JArgumentTypeRegistry.registerArgumentTypes();
        JEnchantmentRegistry.init();
        JLootTableHelper.init();
        JServerConfig.init();
        JStatRegistry.init();

        TAB_REGISTER.register("general", JCraft::createItemGroup);
        TAB_REGISTER.register();

        NetworkManager.registerReceiver(NetworkManager.Side.C2S, JPacketRegistry.C2S_PLAYER_INPUT, PlayerInputPacket::handle);
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, JPacketRegistry.C2S_PLAYER_INPUT_HOLD, PlayerInputPacket::handleHold);
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, ConfigUpdatePacket.ID, ConfigUpdatePacket::handle);
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, JPacketRegistry.C2S_STAND_BLOCK, StandBlockPacket::handle);
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, JPacketRegistry.C2S_COOLDOWN_CANCEL, CooldownCancelPacket::handle);
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, JPacketRegistry.C2S_REMOTE_STAND_INTERACT, RemoteStandInteractPacket::handle);
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, JPacketRegistry.C2S_PREDICTION_TRIGGER, PredictionTriggerPacket::handle);


        DispenserBlock.registerBehavior(KNIFE.get(), new ProjectileDispenserBehavior() {
            @Override
            protected ProjectileEntity createProjectile(World world, Position position, ItemStack stack) {
                KnifeProjectile knife = new KnifeProjectile(world);
                knife.setPosition(position.getX(), position.getY(), position.getZ());
                return knife;
            }
        });
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
    public static void beginTimestop(LivingEntity timestopper, Vec3d position, ServerWorld world, int duration) {
        //JCraft.LOGGER.info(timestopper + " is stopping time in world " + world + " for " + duration + " ticks.");

        // Registration
        RegistryKey<World> worldRegistryKey = world.getRegistryKey();
        Timestops.enqueue(new DimensionData(timestopper, position, worldRegistryKey, duration));

        // Synchronization
        PacketByteBuf buf = TimeStopStatePacket.createStartPacket(timestopper.getId(), position, worldRegistryKey, duration);
        world.getPlayers().forEach( playerEntity -> TimeStopStatePacket.send(playerEntity, buf)); // Sends to unaffected players because they may walk into range


        List<ServerPlayerEntity> toStop = world.getEntitiesByClass(ServerPlayerEntity.class,
                new Box(position.add(96.0, 96.0, 96.0), position.subtract(96.0, 96.0, 96.0)), EntityPredicates.VALID_LIVING_ENTITY);

        for (ServerPlayerEntity serverPlayer : toStop) {
            // Shader handling
            ShaderActivationPacket.send(serverPlayer, timestopper, 0, duration, ShaderActivationPacket.Type.ZA_WARUDO);
            if (serverPlayer == timestopper || serverPlayer.isCreative()) continue;

            // Puts all player items besides armor into cooldown for entire duration of timestop
            for (int i = 0; i < serverPlayer.getInventory().main.size(); i++)
                serverPlayer.getItemCooldownManager().set(serverPlayer.getInventory().main.get(i).getItem(), duration);
            serverPlayer.getItemCooldownManager().set(serverPlayer.getOffHandStack().getItem(), duration);
        }
    }

    public static void stopTimestop(Entity timestopper) {
        DimensionData timestop = Timestops.getTimestop(timestopper);
        World world = timestopper.getWorld();

        if (timestop == null || !(world instanceof ServerWorld serverWorld)) return;

        // Synchronization
        PacketByteBuf buf = TimeStopStatePacket.createStopPacket(timestopper.getId());
        serverWorld.getPlayers().forEach(playerEntity -> TimeStopStatePacket.send(playerEntity, buf));

        Vec3d position = timestop.pos;

        List<ServerPlayerEntity> toUnfreeze = serverWorld.getEntitiesByClass(ServerPlayerEntity.class,
                new Box(position.add(96.0, 96.0, 96.0), position.subtract(96.0, 96.0, 96.0)), EntityPredicates.VALID_LIVING_ENTITY);

        for (ServerPlayerEntity serverPlayer : toUnfreeze) {
            // Shader handling
            ShaderDeactivationPacket.send(serverPlayer, ShaderActivationPacket.Type.ZA_WARUDO);

            // Removes cooldowns
            for (int i = 0; i < serverPlayer.getInventory().main.size(); i++)
                serverPlayer.getItemCooldownManager().remove(serverPlayer.getInventory().main.get(i).getItem());
            serverPlayer.getItemCooldownManager().remove(serverPlayer.getOffHandStack().getItem());
        }

        Timestops.remove(timestop);
    }

    /**
     * Clears pre/force loaded chunks in the AU
     */
    public static void clearPreloadedChunks() {
        if (preloadedChunks.isEmpty()) return;
        for (ChunkPos p : preloadedChunks)
            auWorld.setChunkForced(p.x, p.z, false);
        preloadedChunks.clear();
    }

    public static void preloadChunk(ServerWorld auWorld, int chunkX, int chunkZ) {
        // Already loaded, no need to do so again.
        if (auWorld.getForcedChunks().contains(new ChunkPos(chunkX, chunkZ).toLong())) return;

        preloadedChunks.add(new ChunkPos(chunkX, chunkZ));
        auWorld.setChunkForced(chunkX, chunkZ, true);
    }

    public static StandEntity<?, ?> summon(World world, LivingEntity user) {
        if (user.hasStatusEffect(JStatusRegistry.STANDLESS)) return null;

        CommonStandComponent standData = JComponentPlatformUtils.getStandData(user);
        StandType type = standData.getType();
        if (type == StandType.NONE) return null;
        StandEntity<?, ?> stand = type == null ? null : type.createNew(world);

        if (stand == null) return null;

        int skin = standData.getSkin();
        stand.setSkin(skin);
        stand.setPosition(user.getPos().subtract(user.getRotationVector()));
        stand.startRiding(user);
        stand.setUser(user);

        if (user instanceof ServerPlayerEntity player) {
            if (JUtils.canAct(user) && StandBlockPacket.isBlocking(player)) {
                stand.wantToBlock = true;
                stand.tryBlock();
            }
        } else if (user instanceof MobEntity mob) {
            JEnemies.add(mob);
        }

        world.spawnEntity(stand);

        standData.setStand(stand);
        return stand;
    }

    public static void createParticle(ServerWorld world, double x, double y, double z, JParticleType type) {
        if (world == null || type == null) return;
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());

        buf.writeShort(3);
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
        buf.writeEnumConstant(type);

        JUtils.around(world, new Vec3d(x, y, z), 128).forEach(
                serverPlayer -> ServerChannelFeedbackPacket.send(serverPlayer, buf)
        );

    }

    public static void createHitsparks(ServerWorld world, double x, double y, double z, JParticleType type, int sparkCount, double sparkSpeed) {
        if (world == null || type == null) return;
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());

        buf.writeShort(5);
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
        buf.writeEnumConstant(type);
        buf.writeInt(sparkCount);
        buf.writeDouble(sparkSpeed);

        JUtils.around(world, new Vec3d(x, y, z), 128).forEach(
                serverPlayer -> ServerChannelFeedbackPacket.send(serverPlayer, buf)
        );
    }

    /**
     * Breaks out of a combo using a slightly delayed attack centered at the player.
     * This attack is blockable, launches and stuns on hit.
     */
    public static void comboBreak(ServerWorld world, LivingEntity player, StatusEffectInstance stun) {
        if (player.isSpectator()) return;
        CommonCooldownsComponent cooldowns = JComponentPlatformUtils.getCooldowns(player);

        if (stun.getDuration() > 1 && DazedStatusEffect.canBeComboBroken(stun.getAmplifier()) && cooldowns.getCooldown(CooldownType.COMBO_BREAKER) <= 0) {
            cooldowns.startCooldown(CooldownType.COMBO_BREAKER);

            stun(player, 5, 2); // Player is slowed down considerably pre-burst

            world.playSoundFromEntity(null, player, JSoundRegistry.COMBO_BREAK, SoundCategory.PLAYERS, 1, 1);

            Vec3d pPos = player.getEyePos();
            burstTimers.put(player, 4);
            createParticle(world, pPos.x, pPos.y, pPos.z, JParticleType.COMBO_BREAK);
        }
    }

    @SuppressWarnings("unchecked")
    public static @Nullable <T extends Entity> T teleportToWorld(T e, ServerWorld w, double x, double y, double z) {
        if (!e.isRemoved()) {
            e.detach();
            T entity = (T) e.getType().create(w);
            if (entity != null) {
                entity.copyFrom(e);
                entity.refreshPositionAndAngles(x, y, z, e.getYaw(), e.getPitch());
                entity.setVelocity(e.getVelocity());
                w.onDimensionChanged(entity);
                e.setRemoved(Entity.RemovalReason.CHANGED_DIMENSION);
                w.resetIdleTimeout();
                return entity;
            }
        }
        return null;
    }

    public static void dimensionHop(LivingEntity entity, int heightOffset) {
        ServerWorld original = (ServerWorld) entity.getWorld();
        MinecraftServer server = original.getServer();
        ServerWorld au = server.getWorld(JDimensionRegistry.AU_DIMENSION_KEY);
        if (au == null) {
            JCraft.LOGGER.fatal("Alternate universe world does not exist!");
            return;
        }
        if (original == au) return;

        Vec3d pos = entity.getPos();
        LivingEntity finalEnt = entity;

        if (entity instanceof ServerPlayerEntity player) {
            player.teleport(au, pos.x, pos.y - heightOffset, pos.z, entity.getYaw(), entity.getPitch());
            player.networkHandler.sendPacket(
                    new PlaySoundS2CPacket(Registries.SOUND_EVENT.getEntry(JSoundRegistry.D4C_ALT_UNIVERSE_AMBIENCE), SoundCategory.MUSIC, pos.x, pos.y - heightOffset, pos.z, 1.0F, 1.0F, 0)
            );
        } else finalEnt = teleportToWorld(entity, au, entity.getX(), entity.getY() - heightOffset, entity.getZ());

        if (finalEnt == null) {
            JCraft.LOGGER.error("Failed to teleport " + entity + " to alternate universe!");
            return;
        }

        finalEnt.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 100, 9, true, false, true));
        PastDimensions.enqueue(new DimensionData(finalEnt, pos, original.getRegistryKey()));
    }

    public static ItemGroup createItemGroup() {
        return ItemGroup.create(ItemGroup.Row.TOP, 0)
                .displayName(Text.translatable("itemGroup.jcraft.main"))
                .icon(() -> JItemRegistry.STANDARROW.get().getDefaultStack())
                .entries((displayContext, entries) -> {
                    for (Map.Entry<RegistrySupplier<Item>, Identifier> i : JItemRegistry.ITEMS.entrySet()) {
                        if (!i.getKey().get().getDefaultStack().isOf(JItemRegistry.DEBUG_WAND.get())) {
                            entries.add(i.getKey().get());
                        }
                    }
                })
                .build();
    }

    public static boolean wasRecentlyAttacked(DamageTracker tracker){
        tracker.update();
        return tracker.recentlyAttacked;
    }

    public static Identifier id(String name) {
        return new Identifier(MOD_ID, name);
    }
}
