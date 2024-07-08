package net.arna.jcraft.common.entity.stand;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import lombok.Getter;
import lombok.NonNull;
import net.arna.jcraft.registry.JEntityTypeRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

// do NOT change the order in this enum, only append new stands AT THE END
public enum StandType {
    NONE(),
    STAR_PLATINUM(JEntityTypeRegistry.STAR_PLATINUM.get(), StarPlatinumEntity::new, "starplatinum",
            Component.literal("Manga"), Component.literal("Arcade"), Component.literal("OVA")),
    THE_WORLD(JEntityTypeRegistry.THE_WORLD.get(), TheWorldEntity::new, "theworld",
            Component.literal("OVA"), Component.literal("Black"), Component.literal("Greatest High")),
    KING_CRIMSON(JEntityTypeRegistry.KING_CRIMSON.get(), KingCrimsonEntity::new, "kingcrimson",
            Component.literal("Royal"), Component.literal("Manga"), Component.literal("Nightshade")),
    D4C(JEntityTypeRegistry.D4C.get(), D4CEntity::new, "d4c",
            Component.literal("Jojoveller"), Component.literal("Teaser"), Component.literal("Spangled")),
    CREAM(JEntityTypeRegistry.CREAM.get(), CreamEntity::new, "cream",
            Component.literal("Menace"), Component.literal("Eraser"), Component.literal("White Void")),
    KILLER_QUEEN(JEntityTypeRegistry.KILLER_QUEEN.get(), KillerQueenEntity::new, "killerqueen",
            Component.literal("Gunpowder"), Component.literal("Deadly"), Component.literal("1999")),
    WHITE_SNAKE(JEntityTypeRegistry.WHITE_SNAKE.get(), WhiteSnakeEntity::new, "whitesnake",
            Component.literal("Melting"), Component.literal("Mamba"), Component.literal("Redsnake")),
    SILVER_CHARIOT(JEntityTypeRegistry.SILVER_CHARIOT.get(), SilverChariotEntity::new, "silverchariot",
            Component.literal("Gold Chariot"), Component.literal("OVA"), Component.literal("Vento")),
    MAGICIANS_RED(JEntityTypeRegistry.MAGICIANS_RED.get(), MagiciansRedEntity::new, "mr",
            Component.literal("Purple"), Component.literal("OVA"), Component.literal("Fried")),
    THE_FOOL(JEntityTypeRegistry.THE_FOOL.get(), TheFoolEntity::new, "thefool",
            Component.literal("Chilled"), Component.literal("OVA"), Component.literal("Neon")),
    GOLD_EXPERIENCE(JEntityTypeRegistry.GOLD_EXPERIENCE.get(), GoldExperienceEntity::new, "goldexperience",
            Component.literal("Anime"), Component.literal("Spectre"), Component.literal("Burning Passion")),
    HIEROPHANT_GREEN(JEntityTypeRegistry.HIEROPHANT_GREEN.get(), HGEntity::new, "hierophantgreen",
            Component.literal("Cold"), Component.literal("Burning"), Component.literal("Seaside")),
    THE_SUN(JEntityTypeRegistry.THE_SUN.get(), TheSunEntity::new, "the_sun",
            Component.literal(":D"), Component.literal("Neutron Star"), Component.literal("Dark")),
    PURPLE_HAZE(JEntityTypeRegistry.PURPLE_HAZE.get(), PurpleHazeEntity::new, "purple_haze",
            Component.literal("Toxin"), Component.literal("Stopping Force"), Component.literal("Reversal")),
    C_MOON(JEntityTypeRegistry.C_MOON.get(), CMoonEntity::new, "cmoon", true,
            Component.literal("Inversion"), Component.literal("Gravity"), Component.literal("Rose")),
    MADE_IN_HEAVEN(JEntityTypeRegistry.MADE_IN_HEAVEN.get(), MadeInHeavenEntity::new, "mih", true,
            Component.literal("Brick"), Component.literal("Daft"), Component.literal("Nightmare")),
    THE_WORLD_OVER_HEAVEN(JEntityTypeRegistry.THE_WORLD_OVER_HEAVEN.get(), TheWorldOverHeavenEntity::new, "twoh", true,
            Component.literal("Shooting Star"), Component.literal("Above the Clouds"), Component.literal("Dirt to Divinity")),
    KILLER_QUEEN_BITES_THE_DUST(JEntityTypeRegistry.KILLER_QUEEN_BITES_THE_DUST.get(), KQBTDEntity::new, "kqbtd", true,
            Component.literal("Veiled"), Component.literal("Back from the Dead"), Component.literal("Garf")),
    GOLD_EXPERIENCE_REQUIEM(JEntityTypeRegistry.GER.get(), GEREntity::new, "ger", true,
            Component.literal("Silver"), Component.literal("Manga"), Component.literal("Cherry Blossom")),
    STAR_PLATINUM_THE_WORLD(JEntityTypeRegistry.SPTW.get(), SPTWEntity::new, "sptw", true,
            Component.literal("Judge, Jury, Executioner"), Component.literal("Diamond"), Component.literal("Over Heaven")),
    PURPLE_HAZE_DISTORTION(JEntityTypeRegistry.PURPLE_HAZE_DISTORTION.get(), PurpleHazeDistortionEntity::new, "purple_haze_distortion", true,
            Component.literal("Black Knight"), Component.literal("Vintage"), Component.literal("Reversal")),
    HORUS(JEntityTypeRegistry.HORUS.get(), HorusEntity::new, "horus"),
    CINDERELLA(JEntityTypeRegistry.CINDERELLA.get(), CinderellaEntity::new, "cinderella", false, false),
    OSIRIS(JEntityTypeRegistry.OSIRIS.get(), OsirisEntity::new, "osiris", false, false),
    ATUM(JEntityTypeRegistry.ATUM.get(), AtumEntity::new, "atum", false, false),
    CHARIOT_REQUIEM(JEntityTypeRegistry.CHARIOT_REQUIEM.get(), ChariotRequiemEntity::new, "chariot_requiem", false, false),
    DIVER_DOWN(JEntityTypeRegistry.DIVER_DOWN.get(), DiverDownEntity::new, "diver_down", false, false),
    DRAGONS_DREAM(JEntityTypeRegistry.DRAGONS_DREAM.get(), DragonsDreamEntity::new, "dragons_dream", false, false),
    ;

