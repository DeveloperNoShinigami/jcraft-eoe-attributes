package net.arna.jcraft.registry;

import dev.architectury.registry.level.entity.EntityAttributeRegistry;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrySupplier;
import lombok.RequiredArgsConstructor;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.block.tile.CoffinTileEntity;
import net.arna.jcraft.common.entity.*;
import net.arna.jcraft.common.entity.projectile.*;
import net.arna.jcraft.common.entity.stand.*;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.world.World;

import java.util.function.Function;

import static net.arna.jcraft.JCraft.MANAGER;

public interface JEntityTypeRegistry {

    Registrar<EntityType<?>> REGISTRY = MANAGER.get().get(Registries.ENTITY_TYPE);

    RegistrySupplier<EntityType<StarPlatinumEntity>> STAR_PLATINUM = REGISTRY.register(JCraft.id("starplatinum"),
            () -> EntityType.Builder.create(
                    WorldOnlyEntityFactory.from(StarPlatinumEntity::new),
                    SpawnGroup.CREATURE
            ).setDimensions(
                    0.6f,
                    1.8f
            ).build(null)
    );

    RegistrySupplier<EntityType<SPTWEntity>> SPTW = REGISTRY.register(JCraft.id("sptw"),
            () -> EntityType.Builder.create(
                    WorldOnlyEntityFactory.from(SPTWEntity::new),
                    SpawnGroup.CREATURE
            ).setDimensions(
                    0.6f,
                    1.8f
            ).build(null)
    );

    RegistrySupplier<EntityType<KingCrimsonEntity>> KING_CRIMSON = REGISTRY.register(JCraft.id("kingcrimson"),
            () -> EntityType.Builder.create(
                    WorldOnlyEntityFactory.from(KingCrimsonEntity::new),
                    SpawnGroup.CREATURE
            ).setDimensions(
                    0.6f,
                    1.8f
            ).build(null)
    );

    RegistrySupplier<EntityType<TheWorldEntity>> THE_WORLD = REGISTRY.register(JCraft.id("theworld"),
            () -> EntityType.Builder.create(
                    WorldOnlyEntityFactory.from(TheWorldEntity::new),
                    SpawnGroup.CREATURE
            ).setDimensions(
                    0.6f,
                    1.8f
            ).build(null)
    );

    RegistrySupplier<EntityType<D4CEntity>> D4C = REGISTRY.register(JCraft.id("d4c"),
            () -> EntityType.Builder.create(
                    WorldOnlyEntityFactory.from(D4CEntity::new),
                    SpawnGroup.CREATURE
            ).setDimensions(
                    0.6f,
                    1.8f
            ).build(null)
    );

    RegistrySupplier<EntityType<CreamEntity>> CREAM = REGISTRY.register(JCraft.id("cream"),
            () -> EntityType.Builder.create(
                    WorldOnlyEntityFactory.from(CreamEntity::new),
                    SpawnGroup.CREATURE
            ).setDimensions(
                    0.6f,
                    1.8f
            ).build(null)
    );

    RegistrySupplier<EntityType<KillerQueenEntity>> KILLER_QUEEN = REGISTRY.register(JCraft.id("killerqueen"),
            () -> EntityType.Builder.create(
                    WorldOnlyEntityFactory.from(KillerQueenEntity::new),
                    SpawnGroup.CREATURE
            ).setDimensions(
                    0.6f,
                    1.8f
            ).build(null)
    );

    RegistrySupplier<EntityType<KQBTDEntity>> KILLER_QUEEN_BITES_THE_DUST = REGISTRY.register(JCraft.id("kqbtd"),
            () -> EntityType.Builder.create(
                    WorldOnlyEntityFactory.from(KQBTDEntity::new),
                    SpawnGroup.CREATURE
            ).setDimensions(
                    0.6f,
                    1.8f
            ).build(null)
    );

