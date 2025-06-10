package net.arna.jcraft.api.stand;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.*;
import lombok.experimental.Tolerate;
import net.minecraft.network.chat.Component;

import java.util.function.UnaryOperator;

/**
 * Represents the data associated with a stand, including its idle distance,
 * rotation, block distance, and other metadata. Should be registered in the registry
 * and should be loaded from data files.
 * <p>
 * This class is immutable and can be built using the {@link Builder}.
 */
@Getter
@Builder(toBuilder = true, builderClassName = "Builder")
@With
@ToString
@EqualsAndHashCode
public class StandData {
    /**
     * Empty stand data, used when no stand data is available.
     * This is used as the fallback for when no stand data file could be found.
     * A warning is printed when this is the case.
     */
    public static final StandData EMPTY = StandData.of(StandInfo.of(Component.translatable("entity.jcraft.nostand")))
            .withObtainable(false);

    public static final Codec<StandData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.optionalFieldOf("idle_distance", 1.25f).forGetter(StandData::getIdleDistance),
            Codec.FLOAT.fieldOf("idle_rotation").forGetter(StandData::getIdleRotation),
            Codec.FLOAT.optionalFieldOf("block_distance", 0.75f).forGetter(StandData::getBlockDistance),
            Codec.BOOL.optionalFieldOf("evolution", false).forGetter(StandData::isEvolution),
            Codec.BOOL.optionalFieldOf("obtainable", true).forGetter(StandData::isObtainable),
            StandInfo.CODEC.fieldOf("info").forGetter(StandData::getInfo),
            SummonData.CODEC.optionalFieldOf("summon_data", SummonData.of(() -> null)).forGetter(StandData::getSummonData)
    ).apply(instance, StandData::new));

    /**
     * The distance at which the stand will idle from the player.
     * This is used for the idle animation and positioning.
     */
    @lombok.Builder.Default
    private float idleDistance = 1.25f;
    /**
     * The angle at which the idle position will be calculated.
     */
    @lombok.Builder.Default
    private float idleRotation = -45f;
    /**
     * The distance of the stand from the user when blocking.
     */
    @lombok.Builder.Default
    private float blockDistance = 0.75f;
    /**
     * Whether this stand is an evolution.
     */
    private boolean evolution;
    /**
     * Whether this stand is obtainable.
     */
    @lombok.Builder.Default
    private boolean obtainable = true;

    /**
     * The info of this stand, containing its name, skin names, and other metadata.
     * This is used for display purposes and to provide information about the stand.
     */
    @NonNull
    private final StandInfo info;

    /**
     * The data used when summoning this stand.
     * This includes sound effects, animation duration, and whether to play the animation.
     */
    @NonNull
    @lombok.Builder.Default
    private SummonData summonData = SummonData.of(() -> null);

    public static StandData of(final @NonNull StandInfo info) {
        return builder().info(info).build();
    }

    /**
     * The info of this stand, containing its name, skin names, and other metadata.
     * This is used for display purposes and to provide information about the stand.
     * @param newInfo The new stand info builder
     * @return The new stand data
     */
    @Tolerate
    public StandData withInfo(final UnaryOperator<StandInfo.Builder> newInfo) {
        return withInfo(newInfo.apply(this.info.toBuilder()).build());
    }
}
