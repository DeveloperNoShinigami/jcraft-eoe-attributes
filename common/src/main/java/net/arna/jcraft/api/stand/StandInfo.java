package net.arna.jcraft.api.stand;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.*;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.util.ExtraCodecs;

import java.util.List;

@Getter
@Builder(toBuilder = true, builderClassName = "Builder")
@With
@ToString
@EqualsAndHashCode
public class StandInfo {
    public static final Codec<StandInfo> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ExtraCodecs.COMPONENT.fieldOf("name").forGetter(StandInfo::getName),
            ExtraCodecs.COMPONENT.listOf().fieldOf("skin_names").forGetter(StandInfo::getSkinNames),
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("pro_count").forGetter(StandInfo::getProCount),
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("con_count").forGetter(StandInfo::getConCount),
            ExtraCodecs.COMPONENT.optionalFieldOf("free_space", Component.empty()).forGetter(StandInfo::getFreeSpace)
    ).apply(instance, StandInfo::new));

    /**
     * The name of this stand, used for display purposes.
     */
    @NonNull
    private final Component name;
    /**
     * The key of the name, mainly for internal use (such as data-gen providers).
     * This will resolve to 'unknown' if the name is not translatable (which it should be).
     */
    @Getter(lazy = true)
    private final String nameKey = name.getContents() instanceof TranslatableContents tc ? tc.getKey() : "entity.jcraft.unknown";
    /**
     * Same as {@link #getNameKey()}, but reduced to the last part of the key. (Excluding the 'entity.&lt;modid&gt;.' prefix)
     */
    @Getter(lazy = true)
    private final String reducedNameKey = getNameKey().split("\\.", 3)[2];
    /**
     * The name of each of the skins available for this stand.
     * The number of entries in this list is the number of skins available for this stand.
     * If the list has more entries than the number of skins available,
     * using a skin that does not exist will likely cause a crash.
     */
    @Singular
    @NonNull
    private List<Component> skinNames = List.of();
    /**
     * The number of pros and cons of this stand.
     * Used in /stand about and whatnot.
     */
    private int proCount, conCount;
    /**
     * The free space in the stand info, used for displaying additional information.
     */
    @NonNull
    @lombok.Builder.Default
    private Component freeSpace = Component.empty();

    public static StandInfo of(@NonNull Component name) {
        return builder().name(name).build();
    }

    /**
     * Returns the number of skins available for this stand.
     *
     * @return the number of skins available for this stand.
     */
    public int getSkinCount() {
        return skinNames.size();
    }
}