    RegistrySupplier<EntityType<SheerHeartAttackEntity>> SHEER_HEART_ATTACK = REGISTRY.register(JCraft.id("sha"),
            () -> EntityType.Builder.create(
                    SheerHeartAttackEntity::new,
                    SpawnGroup.CREATURE
            ).setDimensions(
                    0.5f,
                    0.5f
            ).build(null)
    );

    RegistrySupplier<EntityType<WhiteSnakeEntity>> WHITE_SNAKE = REGISTRY.register(JCraft.id("whitesnake"),
            () -> EntityType.Builder.create(
                    WorldOnlyEntityFactory.from(WhiteSnakeEntity::new),
                    SpawnGroup.CREATURE
            ).setDimensions(0.6f, 1.8f).build(null)
    );

    RegistrySupplier<EntityType<CMoonEntity>> C_MOON = REGISTRY.register(JCraft.id("cmoon"),
            () -> EntityType.Builder.create(
                    WorldOnlyEntityFactory.from(CMoonEntity::new),
                    SpawnGroup.CREATURE
            ).setDimensions(0.6f, 1.8f).build(null)
    );

    RegistrySupplier<EntityType<MadeInHeavenEntity>> MADE_IN_HEAVEN = REGISTRY.register(JCraft.id("mih"),
            () -> EntityType.Builder.create(
                    WorldOnlyEntityFactory.from(MadeInHeavenEntity::new),
                    SpawnGroup.CREATURE
            ).setDimensions(0.6f, 2.1f).build(null)
    );

    RegistrySupplier<EntityType<TheWorldOverHeavenEntity>> THE_WORLD_OVER_HEAVEN = REGISTRY.register(JCraft.id("twoh"),
            () -> EntityType.Builder.create(
                    WorldOnlyEntityFactory.from(TheWorldOverHeavenEntity::new),
                    SpawnGroup.CREATURE
            ).setDimensions(0.6f, 1.8f).build(null)
    );

    RegistrySupplier<EntityType<SilverChariotEntity>> SILVER_CHARIOT = REGISTRY.register(JCraft.id("silverchariot"),
            () -> EntityType.Builder.create(
                    WorldOnlyEntityFactory.from(SilverChariotEntity::new),
                    SpawnGroup.CREATURE
            ).setDimensions(0.6f, 1.8f).build(null)
    );

    RegistrySupplier<EntityType<MagiciansRedEntity>> MAGICIANS_RED = REGISTRY.register(JCraft.id("mr"),
            () -> EntityType.Builder.create(
                    WorldOnlyEntityFactory.from(MagiciansRedEntity::new),
                    SpawnGroup.CREATURE
            ).setDimensions(0.6f, 1.8f).build(null)
    );

    RegistrySupplier<EntityType<TheFoolEntity>> THE_FOOL = REGISTRY.register(JCraft.id("thefool"),
            () -> EntityType.Builder.create(
                    WorldOnlyEntityFactory.from(TheFoolEntity::new),
                    SpawnGroup.CREATURE
            ).setDimensions(2f, 2f).makeFireImmune().build(null)
    );

    RegistrySupplier<EntityType<GoldExperienceEntity>> GOLD_EXPERIENCE = REGISTRY.register(JCraft.id("goldexperience"),
            () -> EntityType.Builder.create(
                    WorldOnlyEntityFactory.from(GoldExperienceEntity::new),
                    SpawnGroup.CREATURE
            ).setDimensions(0.6f, 1.8f).build(null)
    );

    RegistrySupplier<EntityType<GETreeEntity>> GE_TREE = REGISTRY.register(JCraft.id("getree"),
            () -> EntityType.Builder.create(
                    ((EntityType<GETreeEntity> type, World world) -> new GETreeEntity(type, world)),
                    SpawnGroup.CREATURE
            ).setDimensions(0.6f, 0.8f).build(null)
    );

