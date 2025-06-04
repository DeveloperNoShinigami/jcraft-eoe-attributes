package net.arna.jcraft.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.*;
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
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@RequiredArgsConstructor(staticName = "of")
@ToString
@EqualsAndHashCode
public class StandData {
    /**
     * Empty stand data, used when no stand data is available.
     * This is used as the fallback for when no stand data file could be found.
     * A warning is printed when this is the case.
     */
    public static final StandData EMPTY = StandData.of(StandInfo.of(Component.translatable("entity.jcraft.nostand")));

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
    private float idleDistance = 1.25f;
    /**
     * The angle at which the idle position will be calculated.
     */
    private float idleRotation = -45f;
    /**
     * The distance of the stand from the user when blocking.
     */
    private float blockDistance = 0.75f;
    /**
     * Whether this stand is an evolution.
     */
    private boolean evolution;
    /**
     * Whether this stand is obtainable.
     */
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
    private SummonData summonData = SummonData.of(() -> null);

    /**
     * The info of this stand, containing its name, skin names, and other metadata.
     * This is used for display purposes and to provide information about the stand.
     * @param newInfo The new stand info
     * @return The new stand data
     */
    public StandData withInfo(StandInfo newInfo) {
        return new StandData(idleDistance, idleRotation, blockDistance, evolution, obtainable, newInfo, summonData);
    }

    /**
     * The info of this stand, containing its name, skin names, and other metadata.
     * This is used for display purposes and to provide information about the stand.
     * @param newInfo The new stand info builder
     * @return The new stand data
     */
    public StandData withInfo(UnaryOperator<StandInfo.Builder> newInfo) {
        return withInfo(newInfo.apply(this.info.toBuilder()).build());
    }
}