    /**
     * @deprecated only use to work with legacy IDs; this will be removed in 1.0.0
     */
    @Deprecated
    private static final Int2ObjectMap<StandType> legacyInt2TypeMap;

    static {
        legacyInt2TypeMap = new Int2ObjectArrayMap<>();
        legacyInt2TypeMap.put(255, NONE);
        legacyInt2TypeMap.put(1, STAR_PLATINUM);
        legacyInt2TypeMap.put(2, THE_WORLD);
        legacyInt2TypeMap.put(3, KING_CRIMSON);
        legacyInt2TypeMap.put(4, D4C);
        legacyInt2TypeMap.put(5, CREAM);
        legacyInt2TypeMap.put(6, KILLER_QUEEN);
        legacyInt2TypeMap.put(7, WHITE_SNAKE);
        legacyInt2TypeMap.put(8, SILVER_CHARIOT);
        legacyInt2TypeMap.put(9, MAGICIANS_RED);
        legacyInt2TypeMap.put(10, THE_FOOL);
        legacyInt2TypeMap.put(11, GOLD_EXPERIENCE);
        legacyInt2TypeMap.put(12, HIEROPHANT_GREEN);
        legacyInt2TypeMap.put(13, THE_SUN);
        legacyInt2TypeMap.put(14, PURPLE_HAZE);
        legacyInt2TypeMap.put(-1, C_MOON);
        legacyInt2TypeMap.put(-2, MADE_IN_HEAVEN);
        legacyInt2TypeMap.put(-3, THE_WORLD_OVER_HEAVEN);
        legacyInt2TypeMap.put(-4, KILLER_QUEEN_BITES_THE_DUST);
        legacyInt2TypeMap.put(-5, GOLD_EXPERIENCE_REQUIEM);
        legacyInt2TypeMap.put(-6, STAR_PLATINUM_THE_WORLD);
        legacyInt2TypeMap.put(-7, PURPLE_HAZE_DISTORTION);
    }

