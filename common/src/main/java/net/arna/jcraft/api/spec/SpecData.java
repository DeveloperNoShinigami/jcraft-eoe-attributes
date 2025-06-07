package net.arna.jcraft.api.spec;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.*;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.util.ExtraCodecs;

@Getter
@Builder(toBuilder = true, builderClassName = "Builder")
@With
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@RequiredArgsConstructor(staticName = "of")
@ToString
@EqualsAndHashCode
public class SpecData {
    public static final SpecData EMPTY = new SpecData(Component.translatable("spec.jcraft.none"));
    public static final Codec<SpecData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ExtraCodecs.COMPONENT.fieldOf("name").forGetter(SpecData::getName),
            ExtraCodecs.COMPONENT.optionalFieldOf("description", Component.empty()).forGetter(SpecData::getDescription),
            ExtraCodecs.COMPONENT.optionalFieldOf("free_space", Component.empty()).forGetter(SpecData::getDetails)
    ).apply(instance, SpecData::new));

    private final Component name;
    private Component description = Component.empty();
    private Component details = Component.empty();

    /**
     * The key of the name, mainly for internal use (such as data-gen providers).
     * This will resolve to 'unknown' if the name is not translatable (which it should be).
     */
    @Getter(lazy = true)
    private final String nameKey = name.getContents() instanceof TranslatableContents tc ? tc.getKey() : "spec.jcraft.none";
}
