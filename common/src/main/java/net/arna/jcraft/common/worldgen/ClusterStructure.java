package net.arna.jcraft.common.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.arna.jcraft.api.registry.JStructureTypeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ClusterStructure extends Structure {

    private static boolean overlapsAny(BoundingBox candidate, BoundingBox[] existing) {
        for (BoundingBox box : existing) {
            if (box == null) continue;
            if (box.intersects(candidate)) return true;
        }
        return false;
    }

    public static final MapCodec<ClusterStructure> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            settingsCodec(inst),
            ResourceLocation.CODEC.fieldOf("start_pool").forGetter(s -> s.startPool),
            Codec.INT.optionalFieldOf("radius", 32).forGetter(s -> s.radius),
            Codec.INT.optionalFieldOf("min_satellites", 2).forGetter(s -> s.minSatellites),
            Codec.INT.optionalFieldOf("max_satellites", 5).forGetter(s -> s.maxSatellites)
            ).apply(inst, ClusterStructure::new)
    );

    private final ResourceLocation startPool;
    private final int radius;
    private final int minSatellites;
    private final int maxSatellites;

    public ClusterStructure(Structure.StructureSettings settings, ResourceLocation startPool, int radius, int minSatellites, int maxSatellites) {
        super(settings);
        this.startPool = startPool;
        this.radius = Math.max(1, radius);
        this.minSatellites = Math.max(0, minSatellites);
        this.maxSatellites = Math.max(this.minSatellites, maxSatellites);
    }

    @Override
    public StructureType<?> type() {
        return JStructureTypeRegistry.CLUSTER.get();
    }

    @Override
    protected Optional<GenerationStub> findGenerationPoint(GenerationContext ctx) {
        HolderLookup.RegistryLookup<StructureTemplatePool> poolLookup =
                ctx.registryAccess().lookupOrThrow(Registries.TEMPLATE_POOL);
        Optional<Holder.Reference<StructureTemplatePool>> optPool =
                poolLookup.get(ResourceKey.create(Registries.TEMPLATE_POOL, this.startPool));

        if (optPool.isEmpty()) return Optional.empty();

        StructureTemplatePool pool = optPool.get().value();

        // Extract only SinglePoolElements (templates). We ignore Feature/Empty/List pool elements on purpose.
        List<SinglePoolElement> allSingles = pool.templates.stream()
                .filter(e -> e instanceof SinglePoolElement)
                .map(e -> (SinglePoolElement) e)
                .collect(Collectors.toList());

        if (allSingles.isEmpty()) return Optional.empty();

        // Main = first single element in the pool
        SinglePoolElement mainElem = allSingles.get(0);

        // Candidate satellites = all others
        List<SinglePoolElement> satelliteCandidates = allSingles.subList(1, allSingles.size());
        int count = clampRandom(ctx.random(), minSatellites, maxSatellites);

        Consumer<StructurePiecesBuilder> generator = (builder) -> {
            RandomSource rand = ctx.random();
            Rotation mainRot = randomRotation(rand);

            // Place main at chunk center surface
            ChunkPos ch = ctx.chunkPos();
            int x = ch.getMiddleBlockX();
            int z = ch.getMiddleBlockZ();
            int y = ctx.chunkGenerator().getFirstOccupiedHeight(x, z, Heightmap.Types.WORLD_SURFACE_WG, ctx.heightAccessor(), ctx.randomState());

            BoundingBox[] occupied = new BoundingBox[1 + count];

            BlockPos mainPos = new BlockPos(x, y, z);
            builder.addPiece(ClusterTemplatePiece.fromPoolElement(
                    ctx.structureTemplateManager(), mainElem, mainPos, mainRot));
            BoundingBox mainBox = mainElem.getBoundingBox(ctx.structureTemplateManager(), mainPos, mainRot);
            occupied[0] = mainBox;

            // Satellites
            for (int i = 0; i < count && !satelliteCandidates.isEmpty(); i++) {
                int tries = 0;
                while (tries++ < 10) {
                    SinglePoolElement choice = satelliteCandidates.get(rand.nextInt(satelliteCandidates.size()));
                    Rotation rot = randomRotation(rand);

                    double angle = rand.nextDouble() * Math.PI * 2.0;
                    int r = (int) Math.round(rand.nextDouble() * radius);
                    int sx = x + (int) Math.round(Math.cos(angle) * r);
                    int sz = z + (int) Math.round(Math.sin(angle) * r);
                    int sy = ctx.chunkGenerator().getFirstOccupiedHeight(sx, sz, Heightmap.Types.WORLD_SURFACE_WG, ctx.heightAccessor(), ctx.randomState());

                    BlockPos sPos = new BlockPos(sx, sy, sz);
                    BoundingBox satBox = choice.getBoundingBox(ctx.structureTemplateManager(), sPos, rot);

                    if (!overlapsAny(satBox, occupied)) {
                        occupied[i + 1] = satBox; // because 0 is mainBox
                        builder.addPiece(ClusterTemplatePiece.fromPoolElement(
                                ctx.structureTemplateManager(), choice, sPos, rot));
                        break;
                    }
                }
            }
        };

        // Center on chunk & pass our piece generator
        return Structure.onTopOfChunkCenter(ctx, Heightmap.Types.WORLD_SURFACE_WG, generator);
    }

    private static int clampRandom(RandomSource rand, int min, int max) {
        if (max <= min) return min;
        return min + rand.nextInt((max - min) + 1);
    }

    private static Rotation randomRotation(RandomSource rand) {
        Rotation[] rs = Rotation.values();
        return rs[rand.nextInt(rs.length)];
    }
}