    RegistrySupplier<EntityType<GESnakeEntity>> GE_SNAKE = REGISTRY.register(JCraft.id("gesnake"),
            () -> EntityType.Builder.create(
                    (GESnakeEntity::new),
                    SpawnGroup.CREATURE
            ).setDimensions(1f, 0.3f).build(null)
    );

    RegistrySupplier<EntityType<GEFrogEntity>> GE_FROG = REGISTRY.register(JCraft.id("gefrog"),
            () -> EntityType.Builder.create(
                    (GEFrogEntity::new),
                    SpawnGroup.CREATURE
            ).setDimensions(0.3f, 0.3f).build(null)
    );

    RegistrySupplier<EntityType<GEButterflyEntity>> GE_BUTTERFLY = REGISTRY.register(JCraft.id("gebutterfly"),
            () -> EntityType.Builder.create(
                    (GEButterflyEntity::new),
                    SpawnGroup.CREATURE
            ).setDimensions(0.3f, 0.3f).build(null)
    );


    RegistrySupplier<EntityType<HGEntity>> HIEROPHANT_GREEN = REGISTRY.register(JCraft.id("hierophant_green"),
            () -> EntityType.Builder.create(
                    WorldOnlyEntityFactory.from(HGEntity::new),
                    SpawnGroup.CREATURE
            ).setDimensions(0.6f, 1.8f).build(null)
    );

    RegistrySupplier<EntityType<TheSunEntity>> THE_SUN = REGISTRY.register(JCraft.id("the_sun"),
            () -> EntityType.Builder.create(
                    WorldOnlyEntityFactory.from(TheSunEntity::new),
                    SpawnGroup.CREATURE
            ).setDimensions(2f, 2f).build(null)
    );

    RegistrySupplier<EntityType<PurpleHazeEntity>> PURPLE_HAZE = REGISTRY.register(JCraft.id("purple_haze"),
            () -> EntityType.Builder.create(
                    WorldOnlyEntityFactory.from(PurpleHazeEntity::new),
                    SpawnGroup.CREATURE
            ).setDimensions(0.6f, 1.8f).build(null)
    );

    RegistrySupplier<EntityType<PurpleHazeDistortionEntity>> PURPLE_HAZE_DISTORTION = REGISTRY.register(JCraft.id("purple_haze_distortion"),
            () -> EntityType.Builder.create(
                    WorldOnlyEntityFactory.from(PurpleHazeDistortionEntity::new),
                    SpawnGroup.CREATURE
            ).setDimensions(0.6f, 1.8f).build(null)
    );

    RegistrySupplier<EntityType<GEREntity>> GER = REGISTRY.register(JCraft.id("ger"),
            () -> EntityType.Builder.create(
                    WorldOnlyEntityFactory.from(GEREntity::new),
                    SpawnGroup.CREATURE
            ).setDimensions(0.6f, 1.8f).build(null)
    );

    RegistrySupplier<EntityType<GERScorpionEntity>> GER_SCORPION = REGISTRY.register(JCraft.id("gerscorpion"),
            () -> EntityType.Builder.create(
                    GERScorpionEntity::new, SpawnGroup.CREATURE)
                    .setDimensions(0.4f, 0.4f)
                    .build(null)
    );

    RegistrySupplier<EntityType<PlayerCloneEntity>> PLAYER_CLONE = REGISTRY.register(JCraft.id("playerclone"),
            () -> EntityType.Builder.create(
                    (EntityType<PlayerCloneEntity> entityType, World world) -> new PlayerCloneEntity(world), SpawnGroup.CREATURE)
                    .setDimensions(0.6f, 1.8f)
                    .build(null)
    );

    // Take note of the extra <KnifeProjectile> and tracked values
    RegistrySupplier<EntityType<KnifeProjectile>> KNIFE = REGISTRY.register(JCraft.id("knife"),
            () -> EntityType.Builder.create(
                    (EntityType<KnifeProjectile> entityType, World world) -> new KnifeProjectile(world), SpawnGroup.MISC)
                    .setDimensions(0.5f, 0.5f)
                    .maxTrackingRange(6)
                    .trackingTickInterval(10)
                    .build(null)
    );

