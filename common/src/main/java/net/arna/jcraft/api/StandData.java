package net.arna.jcraft.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.*;
import net.minecraft.network.chat.Component;

import java.util.Collections;

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
    // TODO redo this javadoc, it is not accurate anymore
    /**
     * The NONE stand type data, used when the mob/player has no stand.
     * Different from a {@code null} stand type in that mobs with this type
     * will not get a stand at all, while mobs with a {@code null} stand type
     * may yet get a stand assigned.
     * <p>
     * For players, this and {@code null} are equivalent.
     */
    public static final StandData EMPTY = StandData.of(StandInfo.of(Component.translatable("entity.jcraft.nostand"), Collections.emptyList()));

    public static final Codec<StandData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("idle_distance").forGetter(StandData::getIdleDistance),
            Codec.FLOAT.fieldOf("idle_rotation").forGetter(StandData::getIdleRotation),
            Codec.FLOAT.fieldOf("block_distance").forGetter(StandData::getBlockDistance),
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
}