    @Getter(lazy = true)
    private static final List<StandType> regularStandTypes = Arrays.stream(values()).filter(t -> !t.isEvolution() && t.isObtainable()).collect(ImmutableList.toImmutableList());
    @Getter(lazy = true)
    private static final List<StandType> evoStandTypes = Arrays.stream(values()).filter(t -> t.isEvolution() && t.isObtainable()).collect(ImmutableList.toImmutableList());
    @Getter(lazy = true)
    private static final List<StandType> allStandTypes = Arrays.stream(values()).filter(StandType::isObtainable).collect(ImmutableList.toImmutableList());

    @Getter
    private static final int regularStandCount = getRegularStandTypes().size(), evoStandCount = getEvoStandTypes().size(),
            totalStandCount = regularStandCount + evoStandCount;

    @Getter(lazy = true)
    private static final Set<EntityType<? extends StandEntity<?, ?>>> entityTypes = getAllStandTypes().stream()
            .map(StandType::getEntityType)
            .collect(Collectors.toSet());

    @Getter
    private final EntityType<? extends StandEntity<?, ?>> entityType;
    @Getter
    private final boolean evolution;
    @Getter
    private final boolean obtainable;
    private final Function<Level, StandEntity<?, ?>> ctor;
    @Getter
    private final String nameKey;
    @Getter
    private final Component nameText;
    @Getter
    private final List<Component> skinNames;

    StandType(EntityType<? extends StandEntity<?, ?>> entityType, Function<Level, StandEntity<?, ?>> ctor, String nameKey, Component... skinNames) {
        this(entityType, ctor, nameKey, false, skinNames);
    }

    StandType(EntityType<? extends StandEntity<?, ?>> entityType, Function<Level, StandEntity<?, ?>> ctor, String nameKey, boolean evolution, Component... skinNames) {
        this(entityType, ctor, nameKey, evolution, true, skinNames);
    }

    StandType(EntityType<? extends StandEntity<?, ?>> entityType, Function<Level, StandEntity<?, ?>> ctor, String nameKey, boolean evolution, boolean obtainable, Component... skinNames) {
        this.entityType = entityType;
        this.ctor = ctor;
        this.nameKey = nameKey;
        this.nameText = Component.translatable("entity.jcraft." + nameKey);
        this.evolution = evolution;
        this.obtainable = obtainable;
        this.skinNames = ImmutableList.copyOf(skinNames);
    }

    StandType() {
        this.entityType = null;
        this.ctor = null;
        this.nameKey = "nostand";
        this.nameText = Component.translatable("entity.jcraft.nostand");
        this.obtainable = false;
        this.evolution = false;
        this.skinNames = List.of();
    }

    public static boolean isNone(StandType standType) {
        return standType == null || standType == NONE;
    }

    /**
     * @param internalId 255 for None, negative values for evolved stands
     * @deprecated use fromOrdinal instead
     */
    @Nullable
    @Deprecated
    private static StandType fromId(int internalId) {
        return legacyInt2TypeMap.get(internalId);
    }

    @NonNull
    public static StandType fromOrdinal(final int ordinal) {
        return values()[ordinal];
    }

    /**
     * @param id 255 for None, negative values for evolved stands
     * @deprecated use fromOrdinal instead
     */
    @Nullable
    @Deprecated
    public static StandType fromIdOrOrdinal(int id) {
        if (id < 0 || id == 255) {
            return fromId(id);
        }
        return fromOrdinal(id);
    }

    public static StandType getRandomRegular(RandomSource random) {
        return getRegularStandTypes().get(random.nextInt(regularStandCount));
    }

    @NonNull
    public StandEntity<?, ?> createNew(Level world) {
        return ctor.apply(world);
    }

    public int getSkinCount() {
        return skinNames.size();
    }
}