    RegistrySupplier<EntityType<EmeraldProjectile>> EMERALD = REGISTRY.register(JCraft.id("emerald"),
            () -> EntityType.Builder.create(
                            (EntityType<EmeraldProjectile> entityType, World world) -> new EmeraldProjectile(world),
                            SpawnGroup.MISC
                    ).setDimensions(0.5f, 0.5f)
                    .maxTrackingRange(6)
                    .trackingTickInterval(15)
                    .build(null)
    );

    RegistrySupplier<EntityType<BulletProjectile>> BULLET = REGISTRY.register(JCraft.id("bullet"),
            () -> EntityType.Builder.create(
                            (EntityType<BulletProjectile> entityType, World world) -> new BulletProjectile(entityType, world),
                            SpawnGroup.MISC
                    ).setDimensions(0.1f, 0.1f)
                    .maxTrackingRange(6)
                    .trackingTickInterval(10)
                    .build(null)
    );

    RegistrySupplier<EntityType<RapierProjectile>> RAPIER = REGISTRY.register(JCraft.id("rapier"),
            () -> EntityType.Builder.create(
                            (EntityType<RapierProjectile> entityType, World world) -> new RapierProjectile(entityType, world),
                            SpawnGroup.MISC
                    ).setDimensions(0.5f, 0.5f)
                    .maxTrackingRange(6)
                    .trackingTickInterval(15)
                    .build(null)
    );

    RegistrySupplier<EntityType<AnkhProjectile>> ANKH = REGISTRY.register(JCraft.id("ankh"),
            () -> EntityType.Builder.create(
                            (EntityType<AnkhProjectile> entityType, World world) -> new AnkhProjectile(entityType, world),
                            SpawnGroup.MISC
                    ).setDimensions(0.75f, 0.75f)
                    .maxTrackingRange(6)
                    .trackingTickInterval(20)
                    .build(null)
    );

    RegistrySupplier<EntityType<MeteorProjectile>> METEOR = REGISTRY.register(JCraft.id("meteor"),
            () -> EntityType.Builder.create(
                            (EntityType<MeteorProjectile> entityType, World world) -> new MeteorProjectile(entityType, world),
                            SpawnGroup.MISC
                    ).setDimensions(1.0f, 1.0f)
                    .maxTrackingRange(6)
                    .trackingTickInterval(20)
                    .build(null)
    );

    RegistrySupplier<EntityType<BubbleProjectile>> BUBBLE = REGISTRY.register(JCraft.id("bubble"),
            () -> EntityType.Builder.create(
                            (EntityType<BubbleProjectile> entityType, World world) -> new BubbleProjectile(entityType, world),
                            SpawnGroup.MISC
                    ).setDimensions(0.5f, 0.5f)
                    .maxTrackingRange(8)
                    .trackingTickInterval(20)
                    .build(null)
    );

    RegistrySupplier<EntityType<BloodProjectile>> BLOOD_PROJECTILE = REGISTRY.register(JCraft.id("bloodprojectile"),
            () -> EntityType.Builder.create(
                            (EntityType<BloodProjectile> entityType, World world) -> new BloodProjectile(entityType, world),
                            SpawnGroup.MISC
                    ).setDimensions(0.5f, 0.5f)
                    .maxTrackingRange(4)
                    .trackingTickInterval(10)
                    .build(null)
    );

    RegistrySupplier<EntityType<LaserProjectile>> LASER_PROJECTILE = REGISTRY.register(JCraft.id("laserprojectile"),
            () -> EntityType.Builder.create(
                            (EntityType<LaserProjectile> entityType, World world) -> new LaserProjectile(entityType, world),
                            SpawnGroup.MISC
                    ).setDimensions(0.5f, 0.5f)
                    .maxTrackingRange(4)
                    .trackingTickInterval(10)
                    .build(null)
    );

