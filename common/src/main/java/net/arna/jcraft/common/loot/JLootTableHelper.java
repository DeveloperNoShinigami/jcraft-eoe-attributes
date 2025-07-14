package net.arna.jcraft.common.loot;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import net.arna.jcraft.common.enchantments.CinderellasKissEnchantment;
import net.arna.jcraft.api.registry.JItemRegistry;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetEnchantmentsFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.BinomialDistributionGenerator;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import java.util.function.Consumer;

public class JLootTableHelper {
    public static final Multimap<ResourceLocation, Consumer<LootTable.Builder>> modifications = MultimapBuilder.hashKeys().linkedHashSetValues().build();

    public static void registerLootTables() {
        registerModification(JLootTableHelper::addMaskPool,
                new ResourceLocation("chests/abandoned_mineshaft"),
                new ResourceLocation("chests/buried_treasure"),
                new ResourceLocation("chests/end_city_treasure"),
                new ResourceLocation("chests/pillager_outpost"),
                new ResourceLocation("chests/simple_dungeon"),
                new ResourceLocation("chests/spawn_bonus_chest"),
                new ResourceLocation("chests/stronghold_library"),
                new ResourceLocation("chests/woodland_mansion")
        );
    }

    public static void registerModification(Consumer<LootTable.Builder> modifier, ResourceLocation... lootTables) {
        for (ResourceLocation lootTable : lootTables) {
            modifications.put(lootTable, modifier);
        }
    }

    public static void registerMusicDiscLootTables() {
        // Openings: Desert/Jungle Temples (pyramids)
        registerModification(JLootTableHelper::addOpeningDiscs,
                new ResourceLocation("chests/desert_pyramid"),
                new ResourceLocation("chests/jungle_temple")
        );

        // JoJo's OST: Dungeons
        registerModification(JLootTableHelper::addOSTDiscs,
                new ResourceLocation("chests/simple_dungeon")
        );

        // Endings: Ancient Cities and End Cities
        registerModification(JLootTableHelper::addEndingDiscs,
                new ResourceLocation("chests/ancient_city"),
                new ResourceLocation("chests/end_city_treasure")
        );

        // Meme discs: Creeper drops (when shot by Skeletons)
        registerModification(JLootTableHelper::addMemeDiscsToCreeper,
                new ResourceLocation("entities/creeper")
        );
    }

    // Openings: Desert Pyramids & Jungle Temples
    private static void addOpeningDiscs(LootTable.Builder builder) {
        builder.withPool(LootPool.lootPool()
                .add(LootItem.lootTableItem(JItemRegistry.DISC_SONO_CHI_NO_SADAME.get()).setWeight(1))
                .add(LootItem.lootTableItem(JItemRegistry.DISC_BLOODY_STREAM.get()).setWeight(1))
                .add(LootItem.lootTableItem(JItemRegistry.DISC_STAND_PROUD.get()).setWeight(1))
                .add(LootItem.lootTableItem(JItemRegistry.DISC_END_OF_THE_WORLD.get()).setWeight(1))
                .add(LootItem.lootTableItem(JItemRegistry.DISC_CRAZY_NOISY_BIZARRE_TOWN.get()).setWeight(1))
                .add(LootItem.lootTableItem(JItemRegistry.DISC_CHASE.get()).setWeight(1))
                .add(LootItem.lootTableItem(JItemRegistry.DISC_GREAT_DAYS.get()).setWeight(1))
                .add(LootItem.lootTableItem(JItemRegistry.DISC_FIGHTING_GOLD.get()).setWeight(1))
                .add(LootItem.lootTableItem(JItemRegistry.DISC_TRAITORS_REQUIEM.get()).setWeight(1))
                .add(LootItem.lootTableItem(JItemRegistry.DISC_STONE_OCEAN.get()).setWeight(1))
                .add(LootItem.lootTableItem(JItemRegistry.DISC_HEAVENS_FALLING_DOWN.get()).setWeight(1))
                .when(LootItemRandomChanceCondition.randomChance(0.25f))
        );
    }

