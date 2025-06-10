package net.arna.jcraft.api.stand;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

@Getter
@Builder(toBuilder = true, builderClassName = "Builder")
@With
@ToString
@EqualsAndHashCode
public class SummonData {
    /**
     * Default summon data that plays only the generic summon sound (default behavior if no sound is specified)
     * and assumes a default animation duration of 19 ticks.
     */
    public static final SummonData EMPTY = SummonData.of(() -> null);
    public static final Codec<SummonData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            SoundEvent.CODEC.<Supplier<SoundEvent>>xmap(h -> h::value, s -> BuiltInRegistries.SOUND_EVENT.wrapAsHolder(s.get()))
                    .fieldOf("sound").forGetter(d -> d.sound),
            Codec.BOOL.optionalFieldOf("play_generic_sound", false).forGetter(SummonData::isPlayGenericSound),
            Codec.INT.optionalFieldOf("anim_duration", 19).forGetter(SummonData::getAnimDuration)
    ).apply(instance, SummonData::new));

    /**
     * The sound to play when summoning this stand.
     * This can be a supplier that returns a sound event or null if no sound should be played.
     * The supplier itself is non-null, but the sound event it returns can be null.
     * In case this supplier returns null, the generic sound will be played regardless
     * of the value of {@link #playGenericSound}.
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
    @lombok.Builder.Default
    private int animDuration = 19;

    public static SummonData of(final Supplier<@Nullable SoundEvent> sound) {
        return builder().sound(sound).build();
    }

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