    RegistrySupplier<EntityType<PHCapsuleProjectile>> PH_CAPSULE = REGISTRY.register(JCraft.id("ph_capsule"),
            () -> EntityType.Builder.create(
                            (EntityType<PHCapsuleProjectile> entityType, World world) -> new PHCapsuleProjectile(world),
                            SpawnGroup.MISC
                    ).setDimensions(0.75f, 0.75f)
                    .maxTrackingRange(6)
                    .trackingTickInterval(20)
                    .build(null)
    );

    RegistrySupplier<EntityType<LifeDetectorEntity>> LIFE_DETECTOR = REGISTRY.register(JCraft.id("lifedetector"),
            () -> EntityType.Builder.create(
                            LifeDetectorEntity::new,
                            SpawnGroup.MISC
                    ).setDimensions(1f, 1f)
                    .build(null)
    );

    RegistrySupplier<EntityType<HGNetEntity>> HG_NET = REGISTRY.register(JCraft.id("hg_net"),
            () -> EntityType.Builder.create(
                            HGNetEntity::new,
                            SpawnGroup.MISC
                    ).setDimensions(2f, 4f)
                    .build(null)
    );

    RegistrySupplier<EntityType<RedBindEntity>> RED_BIND = REGISTRY.register(JCraft.id("redbind"),
            () -> EntityType.Builder.create(
                            RedBindEntity::new,
                            SpawnGroup.MISC
                    ).setDimensions(1f, 2f)
                    .build(null)
    );

    RegistrySupplier<EntityType<BlockProjectile>> BLOCK_PROJECTILE = REGISTRY.register(JCraft.id("blockprojectile"),
            () -> EntityType.Builder.create(
                            BlockProjectile::new,
                            SpawnGroup.MISC
                    ).setDimensions(0.5f, 0.5f)
                    .build(null)
    );

    RegistrySupplier<EntityType<SandTornadoEntity>> SAND_TORNADO = REGISTRY.register(JCraft.id("sandtornado"),
            () -> EntityType.Builder.create(
                            SandTornadoEntity::new,
                            SpawnGroup.MISC
                    ).setDimensions(1f, 2f)
                    .build(null)
    );

    RegistrySupplier<EntityType<WSAcidProjectile>> WS_ACID_PROJECTILE = REGISTRY.register(JCraft.id("wsacidprojectile"),
            () -> EntityType.Builder.create(
                            WorldOnlyEntityFactory.from(WSAcidProjectile::new),
                            SpawnGroup.MISC
                    ).setDimensions(0.5f, 0.5f)
                    .maxTrackingRange(4)
                    .trackingTickInterval(10)
                    .build(null)
    );

    RegistrySupplier<EntityType<SunBeamProjectile>> SUN_BEAM = REGISTRY.register(JCraft.id("sunbeam"),
            () -> EntityType.Builder.create(
                            (EntityType<SunBeamProjectile> entityType, World world) -> new SunBeamProjectile(world),
                            SpawnGroup.MISC
                    ).setDimensions(1f, 2f)
                    .build(null)
    );

    RegistrySupplier<EntityType<PurpleHazeCloudEntity>> PURPLE_HAZE_COUD = REGISTRY.register(JCraft.id("purple_haze_cloud"),
            () -> EntityType.Builder.create(
                            (EntityType<PurpleHazeCloudEntity> entityType, World world) -> new PurpleHazeCloudEntity(world),
                            SpawnGroup.MISC
                    ).setDimensions(1f, 1f)
                    .build(null)
    );