    // JoJo's OST: Dungeons
    private static void addOSTDiscs(LootTable.Builder builder) {
        builder.withPool(LootPool.lootPool()
                .add(LootItem.lootTableItem(JItemRegistry.DISC_AWAKEN.get()).setWeight(1))
                .add(LootItem.lootTableItem(JItemRegistry.DISC_DARK_REBIRTH.get()).setWeight(1))
                .add(LootItem.lootTableItem(JItemRegistry.DISC_KIRA_THEME.get()).setWeight(1))
                .add(LootItem.lootTableItem(JItemRegistry.DISC_GIORNO_THEME.get()).setWeight(1))
                .add(LootItem.lootTableItem(JItemRegistry.DISC_JONATHAN_THEME.get()).setWeight(1))
                .add(LootItem.lootTableItem(JItemRegistry.DISC_JOLYNE_THEME.get()).setWeight(1))
                .add(LootItem.lootTableItem(JItemRegistry.DISC_JOTARO_THEME.get()).setWeight(1))
                .add(LootItem.lootTableItem(JItemRegistry.DISC_TORTURE_DANCE.get()).setWeight(1))
                .add(LootItem.lootTableItem(JItemRegistry.DISC_UN_ALTRA_PERSONA.get()).setWeight(1))
                .add(LootItem.lootTableItem(JItemRegistry.DISC_JOSUKE_THEME.get()).setWeight(1))
                .add(LootItem.lootTableItem(JItemRegistry.DISC_PROPAGANDA.get()).setWeight(1))
                .when(LootItemRandomChanceCondition.randomChance(0.25f))
        );
    }

    // Endings: Ancient Cities & End Cities
    private static void addEndingDiscs(LootTable.Builder builder) {
        builder.withPool(LootPool.lootPool()
                .add(LootItem.lootTableItem(JItemRegistry.DISC_TO_BE_CONTINUED.get()).setWeight(1))
                .add(LootItem.lootTableItem(JItemRegistry.DISC_WALK_LIKE_AN_EGYPTIAN.get()).setWeight(1))
                .add(LootItem.lootTableItem(JItemRegistry.DISC_OINGO_BOINGO.get()).setWeight(1))
                .add(LootItem.lootTableItem(JItemRegistry.DISC_I_WANT_YOU.get()).setWeight(1))
                .add(LootItem.lootTableItem(JItemRegistry.DISC_FREEKN_YOU.get()).setWeight(1))
                .add(LootItem.lootTableItem(JItemRegistry.DISC_MODERN_CRUSADERS.get()).setWeight(1))
                .when(LootItemRandomChanceCondition.randomChance(0.25f))
        );
    }

    // Meme discs: Creeper drops (when shot by Skeletons)
    private static void addMemeDiscsToCreeper(LootTable.Builder builder) {
        builder.withPool(LootPool.lootPool()
                .add(LootItem.lootTableItem(JItemRegistry.DISC_CRUCIFIED.get()).setWeight(1))
                .add(LootItem.lootTableItem(JItemRegistry.DISC_HALLELUJAH_CHORUS.get()).setWeight(1))
                .add(LootItem.lootTableItem(JItemRegistry.DISC_WONDER_OF_YOU.get()).setWeight(1))
                .when(LootItemKilledByPlayerCondition.killedByPlayer())
                .when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.KILLER,
                        EntityPredicate.Builder.entity().of(EntityTypeTags.SKELETONS)))
        );
    }

    private static void addMaskPool(LootTable.Builder builder) {
        builder.withPool(LootPool.lootPool()
                .add(LootItem.lootTableItem(JItemRegistry.CINDERELLA_MASK.get())
                        .setWeight(1) // 33% chance
                        .apply(new SetEnchantmentsFunction.Builder()
                                // Binomial distribution with n = 3, p = 0.4, plotter here:
                                // https://homepage.divms.uiowa.edu/~mbognar/applets/bin.html
                                // P(0) = 21.6%; P(1) = 43.2%; P(2) = 28.8%; P(3) = 6.4%
                                .withEnchantment(CinderellasKissEnchantment.INSTANCE, BinomialDistributionGenerator.binomial(3, 0.4f))))
                .add(LootItem.lootTableItem(Items.BOOK)
                        .setWeight(2) // 67% chance
                        .apply(new SetEnchantmentsFunction.Builder()
                                // Enchant with at least level 1
                                .withEnchantment(CinderellasKissEnchantment.INSTANCE, ConstantValue.exactly(1)))
                        .apply(new SetEnchantmentsFunction.Builder(true)
                                // Add up to 2 levels to the kiss enchantment
                                // n = 2, p = 0.25
                                // P(0) = 56.25%; P(1) = 37.5%; P(2) = 6.25%
                                .withEnchantment(CinderellasKissEnchantment.INSTANCE, BinomialDistributionGenerator.binomial(2, 0.25f))))
                .when(LootItemRandomChanceCondition.randomChance(0.08f)));

        builder.withPool(LootPool.lootPool()
                .add(LootItem.lootTableItem(JItemRegistry.STONE_MASK.get()))
                .when(LootItemRandomChanceCondition.randomChance(0.04f)));
    }
}