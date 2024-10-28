package net.arna.jcraft.common.entity;

import net.arna.jcraft.JCraft;
import net.arna.jcraft.registry.JEntityTypeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.SectionPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;

import static net.arna.jcraft.JCraft.LOGGER;

public class StandMeteorEntity extends Mob {
    private final Registry<Structure> structureRegistry;

    private static final ResourceLocation
            METEOR_LAND = JCraft.id("meteorite_land"),
            METEOR_OCEAN = JCraft.id("meteorite_ocean");

    public StandMeteorEntity(Level level) {
        super(JEntityTypeRegistry.STAND_METEOR.get(), level);

        if (level.isClientSide()) {
            structureRegistry = null;
            return;
        }

        structureRegistry = level.getServer().registryAccess().registryOrThrow(Registries.STRUCTURE);
        setDeltaMovement(random.nextDouble() - 0.5, -random.nextDouble() * 10.0, random.nextDouble() - 0.5);
    }

    @Override
    protected float getWaterSlowDown() {
        return 0.1F;
    }

    @Override
    public void tick() {
        super.tick();

        final double x = getX(), y = getY(), z = getZ();

        if (level() instanceof ServerLevel serverLevel) {
            if (onGround()) {
                level().explode(this, x, y, z, 3.0f, Level.ExplosionInteraction.TNT);
                final Structure meteorStructure = isUnderWater() ? structureRegistry.get(METEOR_OCEAN) : structureRegistry.get(METEOR_LAND);
                if (meteorStructure == null) {
                    LOGGER.error("Meteor structure was null!");
                } else {
                    placeStructure(meteorStructure, serverLevel, blockPosition());
                }
                discard();
            }
        } else {
            level().addParticle(
                    ParticleTypes.FLAME, false,
                    x + random.nextDouble() * 4 - 2.0,
                    y + random.nextDouble() * 4 - 2.0,
                    z + random.nextDouble() * 4 - 2.0,
                    getDeltaMovement().x,
                    getDeltaMovement().y,
                    getDeltaMovement().z
            );
        }
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayerSquared) { // implicitly squared, thanks Mojang :D
        return distanceToClosestPlayerSquared > 262144.0;
    }

    @Override
    public int getAirSupply() {
        return getMaxAirSupply();
    }

    private static void placeStructure(final Structure structure, final ServerLevel serverLevel, final BlockPos pos) {
        final ChunkGenerator chunkGenerator = serverLevel.getChunkSource().getGenerator();
        final StructureStart structureStart = structure.generate(
                serverLevel.registryAccess(),
                chunkGenerator,
                chunkGenerator.getBiomeSource(),
                serverLevel.getChunkSource().randomState(),
                serverLevel.getStructureManager(),
                serverLevel.getSeed(),
                new ChunkPos(pos),
                0,
                serverLevel,
                (holder) -> true);
        if (!structureStart.isValid()) {
            LOGGER.warn("Invalid StructureStart for meteor; " + structureStart);
        } else {
            final BoundingBox boundingBox = structureStart.getBoundingBox();
            final ChunkPos chunkPos1 = new ChunkPos(SectionPos.blockToSectionCoord(boundingBox.minX()), SectionPos.blockToSectionCoord(boundingBox.minZ()));
            final ChunkPos chunkPos2 = new ChunkPos(SectionPos.blockToSectionCoord(boundingBox.maxX()), SectionPos.blockToSectionCoord(boundingBox.maxZ()));
            if (!checkLoaded(serverLevel, chunkPos1, chunkPos2)) {
                return;
            }
            ChunkPos.rangeClosed(chunkPos1, chunkPos2).forEach(
                    (chunkPos) -> structureStart.placeInChunk(
                            serverLevel,
                            serverLevel.structureManager(),
                            chunkGenerator,
                            serverLevel.getRandom(),
                            new BoundingBox(
                                    chunkPos.getMinBlockX(), serverLevel.getMinBuildHeight(), chunkPos.getMinBlockZ(),
                                    chunkPos.getMaxBlockX(), serverLevel.getMaxBuildHeight(), chunkPos.getMaxBlockZ()
                            ), chunkPos
                    )
            );
        }
    }

    private static boolean checkLoaded(final ServerLevel level, final ChunkPos start, final ChunkPos end) {
        return ChunkPos.rangeClosed(start, end).allMatch((chunkPos) -> level.isLoaded(chunkPos.getWorldPosition()));
    }
}