    static void registerEntities() {
        EntityAttributeRegistry.register(STAR_PLATINUM, StarPlatinumEntity::createMobAttributes);
        EntityAttributeRegistry.register(SPTW, SPTWEntity::createMobAttributes);
        EntityAttributeRegistry.register(KING_CRIMSON, KingCrimsonEntity::createMobAttributes);
        EntityAttributeRegistry.register(CREAM, CreamEntity::createMobAttributes);
        EntityAttributeRegistry.register(KILLER_QUEEN, KillerQueenEntity::createMobAttributes);
        EntityAttributeRegistry.register(KILLER_QUEEN_BITES_THE_DUST, KQBTDEntity::createMobAttributes);
        EntityAttributeRegistry.register(SHEER_HEART_ATTACK, () -> SheerHeartAttackEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_ARMOR, 20)
                .add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, 12)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.15));

        EntityAttributeRegistry.register(WHITE_SNAKE, WhiteSnakeEntity::createMobAttributes);
        EntityAttributeRegistry.register(C_MOON, CMoonEntity::createMobAttributes);

        EntityAttributeRegistry.register(MADE_IN_HEAVEN, MadeInHeavenEntity::createMobAttributes);
        EntityAttributeRegistry.register(THE_WORLD, TheWorldEntity::createMobAttributes);
        EntityAttributeRegistry.register(THE_WORLD_OVER_HEAVEN, TheWorldOverHeavenEntity::createMobAttributes);
        EntityAttributeRegistry.register(SILVER_CHARIOT, SilverChariotEntity::createMobAttributes);
        EntityAttributeRegistry.register(MAGICIANS_RED, MagiciansRedEntity::createMobAttributes);
        EntityAttributeRegistry.register(THE_FOOL, TheFoolEntity::createMobAttributes);
        EntityAttributeRegistry.register(HIEROPHANT_GREEN, HGEntity::createMobAttributes);
        EntityAttributeRegistry.register(THE_SUN, TheSunEntity::createMobAttributes);

        EntityAttributeRegistry.register(PURPLE_HAZE, () -> AbstractPurpleHazeEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.55));
        EntityAttributeRegistry.register(PURPLE_HAZE_DISTORTION, AbstractPurpleHazeEntity::createMobAttributes);
        EntityAttributeRegistry.register(GOLD_EXPERIENCE, GoldExperienceEntity::createMobAttributes);
        EntityAttributeRegistry.register(GER, GEREntity::createMobAttributes);
        EntityAttributeRegistry.register(GE_TREE, GETreeEntity::createLivingAttributes);
        EntityAttributeRegistry.register(GE_FROG, GEFrogEntity::createFrogAttributes);
        EntityAttributeRegistry.register(GE_BUTTERFLY, GEButterflyEntity::createButterflyAttributes);
        EntityAttributeRegistry.register(GE_SNAKE, () -> GESnakeEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 10)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 4)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 0));

        EntityAttributeRegistry.register(GER_SCORPION, () -> GERScorpionEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 10)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 0)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 0));
        EntityAttributeRegistry.register(D4C, D4CEntity::createMobAttributes);
        EntityAttributeRegistry.register(PLAYER_CLONE, PlayerCloneEntity::createCloneAttributes);
        EntityAttributeRegistry.register(HG_NET, HGNetEntity::createNetAttributes);
        EntityAttributeRegistry.register(LIFE_DETECTOR, LifeDetectorEntity::createDetectorAttributes);
        EntityAttributeRegistry.register(RED_BIND, RedBindEntity::createLivingAttributes);
        EntityAttributeRegistry.register(BLOCK_PROJECTILE, BlockProjectile::createBlockAttributes);
        EntityAttributeRegistry.register(SAND_TORNADO, SandTornadoEntity::createTornadoAttributes);
    }

    @RequiredArgsConstructor(staticName = "from")
    class WorldOnlyEntityFactory<T extends Entity> implements EntityType.EntityFactory<T> {
        private final Function<World, T> ctor;

        @Override
        public T create(EntityType<T> type, World world) {
            return ctor.apply(world);
        }
    }
}
