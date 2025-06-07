package net.arna.jcraft.common.spec;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.AccessLevel;
import lombok.Getter;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.api.IAttackerType;
import net.arna.jcraft.common.util.NameHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.function.Function;

public enum SpecType implements NameHolder, IAttackerType {
    NONE(player -> null, Component.empty(), Component.empty()),
    BRAWLER(BrawlerSpec::new, Component.literal("Close-range pressure and combo extension tool"), Component.literal(
            """
                    Important hitconfirm: (any stand move)~stand.OFF>Combo>stand.ON+(any stand move)""")),
    ANUBIS(AnubisSpec::new, Component.literal("Accelerating offense"), Component.literal(
            """
                    PASSIVE: Bloodlust
                    Landing blows on opponents speeds up Anubis' attacks up to 2x, with +0.2x per hit.
                    Not hitting opponents reduces Bloodlust by one stack every 4 seconds.""")),

    VAMPIRE(VampireSpec::new, Component.literal("Supernatural all-ranger"), Component.literal(
            """
                    PASSIVES: Burns in sunlight, Blood replaces hunger, Night vision
                    Excellent frametraps with Sweep or Axe Kick.
                    Bloodsuck is extremely rewarding and allows linking into almost any move."""));

    @Getter(lazy = true)
    private static final List<SpecType> allSpecTypes = ImmutableList.copyOf(values());
    @Getter(value = AccessLevel.PRIVATE, lazy = true)
    private static final Int2ObjectMap<SpecType> byId = getAllSpecTypes().stream()
            .collect(Int2ObjectOpenHashMap::new, (map, type) -> map.put(type.getOldId(), type), Int2ObjectMap::putAll);

    private final Function<LivingEntity, @Nullable JSpec<?, ?>> specCreator;
    @Getter
    private final String internalName;
    @Getter
    private final Component translatableName, description, details;

    SpecType(Function<LivingEntity, @Nullable JSpec<?, ?>> specCreator, Component description, Component details) {
        internalName = name().toLowerCase(Locale.ROOT);
        translatableName = Component.translatable("spec.jcraft." + internalName);
        this.description = description;
        this.details = details;
        this.specCreator = specCreator;
    }

    public int getOldId() {
        return ordinal();
    }

    @Override
    public ResourceLocation getId() {
        return JCraft.id(name().toLowerCase(Locale.ROOT));
    }

    @Override
    public String kind() {
        return "spec";
    }

    public JSpec<?, ?> createNew(LivingEntity livingEntity) {
        return specCreator.apply(livingEntity);
    }

    public static SpecType fromId(int id) {
        return getById().get(id);
    }

    @Override
    public Component getName() {
        return translatableName;
    }
}
