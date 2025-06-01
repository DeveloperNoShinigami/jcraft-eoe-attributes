package net.arna.jcraft.common.data;

import lombok.*;
import net.minecraft.sounds.SoundEvent;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

@Getter
@Builder(toBuilder = true, builderClassName = "Builder")
@With
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@RequiredArgsConstructor(staticName = "of")
@ToString
@EqualsAndHashCode
public class SummonData {
    /**
     * The sound to play when summoning this stand.
     * This can be a supplier that returns a sound event or null if no sound should be played.
     * The supplier itself is non-null, but the sound event it returns can be null.
     */
    @NonNull
    private Supplier<@Nullable SoundEvent> sound;
    /**
     * Whether to play a generic sound when summoning this stand.
     * If {@code true}, a generic sound will be played regardless of whether
     * {@link #sound} is null or not.
     */
    private boolean playGenericSound;
    /**
     * The duration of this stand's summoning animation in ticks.
     */
    private int animDuration = 19;

    /**
     * Returns the summoning sound of this stand.
     * This is the sound that will be played when the stand is summoned.
     *
     * @return the sound event to play, or null if
     * no sound (or only the generic one) should be played.
     */
    public @Nullable SoundEvent getSound() {
        return sound.get();
    }
}
