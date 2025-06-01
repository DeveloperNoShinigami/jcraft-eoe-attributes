package net.arna.jcraft.common.data;

import lombok.*;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;

import java.util.List;

@Getter
@Builder(toBuilder = true, builderClassName = "Builder")
@With
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@RequiredArgsConstructor(staticName = "of")
@ToString
@EqualsAndHashCode
public class StandInfo {
    /**
     * The name of this stand, used for display purposes.
     */
    private final Component name;
    /**
     * The key of the name, excluding the 'entity.&lt;modname&gt;' part, mainly for internal use (such as data-gen providers).
     * This will resolve to 'unknown' if the name is not translatable (which it should be).
     */
    @Getter(lazy = true)
    private final String nameKey = name.getContents() instanceof TranslatableContents tc ?
            tc.getKey().split("\\.", 3)[2] : "unknown";
    /**
     * The name of each of the skins available for this stand.
     * The number of entries in this list is the number of skins available for this stand.
     * If the list has more entries than the number of skins available,
     * using a skin that does not exist will likely cause a crash.
     */
    private final List<Component> skinNames;
    /**
     * The number of pros and cons of this stand.
     * Used in /stand about and whatnot.
     */
    private int proCount, conCount;
    /**
     * The free space in the stand info, used for displaying additional information.
     */
    @NonNull
    private Component freeSpace = Component.empty();

    /**
     * Returns the number of skins available for this stand.
     *
     * @return the number of skins available for this stand.
     */
    public int getSkinCount() {
        return skinNames.size();
    }
}
